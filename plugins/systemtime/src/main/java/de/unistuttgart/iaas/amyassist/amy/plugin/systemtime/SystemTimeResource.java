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

package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Method;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Types;

/**
 * REST Resource for the system time
 * 
 * @author Muhammed Kaya
 */
@Path("systemtime")
public class SystemTimeResource implements Resource {

	@Reference
	private SystemTimeLogic logic;

	@Context
	private UriInfo info;
	
	/**
	 * get the current system time
	 * 
	 * @return current time (hour minute second) in a string
	 */
	@GET
	@Path("time")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTime() {
		return this.logic.getTime();
	}

	/**
	 * get the current system date
	 * 
	 * @return current date (day month year) in a string
	 */
	@GET
	@Path("date")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDate() {
		return this.logic.getDate();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceEntity getPluginDescripion() {
		ResourceEntity resource = new ResourceEntity();
		resource.setName("SystemTime");
		resource.setDescription("Plugin for requesting current time and date");
		resource.setMethods(this.getPluginMethods());
		resource.setLink(this.info.getBaseUriBuilder().path(SystemTimeResource.class).build());
		return resource;
	}
	
	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginMethods()
	 */
	@Override
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method[] getPluginMethods() {
		Method[] methods = new Method[2];
		methods[0] = createGetTimeMethod();
		methods[1] = createGetDateMethod();
		return methods;
	}

	/**
	 * returns the options for the time
	 * 
	 * @return a Method object containing all information
	 */
	@Path("time")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetTimeMethod() {
		Method m = new Method();
		m.setName("Time");
		m.setDescription("Returns the current time");
		m.setLink(this.info.getBaseUriBuilder().path(SystemTimeResource.class).path(SystemTimeResource.class, "getTime").build());
		m.setType(Types.GET);
		return m;
	}

	/**
	 * returns the options for the dat3e
	 * 
	 * @return a Method object containing all information
	 */
	@Path("date")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetDateMethod() {
		Method m = new Method();
		m.setName("Date");
		m.setDescription("Returns the current date");
		m.setLink(this.info.getBaseUriBuilder().path(SystemTimeResource.class).path(SystemTimeResource.class, "getDate").build());
		m.setType(Types.GET);
		return m;
	}

}
