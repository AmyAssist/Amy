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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;

/**
 * REST Resource for weather
 * 
 * @author Muhammed Kaya, Christian Bräuner
 */
@Path("weather")
public class WeatherResource implements Resource {

	@Reference
	private WeatherDarkSkyAPI weatherLogic;

	/**
	 * get the weather forecast for today
	 * 
	 * @return todays weather forecast
	 */
	@GET
	@Path("now")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportNow getWeatherNow() {
		return this.weatherLogic.getReportNow();
	}

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
	 * set the location for weather forecast
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
			throw new WebApplicationException("No route found.", Status.NOT_FOUND);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		return null;
	}

}
