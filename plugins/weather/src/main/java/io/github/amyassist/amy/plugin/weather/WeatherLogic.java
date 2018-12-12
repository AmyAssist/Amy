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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.registry.Location;
import io.github.amyassist.amy.registry.LocationRegistry;

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

	private Map<GeoCoordinatePair, WeatherReport> currentReports = new ConcurrentHashMap<>();

	/**
	 * Get the current weather report for the given location.
	 * 
	 * @param location
	 *            The location to get the report for
	 * @return The WeatherReport
	 */
	public WeatherReport getWeatherReport(GeoCoordinatePair location) {

		WeatherReport report = this.currentReports.get(location);
		if (report == null || report.isToOld()) {
			report = this.api.getWeatherReport(String.valueOf(location.getLatitude()),
					String.valueOf(location.getLongitude()));
		}

		return report;
	}

	/**
	 * A class for geo coordinates
	 * 
	 * @author Tim Neumann
	 */
	public static class GeoCoordinatePair {
		private final double longitude;
		private final double latitude;

		/**
		 * Create a new geo coordinate pair from latitude and longitude.
		 * 
		 * @param pLongitude
		 *            The longitude to use
		 * 
		 * @param pLatitude
		 *            The latitude to use
		 */
		public GeoCoordinatePair(double pLongitude, double pLatitude) {
			this.longitude = pLongitude;
			this.latitude = pLatitude;
		}

		/**
		 * Creates a new geo coordinate pair from a location.
		 * 
		 * @param loc
		 *            The location to use.
		 */
		public GeoCoordinatePair(Location loc) {
			this.longitude = loc.getLongitude();
			this.latitude = loc.getLatitude();
		}

		/**
		 * Creates a new geo coordnate pair from a string representation
		 * <p>
		 * The string should be the string representation of a double followed by semicolon followed by another double.
		 * The first double is the longitude, the second the latitude.
		 * <p>
		 * Works with {@link #getStringRepresentation()}.
		 * 
		 * @param stringRepresentation
		 *            The string representation to use.
		 * @throws IllegalArgumentException
		 *             When the given string is not a String followed by semicolon followed by String
		 * @throws NumberFormatException
		 *             When one of the strings before and after the semicolon is not a double.
		 */
		public GeoCoordinatePair(String stringRepresentation) {
			String[] parts = stringRepresentation.split(";");
			if (parts.length != 2)
				throw new IllegalArgumentException(
						"string representation should be a semicolon seperated pair of coordinats.");
			this.longitude = Double.parseDouble(parts[0]);
			this.latitude = Double.parseDouble(parts[1]);
		}

		/**
		 * Get's {@link #longitude longitude}
		 * 
		 * @return longitude
		 */
		public double getLongitude() {
			return this.longitude;
		}

		/**
		 * Get's {@link #latitude latitude}
		 * 
		 * @return latitude
		 */
		public double getLatitude() {
			return this.latitude;
		}

		/**
		 * @return The longitude followed by a semicolon followed by the latitude.
		 */
		public String getStringRepresentation() {
			return this.longitude + ";" + this.latitude;
		}
	}
}
