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

import java.util.Arrays;

/**
 * Weather report for a week
 * 
 * @author Benno Krauß, Muhammed Kaya, Tim Neumann
 */
public class WeatherReportWeek {

	/** The reports for the days of the week. */
	private final WeatherReportDay[] days;
	/** The summary of the report. */
	private final String summary;

	/**
	 * Creates a weather report for a week
	 * 
	 * @param pSummary
	 *            Sets {@link #summary}
	 * @param pDays
	 *            Sets {@link #days}
	 */
	public WeatherReportWeek(String pSummary, WeatherReportDay[] pDays) {
		this.summary = pSummary;
		this.days = pDays;
	}

	/**
	 * Get's {@link #days days}
	 * 
	 * @return days
	 */
	public WeatherReportDay[] getDays() {
		return Arrays.copyOf(this.days, this.days.length);
	}

	/**
	 * Get's {@link #summary summary}
	 * 
	 * @return summary
	 */
	public String getSummary() {
		return this.summary;
	}
}
