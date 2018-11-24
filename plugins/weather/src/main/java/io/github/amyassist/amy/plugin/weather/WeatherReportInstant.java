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

/**
 * Weather report for an instant
 * 
 * @author Muhammed Kaya, Tim Neumann
 */
public class WeatherReportInstant {
	/** The summary of the report */
	private final String summary;
	/** The probability for precipitation from 0 to 1 */
	private final double precipProbability;
	/** The type of precipitation. Possibilities: rain, snow, or sleet */
	private final String precipType;
	/** The temperature in degrees celcius */
	private final double temperature;
	/** The time stamp of the weather report */
	private final long timestamp;
	/** The wind speed in meters per second */
	private final double windSpeed;
	/**
	 * The kind of icon appropriate for this report.
	 * <p>
	 * Possibilities: clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day or
	 * partly-cloudy-night
	 */
	private final String iconType;

	/**
	 * Creates a weather report for an instant
	 * 
	 * @param pSummary
	 *            Sets {@link #summary}
	 * @param pPrecipitationProbability
	 *            Set's {@link #precipProbability}
	 * @param pPrecipitationType
	 *            Set's {@link #precipType}
	 * @param pTemperature
	 *            Set's {@link #temperature}
	 * @param pTimestamp
	 *            Set's {@link #timestamp}
	 * @param pWindSpeed
	 *            Set's {@link #windSpeed}
	 * @param pIconType
	 *            Set's {@link #iconType}
	 */
	public WeatherReportInstant(String pSummary, double pPrecipitationProbability, String pPrecipitationType,
			double pTemperature, long pTimestamp, double pWindSpeed, String pIconType) {
		this.summary = pSummary;
		this.precipProbability = pPrecipitationProbability;
		this.precipType = pPrecipitationType;
		this.temperature = pTemperature;
		this.timestamp = pTimestamp;
		this.windSpeed = pWindSpeed;
		this.iconType = pIconType;
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
	 * Get's {@link #temperature temperature}
	 * 
	 * @return temperature
	 */
	public double getTemperature() {
		return this.temperature;
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
}
