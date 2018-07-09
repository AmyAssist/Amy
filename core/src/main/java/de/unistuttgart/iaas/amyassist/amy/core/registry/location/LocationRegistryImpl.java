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

package de.unistuttgart.iaas.amyassist.amy.core.registry.location;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.LocationRegistry;
import de.unistuttgart.iaas.amyassist.amy.core.registry.AbstractRegistry;
import de.unistuttgart.iaas.amyassist.amy.core.registry.RegistryException;

import javax.annotation.Nonnull;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Location registry implementation
 *
 * @author Benno Krauß
 */
@Service(LocationRegistry.class)
public class LocationRegistryImpl extends AbstractRegistry<Location> implements LocationRegistry {
    @Override
    protected String getPersistenceUnitName() {
        return "LocationRegistry";
    }

    @Nonnull
    @Override
    protected Class<? extends LocationImpl> getEntityClass() {
        return LocationImpl.class;
    }

    @Override
    public void save(Location location) {

        if (location.isHome() || location.isWork()) {
            List<? extends Location> locs = getAll();
            boolean hasHome = locs.stream().anyMatch(Location::isHome);
            boolean hasWork = locs.stream().anyMatch(Location::isWork);

            if (hasHome && location.isHome() || hasWork && location.isWork()) {
                throw new RegistryException("There can only be at most one work and one home entity");
            }
        }

        super.save(location);
    }

    /**
     * Get at most one entity where the attribute is set to true
     * @param attribute
     * @return an entity of null
     */
    private LocationImpl getEntityWithBooleanAttribute(String attribute) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LocationImpl> query = builder.createQuery(LocationImpl.class);
        Root<LocationImpl> root = query.from(LocationImpl.class);
        query.select(root).where(builder.equal(root.get(attribute), true));

        TypedQuery<LocationImpl> typedQuery = entityManager.createQuery(query);

        List<LocationImpl> homes = typedQuery.getResultList();
        if (!homes.isEmpty()) {
            return homes.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Get the home location
     * @return entity or null
     */
    public LocationImpl getHome() {
        return getEntityWithBooleanAttribute("isHome");
    }

    /**
     * Get the work location
     * @return entity or null
     */
    public LocationImpl getWork() {
        return getEntityWithBooleanAttribute("isWork");
    }
}
