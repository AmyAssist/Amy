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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * The Logic for the system time
 * 
 * @author Florian Bauer, Patrick Gebhardt
 */
@Service
public class SystemTimeLogic {

	@Reference
	private Environment environment;

	/**
	 * 
	 * @return object of type Date
	 */
	public LocalDateTime getTimeStamp() {
		return this.environment.getCurrentLocalDateTime();
	}

	/**
	 * 
	 * @return current year as String (yyyy), e.g. 2018
	 */
	public int getYear() {
		return this.getTimeStamp().getYear();
	}

	/**
	 * 
	 * @return current date as String (dd MM yy), e.g. 01 06 18
	 */
	public String getDate() {
		LocalDate localDate = this.getTimeStamp().toLocalDate();
		return ordinal(localDate.getDayOfMonth()) + " of "
				+ localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
	}

	/**
	 * 
	 * @return current time as String (HH:mm:ss), e.g. 12:45:20
	 */
	public String getTime() {
		return this.getTimeStamp().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
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
		} else {
			return i + ordinals[i % 10];
		}
	}
}
