package de.unistuttgart.iaas.amyassist.amy.core.registry;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.Persistence;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.IRegistry;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

/**
 * Abstract persistent registry
 *
 * @author Benno Krauß
 */
public abstract class Registry<T> implements IRegistry<T> {

    @Reference
    Logger log;

    @Reference
    protected Persistence persistence;

    protected EntityManager entityManager;

    protected abstract String getPersistenceUnitName();

    /**
     * Get generic Type T at runtime.
     * This is a safe operation because this class is abstract and only subclasses can be instantiated
     * @return The type T of this class
     */
    @SuppressWarnings("unchecked")
    private @Nonnull Class<T> getGenericType() {
        try {
            Type superClass = this.getClass().getGenericSuperclass();
            Type tType = ((ParameterizedType)superClass).getActualTypeArguments()[0];
            return (Class<T>)Class.forName(tType.getTypeName());
        } catch (ClassNotFoundException | ClassCastException e) {
            log.error("Fatal error in Registry. Unable to determine the generic class", e);
            return null;
        }
    }

    /**
     * Basically a constructor. But the DI-way
     */
    @PostConstruct
    private void init() {
        Class<T> tClass = getGenericType();

        this.persistence.register(tClass);
        this.entityManager = persistence.getEntityManager(getPersistenceUnitName());
    }

    /**
     * Get all entities of this registry
     * @return all entities
     */
    public List<T> getAll() {
        Class<T> type = getGenericType();
        return this.entityManager.createQuery("SELECT x FROM " + type.getName() + " x", type).getResultList();
    }

    /**
     * Get the entity with this exact id
     * @param id
     * @return the entity instance with this id
     */
    public T getById(Object id) {
        return this.entityManager.find(getGenericType(), id);
    }

    @FunctionalInterface
    public interface TransactionBlock {
        void perform();
    }

    private void transaction(TransactionBlock block) {
        entityManager.getTransaction().begin();
        block.perform();
        entityManager.getTransaction().commit();
    }

    /**
     * Persist an entity. The primary key will be set after invoking this method
     * @param t
     */
    public void save(T t) {
        transaction(() -> entityManager.persist(t));
        transaction(() -> entityManager.detach(t));
    }

    /**
     * Delete an entity
     * @param key the primary key of the entity to be removed
     */
    public void deleteById(@Nonnull Object key) {

        if (key.getClass().isAnnotationPresent(Entity.class)) {
            throw new RuntimeException("The deleteById method takes the primary key as a parameter, not the entity itself");
        }

        transaction(() -> {
            T entity = getById(key);
            entityManager.remove(entity);
        });
    }
}
