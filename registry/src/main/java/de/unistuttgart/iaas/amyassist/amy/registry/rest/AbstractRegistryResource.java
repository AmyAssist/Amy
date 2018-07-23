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
import de.unistuttgart.iaas.amyassist.amy.registry.IRegistry;
import de.unistuttgart.iaas.amyassist.amy.registry.RegistryException;
import de.unistuttgart.iaas.amyassist.amy.registry.RegistryInvalidRequestException;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Abstract registry rest resource.
 * This class can be used as a template to create a rest-resource with basic functionality for any registry.
 * Take a look at
 * {@link de.unistuttgart.iaas.amyassist.amy.registry.rest.ContactRegistryResource ContactRegistryResource }
 * for an example.
 *
 * This class is intentionally public. Otherwise Jersey throws some errors because it can't access this class
 *
 * @author Benno Krauß
 * @param <R> Registry interface
 * @param <E> Entity interface
 * @param <I> Entity implementation class
 * @param <P> Primary key class (must fit primary key of entity class)
 */
public abstract class AbstractRegistryResource<R extends IRegistry<E>, E, I extends E, P> implements Resource {

    @Reference
    protected R registry;

    @GET
    @Path("all")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<E> getAll() {
        return handleErrors(() -> registry.getAll());
    }

    /**
     * Get an entity by its id
     * @param id primary key value
     * @return the entity
     */
    @GET
    @Path("{id : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public E getById(@PathParam("id") P id) {
        return handleErrors(() -> registry.getById(id));
    }

    /**
     * Delete an entity
     * @param id the primary key
     */
    @DELETE
    @Path("{id : \\d+}")
    public void deleteById(@PathParam("id") P id) {
        handleErrors(() -> {
            registry.deleteById(id);
            return null;
        });
    }

    /**
     * create a new entity
     * "@Path" annotation is left out because an empty path results in a warning (same as no annotation)
     * @param l the location to be persisted
     * @return the newly created entity with the primary key set
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public E create(I l) {
        return handleErrors(() -> {
            registry.save(l);
            return l;
        });
    }

    @Override
    public ResourceEntity getPluginDescripion() {
        return null;
    }

    @FunctionalInterface
    private interface RegistryLambda<T> {
        /**
         * Do some work. This may throw a registry exception
         * @throws RegistryException any registry exception
         * @return any value
         */
        T perform();
    }

    /**
     * Perform registry operations in a safe manner. When a RegistryException is thrown,
     * it'll be rethrown as an appropriate WebApplicationException
     * @param lambda the block to be executed
     * @param <T> the return type
     * @return the block's return value
     * @throws WebApplicationException any rethrown RegistryException
     */
    private <T> T handleErrors(RegistryLambda<T> lambda) {
        try {
            return lambda.perform();
        } catch (RegistryInvalidRequestException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
        } catch (RegistryException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
