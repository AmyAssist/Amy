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

package de.unistuttgart.iaas.amyassist.amy.plugin.calendar;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * This is the speech class, which contains the commands for the calendar
 * 
 * @author Patrick Gebhardt, Florian Bauer
 */
@SpeechCommand("calendar")
public class CalendarSpeech {

	@Reference
	private CalendarLogic calendar;

	/**
	 * 
	 * @param params
	 *            params from the speech
	 * 
	 * @return the upcoming X events from the calendar
	 */
	@Grammar("get next [#] event[s]")
	public String getEvents(String[] params) {
		String number;
		if (params[2].contains("event")) {
			number = "1";
		} else {
			number = params[2];
		}
		return this.calendar.getEvents(number);
	}

	/**
	 * @param params
	 *            params from the speech
	 * @return upcoming events on the current day
	 */
	@Grammar("events today")
	public String getEventsToday(String[] params) {
		return this.calendar.getEventsToday();

	}

}
