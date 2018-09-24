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

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
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
	private SystemTimeLogic logic;

	/**
	 * A method which returns the current time
	 *
	 * @param entities
	 *            from the speech
	 * @return current time (hour minute) in a string, e.g. It is 10:30
	 */
	@Intent()
	public String time(Map<String, EntityData> entities) {
		return "It is " + this.logic.getTimeStamp().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + ".";
	}

	/**
	 * A method which returns the current date
	 *
	 * @param entities
	 *            from the speech
	 * @return current date (day month year) in a string, e.g. It is the 20th of june
	 */
	@Intent()
	public String date(Map<String, EntityData> entities) {
		return "It is the " + ordinal(this.logic.getTimeStamp().getDayOfMonth()) + " of "
				+ this.logic.getTimeStamp().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ".";
	}

	/**
	 * A method which returns the current year
	 *
	 * @param entities
	 *            from the speech
	 * @return current year in a string, e.g. It is 2018
	 */
	@Intent
	public String year(Map<String, EntityData> entities) {
		return "It is " + this.logic.getTimeStamp().getYear() + ".";
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
