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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.userinteraction;

import java.time.LocalTime;
import java.util.regex.Pattern;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData;

/**
 * Stores the data from one entity. Only one attribute is set at the same time
 * 
 * @author Lars Buttgereit
 */
public class EntityDataImpl implements EntityData {

	private String string;
	private LocalTime time;

	private Pattern minute = Pattern.compile("[0-5]?[0-9]");
	private Pattern hour24 = Pattern.compile("([01]?[0-9]|2[0-3])");
	private Pattern hour12 = Pattern.compile("([0-9]|1[0-2])");

	public EntityDataImpl(String string) {
		this.string = string;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData#getNumber()
	 */
	@Override
	public int getNumber() {
		try {
			return Integer.parseInt(this.string);
		} catch (NumberFormatException e) {

			return Integer.MIN_VALUE;
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData#getString()
	 */
	@Override
	public String getString() {
		return this.string;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData#getTime()
	 */
	@Override
	public LocalTime getTime() {
		if (this.time == null) {
			this.time = getGoogleTime();
		}
		if (this.time == null) {
			this.time = getNaturalTime();
		}
		return this.time;
	}

	private LocalTime getNaturalTime() {
		int hour = 0;
		int min = 0;
		if (Pattern.compile("pm").matcher(this.string).find()) {
			hour += 12;
		}
		if (Pattern.compile(this.hour12.toString() + " o clock (am|pm)").matcher(this.string).matches()) {
			this.string = this.string.replaceAll(" o clock (am|pm)", "");
			hour += Integer.parseInt(this.string.trim());
			hour = convert12(hour);
		} else if (Pattern.compile(this.hour24.toString() + " o clock").matcher(this.string).matches()) {
			this.string = this.string.replaceAll(" o clock", "");
			hour = Integer.parseInt(this.string.trim());
		} else if (Pattern
				.compile("(quarter|" + this.minute.toString() + ") to " + this.hour24.toString() + " (am|pm)?")
				.matcher(this.string).matches()) {
			if (Pattern.compile("quarter").matcher(this.string).find()) {
				min = 45;
				hour += Integer.parseInt(this.string.split(" ")[2]) - 1;
			} else {
				min = 60 - Integer.parseInt(this.string.split(" ")[0]);
				hour += Integer.parseInt(this.string.split(" ")[2]) - 1;
			}
		} else if (Pattern
				.compile("(quarter|" + this.minute.toString() + ") past " + this.hour24.toString() + " (am|pm)?")
				.matcher(this.string).matches()) {
			if (Pattern.compile("quarter").matcher(this.string).find()) {
				min = 15;
				hour += Integer.parseInt(this.string.split(" ")[2]);
			} else {
				min =  Integer.parseInt(this.string.split(" ")[0]);
				hour += Integer.parseInt(this.string.split(" ")[2]);
			}
		} else {
			return null;
		}
		return LocalTime.of(hour, min);
	}

	private LocalTime getGoogleTime() {
		Pattern xOh = Pattern.compile(" (x|oh) ");
		int min = 0;
		int hour = 0;
		if (Pattern.compile("pm").matcher(this.string).find()) {
			hour += 12;
		}
		if (Pattern.compile(this.hour12.toString() + xOh.toString() + this.minute.toString() + " (am|pm)")
				.matcher(this.string).matches()) {
			this.string = this.string.replaceAll("(am|pm)", "");
			String[] minHour = this.string.split(xOh.toString());
			min = Integer.parseInt(minHour[1].trim());
			hour += Integer.parseInt(minHour[0].trim());
			hour = convert12(hour);

		} else if (Pattern.compile(this.hour24.toString() + xOh.toString() + this.minute.toString())
				.matcher(this.string).matches()) {
			String[] minHour = this.string.split(xOh.toString());
			min = Integer.parseInt(minHour[1].trim());
			hour = Integer.parseInt(minHour[0].trim());
		} else {
			return null;
		}

		return LocalTime.of(hour, min);
	}

	private int convert12(int hour) {
		if (hour == 24) {
			return 12;
		}
		if (hour == 12) {
			return 0;
		}
		return hour;
	}
}