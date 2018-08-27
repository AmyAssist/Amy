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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.en;

import java.time.LocalTime;
import java.util.regex.Pattern;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.ITimeUtility;

/**
 * 
 * English Time Utility class
 * 
 * @author Lars Buttgereit
 */
public class EnglishTimeUtility implements ITimeUtility {

	private Pattern minute = Pattern.compile("[0-5]?[0-9]");
	private Pattern hour24 = Pattern.compile("([01]?[0-9]|2[0-3])");
	private Pattern hour12 = Pattern.compile("([0-9]|1[0-2])");

	@Override
	public LocalTime parseTime(String toParse) {
		LocalTime time = null;
		time = getGoogleTime(toParse);
		if (time == null) {
			time = getNaturalTime(toParse);
		}
		return time;
	}

	private LocalTime getNaturalTime(String input) {
		String timeString = input;
		int hour = 0;
		int min = 0;
		if (Pattern.compile("pm").matcher(timeString).find()) {
			hour += 12;
		}
		if (Pattern.compile(this.hour12.toString() + "( o clock)?\\s?(am|pm)?").matcher(timeString).matches()) {
			timeString = timeString.replaceAll(" o clock (am|pm)", "");
			timeString = timeString.replaceAll(" (am|pm)", "");
			hour += Integer.parseInt(timeString.trim());
			hour = convert12(hour);
		} else if (Pattern.compile(this.hour24.toString() + "( o clock)?").matcher(timeString).matches()) {
			timeString = timeString.replaceAll(" o clock", "");
			hour = Integer.parseInt(timeString.trim());
		} else if (Pattern
				.compile("(quarter|" + this.minute.toString() + ") to " + this.hour24.toString() + " (am|pm)?")
				.matcher(timeString).matches()) {
			if (Pattern.compile("quarter").matcher(timeString).find()) {
				min = 45;
				hour += Integer.parseInt(timeString.split(" ")[2]) - 1;
			} else {
				min = 60 - Integer.parseInt(timeString.split(" ")[0]);
				hour += Integer.parseInt(timeString.split(" ")[2]) - 1;
			}
		} else if (Pattern
				.compile("(quarter|" + this.minute.toString() + ") past " + this.hour24.toString() + " (am|pm)?")
				.matcher(timeString).matches()) {
			if (Pattern.compile("quarter").matcher(timeString).find()) {
				min = 15;
				hour += Integer.parseInt(timeString.split(" ")[2]);
			} else {
				min = Integer.parseInt(timeString.split(" ")[0]);
				hour += Integer.parseInt(timeString.split(" ")[2]);
			}
		} else {
			return null;
		}
		return LocalTime.of(hour, min);
	}

	private LocalTime getGoogleTime(String input) {
		String timeString = input;
		Pattern xOh = Pattern.compile(" (x|oh) ");
		int min = 0;
		int hour = 0;
		if (Pattern.compile("pm").matcher(timeString).find()) {
			hour += 12;
		}
		if (Pattern.compile(this.hour12.toString() + xOh.toString() + this.minute.toString() + " (am|pm)")
				.matcher(timeString).matches()) {
			timeString = timeString.replaceAll("(am|pm)", "");
			String[] minHour = timeString.split(xOh.toString());
			min = Integer.parseInt(minHour[1].trim());
			hour += Integer.parseInt(minHour[0].trim());
			hour = convert12(hour);

		} else if (Pattern.compile(this.hour24.toString() + xOh.toString() + this.minute.toString()).matcher(timeString)
				.matches()) {
			String[] minHour = timeString.split(xOh.toString());
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

	@Override
	public String formatTime(String input) {
		String output = input;
		String regex = "\\d+:?(\\d+)?(\\s(p\\.|a\\.)m\\.)?";
		if (Pattern.compile(regex).matcher(input).find()) {
			output = output.replace(":", " x ");
			output = output.replace("a.m.", "am");
			output = output.replace("p.m.", "pm");
		}
		return output;
	}

}
