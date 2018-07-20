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

package de.unistuttgart.iaas.amyassist.amy.plugin.navigation;

import java.time.LocalDateTime;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * Helper class to format times out of strings
 * 
 * @author Lars Buttgereit
 */
@Service
public class TimeFormatter {
	@Reference
	private Environment environment;
	@Reference
	private Logger logger;

	/**
	 * get a formated time. set the actual date and time to the given time of the same date.
	 *
	 * @param input
	 *            three string in the array, {'number', o, clock} or {'number', past|to, 'number'|quarter} the time is
	 *            in 24h format
	 * @return the formatted time
	 */
	protected LocalDateTime formatTimes(String[] input) {
		LocalDateTime currentTime = this.environment.getCurrentLocalDateTime();
		int day = currentTime.getDayOfMonth();
		int month = currentTime.getMonthValue();
		int year = currentTime.getYear();

		if (input != null && input.length == 3) {
			if (input[0].equals("quarter")) {
				if (input[1].equals("past") && (Integer.parseInt(input[2]) >= 0 && Integer.parseInt(input[2]) < 24)) {
					return LocalDateTime.of(year, month, day, Integer.parseInt(input[2]), 15);
				} else if (input[1].equals("to")
						&& (Integer.parseInt(input[2]) >= 0 && Integer.parseInt(input[2]) < 24)) {
					return LocalDateTime.of(year, month, day, Integer.parseInt(input[2]) - 1, 45);
				}
			} else if (input[1].equals("o") && (Integer.parseInt(input[0]) >= 0 && Integer.parseInt(input[0]) < 24)) {
				return LocalDateTime.of(year, month, day, Integer.parseInt(input[0]), 0);
			} else if (input[1].equals("past") && (Integer.parseInt(input[2]) >= 0 && Integer.parseInt(input[2]) < 24)
					&& (Integer.parseInt(input[0]) >= 0 && Integer.parseInt(input[0]) < 60)) {
				return LocalDateTime.of(year, month, day, Integer.parseInt(input[2]), Integer.parseInt(input[0]));
			} else if (input[1].equals("to") && (Integer.parseInt(input[2]) >= 0 && Integer.parseInt(input[2]) < 24)
					&& (Integer.parseInt(input[0]) >= 0 && Integer.parseInt(input[0]) < 60)) {
				return LocalDateTime.of(year, month, day, Integer.parseInt(input[2]) - 1,
						60 - Integer.parseInt(input[0]));
			}
		}
		return null;
	}
}
