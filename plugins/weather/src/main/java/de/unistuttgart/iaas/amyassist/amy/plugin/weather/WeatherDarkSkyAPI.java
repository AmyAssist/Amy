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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Logic class to provide information about current and coming weather
 * 
 * @author Benno
 */
@Service
public class WeatherDarkSkyAPI {
	@Reference
	private Properties configuration;

	private static final String STUTTGART_COORDINATES_LAT = "48.745295";
	private static final String STUTTGART_COORDINATES_LONG = "9.10502";

	private static final String API_SECRET_CONFIG_KEY = "DARKSKY_API_SECRET";
	
	private FIODaily dailyReports;
	private Calendar lastRequest;

	private FIODaily getDailyReports() {
		if(this.dailyReports == null || checkTime()) {
			ForecastIO fio = new ForecastIO(this.configuration.getProperty(API_SECRET_CONFIG_KEY));
			fio.setUnits(ForecastIO.UNITS_SI);
			fio.getForecast(WeatherDarkSkyAPI.STUTTGART_COORDINATES_LAT, WeatherDarkSkyAPI.STUTTGART_COORDINATES_LONG);
			
			this.dailyReports = new FIODaily(fio);
			for (int i = 0; i < this.dailyReports.days(); i++) {
				FIODataPoint report = this.dailyReports.getDay(i);
				report.setTimezone(fio.getTimezone());
			}			
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
		boolean sameDay = now.get(Calendar.YEAR) == this.lastRequest.get(Calendar.YEAR) 
				&& now.get(Calendar.DAY_OF_YEAR) == this.lastRequest.get(Calendar.DAY_OF_YEAR);
		if(sameDay) {
			boolean withinHour = now.getTimeInMillis() - this.lastRequest.getTimeInMillis() < 60*60*1000;
			if(withinHour) {
				return false;
			}			
		}
		this.lastRequest = now;
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
}
