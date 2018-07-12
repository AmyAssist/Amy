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

package de.unistuttgart.iaas.amyassist.amy.registry.rest;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationImpl;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationRegistry;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Location registry rest resource
 *
 * @author Benno Krauß
 */
@Path("registry/location")
public class LocationRegistryResource implements Resource {

    @Reference
    private LocationRegistry registry;

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Location> getAll() {
        return registry.getAll();
    }

    @GET
    @Path("{id}")
    public Location getById(@PathParam("id") int id) {
        return registry.getById(id);
    }

    @DELETE
    @Path("{id}")
    public void deleteById(@PathParam("id") int id) {
        registry.deleteById(id);
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Location create(LocationImpl l) {

        Location loc = registry.createNewEntity();

        loc.setName(l.getName());
        loc.setZipCode(l.getZipCode());
        loc.setCity(l.getCity());
        loc.setStreet(l.getStreet());
        loc.setHouseNumber(l.getHouseNumber());
        loc.setLongitude(l.getLongitude());
        loc.setLatitude(l.getLatitude());
        loc.setHome(l.isHome());
        loc.setWork(l.isWork());

        registry.save(loc);

        return loc;
    }

    @Override
    public ResourceEntity getPluginDescripion() {
        return null;
    }
}
