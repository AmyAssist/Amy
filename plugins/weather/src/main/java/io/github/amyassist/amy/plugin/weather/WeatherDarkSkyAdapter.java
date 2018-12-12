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

import java.util.Properties;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.ForecastIO;

import io.github.amyassist.amy.core.configuration.WithDefault;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * A adapter for the Weather dark sky api
 * 
 * @author Tim Neumann, Benno Krauß, Lars Buttgereit, Muhammed Kaya
 */
@Service
public class WeatherDarkSkyAdapter {
	private static final String CONFIG_KEY_API_SECRET = "DARKSKY_API_SECRET";
	private static final String TRIM_QUOTES_REGEX = "^\"|\"$";

	private static final String JSON_NAME_DAYS = "data";
	private static final String JSON_NAME_SUMMARY = "summary";
	private static final String JSON_NAME_PERCIP_PROB = "precipProbability";
	private static final String JSON_NAME_PERCIP_TYPE = "precipType";
	private static final String JSON_NAME_TEMPERATUR = "temperature";
	private static final String JSON_NAME_TEMPERATUR_MIN = "temperatureMin";
	private static final String JSON_NAME_TEMPERATUR_MAX = "temperatureMax";
	private static final String JSON_NAME_TIME = "time";
	private static final String JSON_NAME_SUNRISE = "sunriseTime";
	private static final String JSON_NAME_SUNSET = "sunsetTime";
	private static final String JSON_NAME_WIND_SPEED = "windSpeed";
	private static final String JSON_NAME_ICON_TYPE = "icon";

	@WithDefault
	@Reference
	private Properties configuration;

	private String apiSecret;

	@PostConstruct
	private void init() {
		this.apiSecret = this.configuration.getProperty(CONFIG_KEY_API_SECRET);
		if (this.apiSecret.isEmpty())
			throw new IllegalStateException(
					"DarkSky API SECRET is missing. Set it in the config of the plugin with the key '"
							+ CONFIG_KEY_API_SECRET + "'.");
	}

	/**
	 * Get the weather report
	 * 
	 * @param latitude
	 *            The latitude to get the report for
	 * @param longitude
	 *            The longitude to get the report for
	 * 
	 * @return current weather forecast
	 */
	WeatherReport getWeatherReport(String latitude, String longitude) {
		ForecastIO fio = new ForecastIO(this.apiSecret);
		fio.setUnits(ForecastIO.UNITS_SI);
		fio.setExcludeURL("[minutely,hourly]");
		fio.getForecast(latitude, longitude);
		JsonObject instant = fio.getCurrently();
		JsonObject week = fio.getDaily();

		return new WeatherReport(convertInstant(instant), convertWeek(week, fio.getTimezone()), fio.getTimezone());
	}

	private WeatherReportInstant convertInstant(JsonObject toConvert) {
		String summary = toConvert.getString(JSON_NAME_SUMMARY, "null");
		double precipProbability = toConvert.getDouble(JSON_NAME_PERCIP_PROB, Double.NaN);
		String precipType = toConvert.getString(JSON_NAME_PERCIP_TYPE, "null");
		double temperature = toConvert.getDouble(JSON_NAME_TEMPERATUR, Double.NaN);
		long timestamp = toConvert.getLong(JSON_NAME_TIME, 0);
		double windSpeed = toConvert.getDouble(JSON_NAME_WIND_SPEED, Double.NaN);
		String iconType = toConvert.getString(JSON_NAME_ICON_TYPE, "null");
		return new WeatherReportInstant(trimQuotes(summary), precipProbability, trimQuotes(precipType), temperature,
				timestamp, windSpeed, trimQuotes(iconType));
	}

	private WeatherReportWeek convertWeek(JsonObject toConvert, String timezone) {
		JsonArray arr = toConvert.get(JSON_NAME_DAYS).asArray();
		int size = arr.size();
		WeatherReportDay[] days = new WeatherReportDay[size];
		for (int i = 0; i < size; i++) {
			days[i] = convertDay(arr.get(i).asObject(), timezone);
		}

		return new WeatherReportWeek(toConvert.get(JSON_NAME_SUMMARY).asString(), days);
	}

	private WeatherReportDay convertDay(JsonObject toConvert, String timezone) {
		String summary = toConvert.getString(JSON_NAME_SUMMARY, "null");
		double precipProbability = toConvert.getDouble(JSON_NAME_PERCIP_PROB, Double.NaN);
		String precipType = toConvert.getString(JSON_NAME_PERCIP_TYPE, "null");
		double temperatureMin = toConvert.getDouble(JSON_NAME_TEMPERATUR_MIN, Double.NaN);
		double temperatureMax = toConvert.getDouble(JSON_NAME_TEMPERATUR_MAX, Double.NaN);
		long timestamp = toConvert.getLong(JSON_NAME_TIME, 0);
		long sunrise = toConvert.getLong(JSON_NAME_SUNRISE, 0);
		long sunset = toConvert.getLong(JSON_NAME_SUNSET, 0);
		double windSpeed = toConvert.getDouble(JSON_NAME_WIND_SPEED, Double.NaN);
		String iconType = toConvert.getString(JSON_NAME_ICON_TYPE, "null");

		return new WeatherReportDay(trimQuotes(summary), precipProbability, trimQuotes(precipType), temperatureMin,
				temperatureMax, timestamp, sunrise, sunset, windSpeed, trimQuotes(iconType), timezone);
	}

	private String trimQuotes(String s) {
		return s.replaceAll(TRIM_QUOTES_REGEX, "");
	}
}
