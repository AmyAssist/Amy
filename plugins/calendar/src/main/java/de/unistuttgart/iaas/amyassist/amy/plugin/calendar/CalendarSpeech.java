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
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand;

/**
 * This is the speech class, which contains the commands for the calendar
 * 
 * @author Patrick Gebhardt, Florian Bauer
 */
@SpeechCommand
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
		return this.calendar.getEvents(Integer.parseInt(number));
	}

	/**
	 * @param params
	 *            params from the speech
	 * @return upcoming events today or tomorrow depending on input
	 */
	@Grammar("events (today|tomorrow)")
	public String getEventsToday(String[] params) {
		if (params[1].contains("today")) {
			return this.calendar.getEventsToday();
		}
		return this.calendar.getEventsTomorrow();

	}

}
