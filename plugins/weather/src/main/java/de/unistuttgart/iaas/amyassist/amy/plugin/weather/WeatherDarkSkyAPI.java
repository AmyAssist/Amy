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

import java.util.Calendar;
import java.util.Properties;

import com.github.dvdme.ForecastIOLib.FIODaily;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.WithDefault;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationRegistry;

/**
 * Logic class to provide information about current and coming weather
 * 
 * @author Benno, Lars Buttgereit
 */
@Service
public class WeatherDarkSkyAPI {
	@WithDefault
	@Reference
	private Properties configuration;

	@Reference
	private LocationRegistry locationRegistry;

	@Reference
	private IStorage storage;

	private boolean locationChanged = true;

	private String coordinateLat = "48.745886";
	private String coordinateLong = "9.107881";

	private static final String API_SECRET_CONFIG_KEY = "DARKSKY_API_SECRET";
	private static final String WEATHER_LOCATION_ID_STRING = "WEATHER_LOCATION_ID";

	private FIODaily dailyReports;
	private Calendar lastRequest;

	private String apiSecret;

	@PostConstruct
	private void checkAPIKey() {
		this.apiSecret = this.configuration.getProperty(API_SECRET_CONFIG_KEY);
		if (this.apiSecret.isEmpty()) {
			throw new IllegalStateException(
					"DarkSky API SECRET is missing. Set it in the config of the plugin with the key '"
							+ API_SECRET_CONFIG_KEY + "'.");
		}
	}

	private FIODaily getDailyReports() {
		if (this.dailyReports == null || checkTime() || this.locationChanged) {
			loadLocation();
			ForecastIO fio = new ForecastIO(this.apiSecret);
			fio.setUnits(ForecastIO.UNITS_SI);
			fio.getForecast(this.coordinateLat, this.coordinateLong);

			this.dailyReports = new FIODaily(fio);
			for (int i = 0; i < this.dailyReports.days(); i++) {
				FIODataPoint report = this.dailyReports.getDay(i);
				report.setTimezone(fio.getTimezone());
			}
			this.lastRequest = Calendar.getInstance();
			this.locationChanged = false;
		}

		return this.dailyReports;
	}

	/**
	 * checks if the time of the last request is still actual and on the same day
	 * 
	 * @return true if the data is outdated, false if its actual
	 */
	private boolean checkTime() {
		Calendar now = Calendar.getInstance();
		if (this.lastRequest != null) {
			boolean sameDay = now.get(Calendar.YEAR) == this.lastRequest.get(Calendar.YEAR)
					&& now.get(Calendar.DAY_OF_YEAR) == this.lastRequest.get(Calendar.DAY_OF_YEAR);
			if (sameDay) {
				boolean withinHour = now.getTimeInMillis() - this.lastRequest.getTimeInMillis() < 60 * 60 * 1000;
				if (withinHour) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * get the weather forecast for today
	 * 
	 * @return todays weather forecast
	 */
	public WeatherReportDay getReportToday() {
		FIODaily d = this.getDailyReports();
		return new WeatherReportDay("This is the weather report for today.", d.getDay(0));
	}

	/**
	 * get the weather forecast for tomorrow
	 * 
	 * @return tomorrows weather forecast
	 */
	public WeatherReportDay getReportTomorrow() {
		FIODaily d = this.getDailyReports();
		return new WeatherReportDay("This is the weather report for tomorrow.", d.getDay(1));
	}

	/**
	 * get the weather forecast for the week
	 * 
	 * @return this weeks weather forecast
	 */
	public WeatherReportWeek getReportWeek() {
		return new WeatherReportWeek("This is the weather report for the week. ", this.getDailyReports());
	}

	/**
	 * set a new locationId
	 * 
	 * @param locationId
	 *            id from the registry entry
	 */
	public void setLocation(int locationId) {
		this.locationChanged = true;
		this.storage.put(WEATHER_LOCATION_ID_STRING, String.valueOf(locationId));
	}

	/**
	 * load the lat and long from registry
	 */
	private void loadLocation() {
		if (this.storage.has(WEATHER_LOCATION_ID_STRING)) {
			Location location = this.locationRegistry
					.getById(Integer.parseInt(this.storage.get(WEATHER_LOCATION_ID_STRING)));
			if (location != null) {
				this.coordinateLat = String.valueOf(location.getLatitude());
				this.coordinateLong = String.valueOf(location.getLongitude());
			} else {
				this.coordinateLat = "48.745886";
				this.coordinateLong = "9.107881";
			}
		}
	}
}
