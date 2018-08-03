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

package de.unistuttgart.iaas.amyassist.amy.registry;

import javax.annotation.Nonnull;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * A registry with a taggable entity class. Adds the ability to search for entities with a specific tag
 * @author Benno Krauß
 * @param <T> entity class
 */
public abstract class AbstractTaggableRegistry<T extends RegistryEntity & Taggable> extends AbstractRegistry<T> implements ITaggableRegistry<T> {

    /**
     * Constants for common applications. Custom tag names are explicitly allowed
     */
    public static final String WORK = "work";
    public static final String HOME = "home";

    /**
     * Get at most one entity with tag tagValue
     * @param tagValue the tag of the requested entity
     * @return an entity or null
     * @throws RegistryException if there is more than one entity with this tag
     */
    public T getEntityWithTag(String tagValue) {
        List<? extends T> l = getEntitiesWithTag(tagValue);

        if (l.size() > 1) {
            throw new RegistryException("More than one entity with tag value " + tagValue);
        }

        if (l.isEmpty()) {
            return null;
        }

        return l.get(0);
    }


    /**
     * Get all entities with tag tagValue
     * @param tagValue the tag of the requested entities
     * @return all entities or an empty list
     */
    @SuppressWarnings("unchecked")
    public @Nonnull List<? extends T> getEntitiesWithTag(String tagValue) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = (CriteriaQuery<T>)builder.createQuery(getEntityClass());
        Root<? extends T> root = query.from(getEntityClass());

        query.select(root).where(builder.equal(root.get("tag"), tagValue));

        TypedQuery<? extends T> typedQuery = entityManager.createQuery(query);

        return typedQuery.getResultList();
    }
}
