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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 
 * this interface provide methods for time processing
 * 
 * @author Lars Buttgereit
 */
public interface ITimeUtility {
	/**
	 * parse a string to a localTime
	 * 
	 * @param toParse
	 *            String to parse
	 * @return a LocalTime object
	 */
	LocalTime parseTime(String toParse);

	/**
	 * format the time from google speech to a more parser friendly format. for example english time: 1:00 p.m. to 1 x
	 * 00 pm
	 * 
	 * @param input
	 *            the string to replace the time
	 * @return the input string with replaced time
	 */
	String formatTime(String input);
	
	/**
	 * parse the date from the speech to a LocalDate
	 * @param toParse string to parse
	 * @return a localDate that represent the date form the string
	 */
	LocalDate parseDate(String toParse);
	
	/**
	 * format the time for parse. for example cut th by 4th or replace . to ' ' by 10.08.2018
	 * @param input string to format
	 * @return the input with all replacements
	 */
	String formatDate(String input);
}
