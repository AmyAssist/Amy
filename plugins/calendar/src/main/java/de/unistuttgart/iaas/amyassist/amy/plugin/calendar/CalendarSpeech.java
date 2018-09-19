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

import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;

/**
 * This is the speech class, which contains the commands for the calendar
 *
 * @author Florian Bauer, Patrick Gebhardt
 */
@Service
@SpeechCommand
public class CalendarSpeech {

	@Reference
	private CalendarLogic calendar;	

	/**
	 *
	 * @param entities
	 *            from the speech
	 *
	 * @return the upcoming X events from the calendar
	 */
	@Intent
	public String getEvents(Map<String, EntityData> entities) {
		return this.calendar.getEvents(entities.get("number").getNumber());
	}

	/**
	 * @param entities
	 *            from the speech
	 * @return upcoming events today or tomorrow depending on input
	 */
	@Intent
	public String getEventsToday(Map<String, EntityData> entities) {
		if (entities.get("day").getString().equalsIgnoreCase("today")) {
			return this.calendar.getEventsToday();
		}
		return this.calendar.getEventsTomorrow();

	}

	/**
	 * @param entities
	 *            from the speech
	 * @return events on the chosen date
	 */
	@Intent
	public String getEventsAt(Map<String, EntityData> entities) {
		return this.calendar.getEventsAtAsString(entities);
	}

	/**
	 * This method handles Amy response for creating a new event
	 *
	 * @param entities
	 *            from the speech
	 * @return if the event was successfully created or if not why
	 */
	@Intent
	public String setEvent(Map<String, EntityData> entities) {
		return this.calendar.createNewEvent(entities);
	}
}
