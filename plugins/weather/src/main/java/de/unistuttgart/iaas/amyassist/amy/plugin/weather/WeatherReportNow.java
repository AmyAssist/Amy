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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import static java.lang.Math.round;

/**
 * Serves as weather entity for current time
 * 
 * @author Muhammed Kaya
 */
@XmlRootElement
public class WeatherReportNow {
	private final String preamble;
	private final String summary;
	private final boolean precip;
	private final String precipProbability;
	private final String precipType;
	private final long temperatureNow;
	private final long timestamp;
	private final double windSpeed;
	private final String icon;

	private static final String TRIM_QUOTES_REGEX = "^\"|\"$";
	private static final int FRACTION_TO_PERCENT_FACTOR = 100;

	private String trimQuotes(String s) {
		return s.replaceAll(TRIM_QUOTES_REGEX, "");
	}

	/**
	 * @param preamble
	 *            Introduction text for speech
	 * @param p
	 *            FIOCurrently to get current weather values
	 */
	public WeatherReportNow(String preamble, FIODataPoint p) {
		this.preamble = preamble;
		this.summary = trimQuotes(p.summary());
		this.precipProbability = round(p.precipProbability() * FRACTION_TO_PERCENT_FACTOR) + "%";
		this.precipType = trimQuotes(p.precipType());
		this.precip = p.precipProbability() > 0;
		this.temperatureNow = Math.round(p.temperature());
		this.windSpeed = p.windSpeed();
		this.timestamp = p.timestamp();

		this.icon = trimQuotes(p.icon());
	}

	private String description(boolean tldr) {
		String result = (this.preamble != null ? this.preamble + " " : "") + this.summary;
		if (this.precip) {
			result += " " + this.precipProbability + " probability of " + this.precipType + ".";
		}
		result += " Temp is " + this.temperatureNow + "°C.";
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
	 * @return temperatureNow
	 */
	public long getTemperatureNow() {
		return this.temperatureNow;
	}

	/**
	 * @return timestamp
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * @return windSpeed
	 */
	public double getWindspeed() {
		return this.windSpeed;
	}

	/**
	 * @return icon
	 */
	public String getIcon() {
		return this.icon;
	}
}
