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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationRegistry;

/**
 * Logic class to provide information about current and coming weather
 * 
 * @author Benno Krauß, Lars Buttgereit, Muhammed Kaya, Tim Neumann
 */
@Service
public class WeatherLogic {
	@Reference
	private LocationRegistry locationRegistry;
	@Reference
	private WeatherDarkSkyAdapter api;

	private Map<Location, WeatherReport> currentReports = new ConcurrentHashMap<>();

	/**
	 * Get the current weather report for the location, that has the given id in the location registry.
	 * 
	 * @param locationId
	 *            The id of the location to get the report for
	 * @return The WeatherReport
	 * @throws IllegalArgumentException
	 *             If the given id does not correspond to a location in the registry.
	 */
	public WeatherReport getWeatherReport(int locationId) {
		Location loc = this.locationRegistry.getById(locationId);
		if (loc == null)
			throw new IllegalArgumentException("Not a valid location id");
		WeatherReport report = this.currentReports.get(loc);
		if (report == null || report.isToOld()) {
			report = this.api.getWeatherReport(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()));
		}

		return report;
	}
}
