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

package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Method;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Parameter;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Types;

/**
 * REST Resource for weather
 * 
 * @author Muhammed Kaya, Christian Bräuner
 */
@Path(WeatherResource.PATH)
public class WeatherResource implements Resource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "weather";

	@Reference
	private WeatherDarkSkyAPI weatherLogic;

	@Context
	private UriInfo info;

	/**
	 * get the weather forecast for today
	 * 
	 * @return todays weather forecast
	 */
	@GET
	@Path("today")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportDay getWeatherToday() {
		return this.weatherLogic.getReportToday();
	}

	/**
	 * get the weather forecast for tomorrow
	 * 
	 * @return tomorrows weather forecast
	 */
	@GET
	@Path("tomorrow")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportDay getWeatherTomorrow() {
		return this.weatherLogic.getReportTomorrow();
	}

	/**
	 * get the weather forecast for the week
	 * 
	 * @return this weeks weather forecast
	 */
	@GET
	@Path("week")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportWeek getWeatherWeek() {
		return this.weatherLogic.getReportWeek();
	}

	/**
	 * set a new locationId
	 * 
	 * @param locationId
	 *            id from the registry entry
	 */
	@PUT
	@Path("setLocation")
	@Consumes(MediaType.TEXT_PLAIN)
	public void setLocation(String locationId) {
		try {
			this.weatherLogic.setLocation(Integer.parseInt(locationId));
		} catch (NumberFormatException e) {
			throw new WebApplicationException("No location found.", Status.NOT_FOUND);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceEntity getPluginDescripion() {
		ResourceEntity resource = new ResourceEntity();
		resource.setName("Weather");
		resource.setDescription("Plugin for requesting the weather report of today, tomorrow and the following week");
		resource.setMethods(this.getPluginMethods());
		resource.setLink(this.info.getBaseUriBuilder().path(WeatherResource.class).build());
		return resource;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginMethods()
	 */
	@Override
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method[] getPluginMethods() {
		Method[] methods = new Method[4];
		methods[0] = createGetWeatherTodayMethod();
		methods[1] = createGetWeatherTomorrowMethod();
		methods[2] = createGetWeatherWeekMethod();
		methods[3] = createSetLocationMethod();
		return methods;
	}

	/**
	 * returns the method describing the getWeatherToday method
	 * 
	 * @return the describing method object
	 */
	@Path("today")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetWeatherTodayMethod() {
		Method today = new Method();
		today.setName("Today");
		today.setDescription("Returns the weather of today");
		today.setLink(this.info.getBaseUriBuilder().path(WeatherResource.class)
				.path(WeatherResource.class, "getWeatherToday").build());
		today.setType(Types.GET);
		return today;
	}

	/**
	 * returns the method describing the getWeatherTomorrow method
	 * 
	 * @return the describing method object
	 */
	@Path("tomorrow")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetWeatherTomorrowMethod() {
		Method tomorrow = new Method();
		tomorrow.setName("Tomorrow");
		tomorrow.setDescription("Returns the weather of tomorrow");
		tomorrow.setLink(this.info.getBaseUriBuilder().path(WeatherResource.class)
				.path(WeatherResource.class, "getWeatherTomorrow").build());
		tomorrow.setType(Types.GET);
		return tomorrow;
	}

	/**
	 * returns the method describing the getWeatherWeek method
	 * 
	 * @return the describing method object
	 */
	@Path("week")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetWeatherWeekMethod() {
		Method week = new Method();
		week.setName("Week");
		week.setDescription("Returns the weather of the following week");
		week.setLink(this.info.getBaseUriBuilder().path(WeatherResource.class)
				.path(WeatherResource.class, "getWeatherWeek").build());
		week.setType(Types.GET);
		return week;
	}

	/**
	 * returns the method describing the setLocation method
	 * 
	 * @return the describing method object
	 */
	@Path("setLocation")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createSetLocationMethod() {
		Method setLocation = new Method();
		setLocation.setName("SetLocation");
		setLocation.setDescription("Sets the ID of a Location");
		setLocation.setLink(this.info.getBaseUriBuilder().path(WeatherResource.class)
				.path(WeatherResource.class, "setLocation").build());
		setLocation.setType(Types.PUT);
		setLocation.setParameters(getSetLocationParameters());
		return setLocation;
	}

	private Parameter[] getSetLocationParameters() {
		Parameter[] params = new Parameter[1];
		// locationId
		params[0] = new Parameter();
		params[0].setName("LocationID");
		params[0].setRequired(true);
		params[0].setParamType(Types.BODY);
		params[0].setValueType(Types.STRING);
		return params;
	}

}
