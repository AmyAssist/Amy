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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand;

/**
 * A plugin which tells time and date
 * 
 * @author Florian Bauer, Patrick Gebhardt
 */
@SpeechCommand
public class SystemTimeSpeech {

	@Reference
	private SystemTimeLogic logic;

	/**
	 * A method which returns the current time
	 *
	 * @return current time (hour minute) in a string, e.g. it is 10:30
	 */
	@Grammar("(what is|tell me) the time")
	public String time(String[] s) {
		return "it is " + this.logic.getTime().substring(0, 5);
	}

	/**
	 * A method which returns the current date
	 *
	 * @return current date (day month year) in a string, e.g. it is the 20th of june
	 */
	@Grammar("(what is|tell me) the date")
	public String date(String[] s) {
		return "it is the " + this.logic.getDate();
	}

	/**
	 * A method which returns the current year
	 * 
	 * @return current year in a string, e.g. it is 2018
	 */
	@Grammar("(what is|tell me) the year")
	public String year(String[] s) {
		return "it is " + this.logic.getYear();
	}

}
