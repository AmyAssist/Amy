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

package de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry;

import java.util.List;

/**
 * Abstract persistent registry interface
 *
 * @author Benno Krauß
 * @param <T> the entity type
 */
public interface IRegistry<T> {

    /**
     * Get entity class at runtime
     * Can be a subclass of T
     * @return The type of the concrete entity class
     */
    Class<? extends T> getEntityClass();

    /**
     * Create an instance of the entity class
     * @return a new entity object
     */
    T createNewEntity();

    /**
     * Get all entities of this registry
     * @return all entities
     */
    List<T> getAll();

    /**
     * Get the entity with this exact id
     * @param id the primary key of the requested entity
     * @return the entity instance with this id
     */
    T getById(Object id);

    /**
     * Persist an entity
     * @param t the entity to be saved
     */
    void save(T t);

    /**
     * Delete an entity
     * @param key the primary key of the entity
     */
    void deleteById(Object key);
}
