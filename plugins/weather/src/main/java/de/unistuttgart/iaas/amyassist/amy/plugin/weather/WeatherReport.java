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

import java.time.Instant;

/**
 * A complete weather report
 * 
 * @author Tim Neumann
 */
public class WeatherReport {
	/** The amount of seconds it is acceptable to cache a weather report */
	private static final int ACCEPTABLE_AGE = 60 * 15;

	/** The report of the current weather */
	private final WeatherReportInstant current;
	/** The report of the weather for the next week. */
	private final WeatherReportWeek week;
	/** The time this weather report was created. */
	private final long creationTime;
	/** The local timezone for this weather report. */
	private final String timezone;

	/**
	 * Creates a complete weather report
	 * 
	 * @param pCurrent
	 *            Set's {@link #current}
	 * @param pWeek
	 *            Set's {@link #week}
	 * @param pTimezone
	 *            Set's {@link #timezone}
	 */
	public WeatherReport(WeatherReportInstant pCurrent, WeatherReportWeek pWeek, String pTimezone) {
		this.current = pCurrent;
		this.week = pWeek;
		this.timezone = pTimezone;
		this.creationTime = Instant.now().getEpochSecond();
	}

	/**
	 * Get's {@link #current current}
	 * 
	 * @return current
	 */
	public WeatherReportInstant getCurrent() {
		return this.current;
	}

	/**
	 * Get's {@link #week week}
	 * 
	 * @return week
	 */
	public WeatherReportWeek getWeek() {
		return this.week;
	}

	/**
	 * Get's {@link #timezone timezone}
	 * 
	 * @return timezone
	 */
	public String getTimezone() {
		return this.timezone;
	}

	/**
	 * Checks whether this report is to old to be up to date.
	 * 
	 * @return Whether this report is to old
	 */
	protected boolean isToOld() {
		return this.creationTime + ACCEPTABLE_AGE < Instant.now().getEpochSecond();
	}
}
