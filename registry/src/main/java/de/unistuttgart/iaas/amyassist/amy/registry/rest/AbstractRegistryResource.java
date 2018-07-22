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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.registry.IRegistry;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Method;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Parameter;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Types;

/**
 * Abstract registry rest resource. This class can be used as a template to create a rest-resource with basic
 * functionality for any registry. Take a look at
 * {@link de.unistuttgart.iaas.amyassist.amy.registry.rest.ContactRegistryResource ContactRegistryResource } for an
 * example.
 *
 * This class is intentionally public. Otherwise Jersey throws some errors because it can't access this class
 *
 * @author Benno Krauß, Muhammed Kaya
 * @param <R>
 *            Registry interface
 * @param <E>
 *            Entity interface
 * @param <I>
 *            Entity implementation class
 * @param <P>
 *            Primary key class (must fit primary key of entity class)
 */
public abstract class AbstractRegistryResource<R extends IRegistry<E>, E, I extends E, P> implements Resource {

	@Reference
	protected R registry;

	@Context
	private UriInfo info;

	@GET
	@Path("all")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public List<E> getAll() {
		return registry.getAll();
	}

	/**
	 * Get an entity by its id
	 * 
	 * @param id
	 *            primary key value
	 * @return the entity
	 */
	@GET
	@Path("{id : \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public E getById(@PathParam("id") P id) {
		return registry.getById(id);
	}

	/**
	 * Delete an entity
	 * 
	 * @param id
	 *            the primary key
	 */
	@DELETE
	@Path("{id : \\d+}")
	public void deleteById(@PathParam("id") P id) {
		registry.deleteById(id);
	}

	/**
	 * create a new entity "@Path" annotation is left out because an empty path results in a warning (same as no
	 * annotation)
	 * 
	 * @param l
	 *            the location to be persisted
	 * @return the newly created entity with the primary key set
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public E create(I l) {
		registry.save(l);
		return l;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		ResourceEntity resource = new ResourceEntity();
		resource.setName("Abstract Registry Resource");
		resource.setDescription("This class can be used as a template to create a rest-resource with basic "
				+ "functionality for any registry");
		resource.setMethods(this.getPluginMethods());
		resource.setLink(this.info.getBaseUriBuilder().path(AbstractRegistryResource.class).build());
		return resource;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginMethods()
	 */
	@Override
	public Method[] getPluginMethods() {
		Method[] methods = new Method[4];
		methods[0] = createGetAllMethod();
		methods[1] = createGetByIdMethod();
		methods[2] = createDeleteByIdMethod();
		methods[3] = createCreateMethod();
		return methods;
	}

	/**
	 * returns the method describing the getAll method
	 * 
	 * @return the describing method object
	 */
	@Path("all")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetAllMethod() {
		Method all = new Method();
		all.setName("Get all");
		all.setDescription("Returns all entities");
		all.setLink(this.info.getBaseUriBuilder().path(AbstractRegistryResource.class)
				.path(AbstractRegistryResource.class, "getAll").build());
		all.setType(Types.GET);
		return all;
	}

	/**
	 * returns the method describing the getById method
	 * 
	 * @return the describing method object
	 */
	@Path("{id : \\d+}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetByIdMethod() {
		Method get = new Method();
		get.setName("Get by ID");
		get.setDescription("Returns entities by ID");
		get.setLink(this.info.getBaseUriBuilder().path(AbstractRegistryResource.class)
				.path(AbstractRegistryResource.class, "getById").build());
		get.setType(Types.GET);
		get.setParameters(getByIdParameters());
		return get;
	}

	/**
	 * returns the method describing the deleteById method
	 * 
	 * @return the describing method object
	 */
	@Path("{id : \\d+}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createDeleteByIdMethod() {
		Method delete = new Method();
		delete.setName("Delete by ID");
		delete.setDescription("Deletes entities by ID");
		delete.setLink(this.info.getBaseUriBuilder().path(AbstractRegistryResource.class)
				.path(AbstractRegistryResource.class, "deleteById").build());
		delete.setType(Types.DELETE);
		delete.setParameters(getByIdParameters());
		return delete;
	}

	/**
	 * returns the method describing the create method
	 * 
	 * @return the describing method object
	 */
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createCreateMethod() {
		Method create = new Method();
		create.setName("Create");
		create.setDescription("Creates a new entity @Path annotation is left out because an empty path results in a "
				+ "warning (same as no\r\n annotation)");
		create.setLink(this.info.getBaseUriBuilder().path(AbstractRegistryResource.class)
				.path(AbstractRegistryResource.class, "create").build());
		create.setType(Types.POST);
		create.setParameters(getCreateParameters());
		return create;
	}

	private Parameter[] getByIdParameters() {
		Parameter[] params = new Parameter[1];
		// id
		params[0] = new Parameter();
		params[0].setName("ID");
		params[0].setRequired(true);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.STRING);
		return params;
	}

	private Parameter[] getCreateParameters() {
		Parameter[] params = new Parameter[1];
		// l
		params[0] = new Parameter();
		params[0].setName("l");
		params[0].setRequired(true);
		params[0].setParamType(Types.BODY);
		params[0].setValueType(Types.BODY);
		return params;
	}

}
