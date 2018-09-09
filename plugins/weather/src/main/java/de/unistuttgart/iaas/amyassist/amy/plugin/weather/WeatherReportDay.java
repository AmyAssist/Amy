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

import com.github.dvdme.ForecastIOLib.FIODataPoint;

import de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.round;

/**
 * Serves as weather entity for current time
 * 
 * @author Benno Krauß, Muhammed Kaya
 */
@XmlRootElement
public class WeatherReportDay extends Entity {
	private final String preamble;
	private final String summary;
	private final boolean precip;
	private final String precipProbability;
	private final String precipType;
	private final long temperatureMin;
	private final long temperatureMax;
	private final String sunriseTime;
	private final String sunsetTime;
	private final String weekday;
	private final long timestamp;
	private final String icon;

	private static final String TRIM_QUOTES_REGEX = "^\"|\"$";
	private static final int FRACTION_TO_PERCENT_FACTOR = 100;
	private static final int SECONDS_TO_MILLIS_FACTOR = 1000;

	private String trimQuotes(String s) {
		return s.replaceAll(TRIM_QUOTES_REGEX, "");
	}

	/**
	 * @param preamble
	 *            Introduction text for speech
	 * @param p
	 *            FIODaily to get weather values of the day
	 */
	public WeatherReportDay(String preamble, FIODataPoint p) {
		this.preamble = preamble;
		this.summary = trimQuotes(p.summary());
		this.precipProbability = round(p.precipProbability() * FRACTION_TO_PERCENT_FACTOR) + "%";
		this.precipType = trimQuotes(p.precipType());
		this.precip = p.precipProbability() > 0;
		this.temperatureMin = Math.round(p.temperatureMin());
		this.temperatureMax = Math.round(p.temperatureMax());
		this.sunriseTime = p.sunriseTime();
		this.sunsetTime = p.sunsetTime();

		Date date = new Date(p.timestamp() * SECONDS_TO_MILLIS_FACTOR);
		this.weekday = new SimpleDateFormat("EEEE").format(date);
		this.timestamp = p.timestamp();

		this.icon = trimQuotes(p.icon());
	}

	private String description(boolean tldr) {
		String result = (this.preamble != null ? this.preamble + " " : "") + this.summary;
		if (this.precip) {
			result += " " + this.precipProbability + " probability of " + this.precipType + ".";
		}
		result += " Between " + this.temperatureMin + " and " + this.temperatureMax + "°C.";
		if (!tldr) {
			result += " Sunrise is at " + this.sunriseTime + " and sunset at " + this.sunsetTime;
		}
		return result;
	}

	/**
	 * @return description
	 */
	public String shortDescription() {
		return description(true);
	}

	@Override
	public String toString() {
		return description(false);
	}

	// Boilerplate getters (ffs it's 2018, when's java gonna get automatic property synthesis?

	/**
	 * @return preamble
	 */
	@XmlTransient
	public String getPreamble() {
		return this.preamble;
	}

	/**
	 * @return summary
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * @return precip
	 */
	public boolean isPrecip() {
		return this.precip;
	}

	/**
	 * @return precipProbability
	 */
	public String getPrecipProbability() {
		return this.precipProbability;
	}

	/**
	 * @return precipType
	 */
	public String getPrecipType() {
		return this.precipType;
	}

	/**
	 * @return temperatureMin
	 */
	public long getTemperatureMin() {
		return this.temperatureMin;
	}

	/**
	 * @return temperatureMax
	 */
	public long getTemperatureMax() {
		return this.temperatureMax;
	}

	/**
	 * @return sunriseTime
	 */
	public String getSunriseTime() {
		return this.sunriseTime;
	}

	/**
	 * @return sunsetTime
	 */
	public String getSunsetTime() {
		return this.sunsetTime;
	}

	/**
	 * @return weekday
	 */
	public String getWeekday() {
		return this.weekday;
	}

	/**
	 * @return timestamp
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * @return icon
	 */
	public String getIcon() {
		return this.icon;
	}
}
