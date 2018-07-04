package de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry;

import java.util.List;

/**
 * Abstract persistent registry interface
 *
 * @author Benno Krauß
 */
public interface IRegistry<T> {

    /**
     * Get all entities of this registry
     * @return all entities
     */
    public List<T> getAll();

    /**
     * Get the entity with this exact id
     * @param id
     * @return the entity instance with this id
     */
    public T getById(Object id);

    /**
     * Persist an entity
     * @param t
     */
    public void save(T t);

    /**
     * Delete an entity
     * @param t
     */
    public void delete(T t);
}
