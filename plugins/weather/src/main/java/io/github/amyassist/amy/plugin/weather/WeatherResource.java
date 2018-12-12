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

package io.github.amyassist.amy.plugin.weather;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.plugin.weather.WeatherLogic.GeoCoordinatePair;
import io.github.amyassist.amy.registry.Location;
import io.github.amyassist.amy.registry.LocationRegistry;
import io.github.amyassist.amy.utility.rest.Resource;
import io.github.amyassist.amy.utility.rest.ResourceEntity;

/**
 * REST Resource for weather
 * 
 * @author Muhammed Kaya, Christian Bräuner, Tim Neumann
 */
@Path("weather")
public class WeatherResource implements Resource {

	@Reference
	private WeatherLogic weatherLogic;

	@Reference
	private LocationRegistry registry;

	/**
	 * get the weather report
	 * 
	 * @param locationId
	 *            The id of the location to get the report for.
	 * 
	 * @return The weather report
	 */
	@GET
	@Path("report")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReport getWeatherReport(@QueryParam("id") int locationId) {
		Location loc = this.registry.getById(locationId);
		if (loc == null)
			throw new WebApplicationException(400);
		return this.weatherLogic.getWeatherReport(new GeoCoordinatePair(loc));
	}

	/**
	 * @see io.github.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		return null;
	}

}
