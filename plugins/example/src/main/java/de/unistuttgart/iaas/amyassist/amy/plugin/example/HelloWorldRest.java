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

package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.api.HelloWorldService;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Method;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Types;

/**
 * Example REST Resource
 * 
 * @author Leon Kiefer, Muhammed Kaya
 */
@Path(HelloWorldRest.PATH)
public class HelloWorldRest implements Resource {
	
	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "helloworld";
	
	@Reference
	private HelloWorldService helloWorld;

	@Context
	private UriInfo info;

	/**
	 * returns hello world
	 * 
	 * @return hello world
	 */
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String helloWorld() {
		return this.helloWorld.helloWorld();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceEntity getPluginDescripion() {
		ResourceEntity resource = new ResourceEntity();
		resource.setName("HelloWorld Plugin");
		resource.setDescription(
				"This plugin serves as an example plugin to understand how the implementation for new plugins works");
		resource.setMethods(this.getPluginMethods());
		resource.setLink(this.info.getBaseUriBuilder().path(HelloWorldRest.class).build());
		return resource;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginMethods()
	 */
	@Override
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method[] getPluginMethods() {
		Method[] methods = new Method[1];
		methods[0] = createHelloWorldMethod();
		return methods;
	}

	/**
	 * returns the method describing the helloWorld method
	 * 
	 * @return the describing method object
	 */
	@Path("hello")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createHelloWorldMethod() {
		Method hello = new Method();
		hello.setName("Hello World");
		hello.setDescription("Returns hello with the number of requested requests");
		hello.setLink(this.info.getBaseUriBuilder().path(HelloWorldRest.class)
				.path(HelloWorldRest.class, "helloWorld").build());
		hello.setType(Types.GET);
		return hello;
	}
}
