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

package de.unistuttgart.iaas.amyassist.amy.core.registry;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.Persistence;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.IRegistry;
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
public abstract class AbstractRegistry<T> implements IRegistry<T> {

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
    public List<? extends T> getAll() {
        Class<? extends T> type = getEntityClass();
        return this.entityManager.createQuery("SELECT x FROM " + type.getName() + " x", type).getResultList();
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
