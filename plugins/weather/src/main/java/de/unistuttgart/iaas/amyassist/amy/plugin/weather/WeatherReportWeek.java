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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.github.dvdme.ForecastIOLib.FIODaily;

import de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity;

/**
 * Serves as weather entity for current time
 * 
 * @author Benno Krauß, Muhammed Kaya
 */
@XmlRootElement
public class WeatherReportWeek extends Entity {

	@XmlTransient
	public final String preamble;

	public final WeatherReportDay[] days;
	public final String summary;

	/**
	 * @param preamble
	 *            Introduction text for speech
	 * @param p
	 *            FIODaily to get weather values of the week
	 */
	public WeatherReportWeek(String preamble, FIODaily d) {
		this.preamble = preamble;
		this.days = new WeatherReportDay[d.days()];
		for (int i = 0; i < d.days(); i++) {
			this.days[i] = new WeatherReportDay(null, d.getDay(i));
		}
		this.summary = d.getSummary();
	}

	@Override
	public String toString() {
		return (this.preamble != null ? this.preamble + " " : "") + this.summary;
	}
}
