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

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.registry.geocoder.Geocoder;
import io.github.amyassist.amy.registry.geocoder.GeocoderException;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

/**
 * Location registry implementation
 *
 * @author Benno Krauß
 */
@Service(LocationRegistry.class)
public class LocationRegistryImpl extends AbstractTaggableRegistry<Location> implements LocationRegistry {

    @Reference
    private Geocoder geocoder;

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

        if (location.getLatitude() == 0 && location.getLongitude() == 0) {
            // Extract coordinates from address by using geocoder API
            try {
                Pair<Double, Double> coordinates = this.geocoder.geocodeAddress(location.getAddressString());
                location.setLatitude(coordinates.getKey());
                location.setLongitude(coordinates.getValue());
            } catch (GeocoderException e) {
                throw new RegistryException("Could not geocode address.", e);
            }
        }

        super.save(location);
    }

    /**
     * Get the home location
     * @return entity or null
     * @throws RegistryException if there is more than one home entity
     */
    public Location getHome() {
        return getEntityWithTag(Tags.HOME);
    }

    /**
     * Get the work location
     * @return entity or null
     * @throws RegistryException if there is more than one work entity
     */
    public Location getWork() {
        return getEntityWithTag(Tags.WORK);
    }
}
