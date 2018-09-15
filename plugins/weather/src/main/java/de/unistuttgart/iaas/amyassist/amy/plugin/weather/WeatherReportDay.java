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

/**
 * Weather report for a day
 * 
 * @author Benno Krauß, Muhammed Kaya, Tim Neumann
 */
public class WeatherReportDay {
	/** The summary of the report */
	private final String summary;
	/** The probability for precipitation from 0 to 1 */
	private final double precipProbability;
	/** The type of precipitation. Possibilities: rain, snow, or sleet */
	private final String precipType;
	/** The minimal temperature in degrees celcius */
	private final double temperatureMin;
	/** The maximal temperature in degrees celcius */
	private final double temperatureMax;
	/** The time stamp of the weather report */
	private final long timestamp;
	/** The time stamp of the sunrise */
	private final long sunriseTime;
	/** The time stamp of the sunset */
	private final long sunsetTime;
	/** The wind speed in meters per second */
	private final double windSpeed;
	/** The local timezone for this weather report. */
	private final String timezone;
	/**
	 * The kind of icon appropriate for this report.
	 * <p>
	 * Possibilities: clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day or
	 * partly-cloudy-night
	 */
	private final String iconType;

	/**
	 * Creates a weather report for a day
	 * 
	 * @param pSummary
	 *            Sets {@link #summary}
	 * @param pPrecipitationProbability
	 *            Set's {@link #precipProbability}
	 * @param pPrecipitationType
	 *            Set's {@link #precipType}
	 * @param pTemperatureMin
	 *            Set's {@link #temperatureMin}
	 * @param pTemperatureMax
	 *            Set's {@link #temperatureMax}
	 * @param pTimestamp
	 *            Set's {@link #timestamp}
	 * @param pSunriseTime
	 *            Set's {@link #sunriseTime}
	 * @param pSunsetTime
	 *            Set's {@link #sunsetTime}
	 * @param pWindSpeed
	 *            Set's {@link #windSpeed}
	 * @param pIconType
	 *            Set's {@link #iconType}
	 */
	public WeatherReportDay(String pSummary, double pPrecipitationProbability, String pPrecipitationType,
			double pTemperatureMin, double pTemperatureMax, long pTimestamp, long pSunriseTime, long pSunsetTime,
			double pWindSpeed, String pIconType, String timezone) {
		this.summary = pSummary;
		this.precipProbability = pPrecipitationProbability;
		this.precipType = pPrecipitationType;
		this.temperatureMin = pTemperatureMin;
		this.temperatureMax = pTemperatureMax;
		this.timestamp = pTimestamp;
		this.sunriseTime = pSunriseTime;
		this.sunsetTime = pSunsetTime;
		this.windSpeed = pWindSpeed;
		this.iconType = pIconType;
		this.timezone = timezone;
	}

	/**
	 * Get's {@link #summary summary}
	 * 
	 * @return summary
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * Get's {@link #precipProbability precipProbability}
	 * 
	 * @return precipProbability
	 */
	public double getPrecipProbability() {
		return this.precipProbability;
	}

	/**
	 * Get's {@link #precipType precipType}
	 * 
	 * @return precipType
	 */
	public String getPrecipType() {
		return this.precipType;
	}

	/**
	 * Get's {@link #temperatureMin temperatureMin}
	 * 
	 * @return temperatureMin
	 */
	public double getTemperatureMin() {
		return this.temperatureMin;
	}

	/**
	 * Get's {@link #temperatureMax temperatureMax}
	 * 
	 * @return temperatureMax
	 */
	public double getTemperatureMax() {
		return this.temperatureMax;
	}

	/**
	 * Get's {@link #timestamp timestamp}
	 * 
	 * @return timestamp
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Get's {@link #sunriseTime sunriseTime}
	 * 
	 * @return sunriseTime
	 */
	public long getSunriseTime() {
		return this.sunriseTime;
	}

	/**
	 * Get's {@link #sunsetTime sunsetTime}
	 * 
	 * @return sunsetTime
	 */
	public long getSunsetTime() {
		return this.sunsetTime;
	}

	/**
	 * Get's {@link #windSpeed windSpeed}
	 * 
	 * @return windSpeed
	 */
	public double getWindSpeed() {
		return this.windSpeed;
	}

	/**
	 * Get's {@link #iconType iconType}
	 * 
	 * @return iconType
	 */
	public String getIconType() {
		return this.iconType;
	}

	/**
	 * Get's {@link #timezone timezone}
	 *
	 * @return iconType
	 */
	public String getTimezone() {
		return this.timezone;
	}
}
