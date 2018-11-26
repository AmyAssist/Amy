/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package io.github.amyassist.amy.registry;

import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.persistence.Persistence;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Abstract persistent registry
 *
 * @author Benno Krauß
 */
public abstract class AbstractRegistry<T extends RegistryEntity> implements IRegistry<T> {

    @Reference
    private Logger log;

    @Reference
    protected Persistence persistence;

    protected EntityManager entityManager;

    protected abstract String getPersistenceUnitName();

    /**
     * Get entity class at runtime
     * Can be a subclass of T
     * @return The type of the concrete entity class
     */
    protected abstract @Nonnull Class<? extends T> getEntityClass();

    /**
     * Create an instance of the entity class
     * @return a new entity object
     */
    public T createNewEntity() {
        try {
            return getEntityClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Error creating new entity instance in registry", e);
            return null;
        }
    }

    /**
     * Basically a constructor. But the DI-way
     */
    @PostConstruct
    private void init() {
        Class<? extends T> tClass = getEntityClass();

        this.persistence.register(tClass);
        this.entityManager = persistence.getEntityManager(getPersistenceUnitName());
    }

    /**
     * Get all entities of this registry
     * @return all entities
     */
    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        Class<? extends T> type = getEntityClass();
        // This requires an explicit cast from List<? extends T> to List<T> because Java doesn't
        // do it automatically
        return (List<T>)this.entityManager.createQuery("SELECT x FROM " + type.getName() + " x", type).getResultList();
    }

    /**
     * Get the entity with this exact id
     * @param id
     * @return the entity instance with this id
     */
    public T getById(Object id) {
        return this.entityManager.find(getEntityClass(), id);
    }

    @FunctionalInterface
    public interface TransactionBlock {
        void perform();
    }

    private void transaction(TransactionBlock block) {
        entityManager.getTransaction().begin();
        try {
            block.perform();
        } finally {
            entityManager.getTransaction().commit();
        }
    }

    /**
     * Persist an entity. The primary key will be set after invoking this method
     * @param t
     */
    public void save(T t) {
        // If the entity has been persisted before, we need to update it instead
        if (t.getPersistentId() != 0) {
            transaction(() -> entityManager.merge(t));
        } else {
            transaction(() -> entityManager.persist(t));
            transaction(() -> entityManager.detach(t));
        }
    }

    /**
     * Delete an entity
     * @param key the primary key of the entity to be removed
     */
    public void deleteById(@Nonnull Object key) {

        if (key.getClass().isAnnotationPresent(Entity.class)) {
            throw new RegistryException("The deleteById method takes the primary key as a parameter, not the entity");
        }

        transaction(() -> {
            T entity = getById(key);
            entityManager.remove(entity);
        });
    }
}
