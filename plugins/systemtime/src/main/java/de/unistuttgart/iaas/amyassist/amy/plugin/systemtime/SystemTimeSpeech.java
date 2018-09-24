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

package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;

/**
 * A plugin which tells time and date
 *
 * @author Florian Bauer, Patrick Gebhardt
 */
@Service
@SpeechCommand
public class SystemTimeSpeech {

	@Reference
	private Environment environment;

	private static final String ITIS = "It is ";

	/**
	 * A method which returns the current time
	 *
	 * @param entities
	 *            from the speech
	 * @return current time (hour minute) in a string, e.g. It is 10:30
	 */
	@Intent()
	public String time(Map<String, EntityData> entities) {
		return ITIS
				+ this.environment.getCurrentLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
				+ ".";
	}

	/**
	 * A method which returns the current date
	 *
	 * @param entities
	 *            from the speech
	 * @return current date (day month year) in a string, e.g. It is the 20th of june.
	 */
	@Intent()
	public String date(Map<String, EntityData> entities) {
		return ITIS + "the " + ordinal(this.environment.getCurrentLocalDateTime().getDayOfMonth()) + " of "
				+ this.environment.getCurrentLocalDateTime().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
				+ ".";
	}

	/**
	 * A method which returns the current year
	 *
	 * @param entities
	 *            from the speech
	 * @return current year in a string, e.g. It is 2018.
	 */
	@Intent
	public String year(Map<String, EntityData> entities) {
		return ITIS + this.environment.getCurrentLocalDateTime().getYear() + ".";
	}

	/**
	 * A method which returns the current day of week
	 *
	 * @param entities
	 *            from the speech
	 * @return current year in a string, e.g. It is monday.
	 */
	@Intent
	public String dayOfWeek(Map<String, EntityData> entities) {
		return ITIS + this.environment.getCurrentLocalDateTime().getDayOfWeek().getDisplayName(TextStyle.FULL,
				Locale.ENGLISH) + ".";
	}

	/**
	 * A method which returns the day of week of a chosen date.
	 *
	 * @param entities
	 *            from the speech
	 * @return current year in a string, e.g. Tomorrow is monday.
	 */
	@Intent
	public String dayOfWeekAt(Map<String, EntityData> entities) {
		LocalDate chosenDate = entities.get("date").getDate();
		LocalDate currentDate = this.environment.getCurrentLocalDateTime().toLocalDate();
		String tense = " is a ";
		if (!entities.get("date").getString().equals("today") && !entities.get("date").getString().equals("tomorrow")
				&& entities.get("year") == null && chosenDate.isBefore(currentDate)) {
			chosenDate = chosenDate.withYear(currentDate.getYear() + 1);
		}
		if (entities.get("date").getString().equals("today")) {
			return "Today is " + chosenDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ".";
		}
		if (entities.get("date").getString().equals("tomorrow")) {
			return "Tomorrow is " + chosenDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ".";
		}
		if (chosenDate.isBefore(currentDate)) {
			tense = " was a ";
		}
		if (chosenDate.getYear() != this.environment.getCurrentLocalDateTime().getYear()) {
			return "The " + ordinal(chosenDate.getDayOfMonth()) + " of "
					+ chosenDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + chosenDate.getYear()
					+ tense + chosenDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ".";
		}
		return "The " + ordinal(chosenDate.getDayOfMonth()) + " of "
				+ chosenDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + tense
				+ chosenDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ".";
	}

	/**
	 * A method to convert the integer day to an ordinal (from 1 to 31)
	 *
	 * @param i
	 *            the day as integer
	 * @return the day as ordinal, e.g. 1st
	 */
	public static String ordinal(int i) {
		String[] ordinals = { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
		if (10 < i && i < 14) {
			return i + "th";
		}
		return i + ordinals[i % 10];
	}

}
