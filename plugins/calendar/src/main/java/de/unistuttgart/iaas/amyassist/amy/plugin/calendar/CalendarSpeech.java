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

import java.time.LocalDateTime;
import java.time.LocalTime;
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
	
	private boolean allDay;
	private boolean startBeforeEnd;
	private LocalDateTime start;
	private LocalDateTime end;
	private LocalTime zero = LocalTime.of(0, 0, 0, 0);
	private CalendarEvent calendarEvent;
	

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
	 * 			from the speech
	 * @return events on the chosen date
	 */
	@Intent
	public String getEventsAt(Map<String, EntityData> entities) {
		return this.calendar.getEventsAtAsString(LocalDateTime.of(entities.get("date").getDate(), this.zero));
	}
	
	/**
	 * This method handles Amy response for creating a new event
	 * 
	 * @param entities
	 * 			from the speech
	 * @return if the event was successfully created or if not why
	 */
	@Intent
	public String setEvent(Map<String, EntityData> entities) {
		if (!this.setStartAndEnd(entities)) {
			return "You have to restart the creation of a new event and please make sure that you add a time to the "
					+ "start and to the end if you choose an non all day event.";
		}
		if (!this.startBeforeEnd) {
			return  "You have to restart the creation of a new event and please make sure that the start of the event "
					+ "is before the end.";
		}
		this.createNewEvent(entities);
		return "I set up the event " + this.calendarEvent.getSummary() + " for you."; 
	}
	
	/**
	 * This method sets the allDay variable
	 * 	
	 * @param allDayString
	 * 			the String from which the boolean is parsed.
	 */
	private void setAllDay(String allDayString) {
		if(allDayString.equals("yes") || allDayString.equals("true")) {
			this.allDay = true;
		} else {
			this.allDay = false;
		}
	}
	
	/**
	 * This method sets the start and the end of the event
	 * 
	 * @param entities
	 * 			contain the information for the start and the end
	 * @return if everything went well
	 */
	private boolean setStartAndEnd(Map<String, EntityData> entities) {
		this.setAllDay(entities.get("allday").getString());
		if(this.allDay) {
			this.start = LocalDateTime.of(entities.get("startdate").getDate().plusDays(1), this.zero);
			this.end = LocalDateTime.of(entities.get("enddate").getDate().plusDays(2), this.zero);
			this.startBeforeEnd = this.start.isBefore(this.end) || this.start.isEqual(this.end);	
		} else if(entities.get("starttime") == null || entities.get("endtime") == null) {
			return false;
		} else {
    		this.start = LocalDateTime.of(entities.get("startdate").getDate(), entities.get("starttime").getTime());
    		this.end = LocalDateTime.of(entities.get("enddate").getDate(), entities.get("endtime").getTime());
    		this.startBeforeEnd = this.start.isBefore(this.end);	
		}
		return true;
	}
	
	/**
	 * This method creates a new event.
	 * 
	 * @param entities
	 * 			informations for the new event
	 */
	private void createNewEvent(Map<String, EntityData> entities) {
		String location;
		String description;
		if (entities.get("location") == null) {
			location = "";
		} else {
			location = entities.get("location").getString();
			if(location.equals("no")) {location = "";}
		}
		if (entities.get("description") == null) {
			description = "";
		} else {
			description = entities.get("description").getString();
			if(description.equals("no")) {description = "";}
		}
		int reminderTime = entities.get("remindertimevalue").getNumber();
		String reminderTimeUnit = entities.get("remindertimeunit").getString();
		if (reminderTimeUnit.equals("hours") || reminderTimeUnit.equals("h")) {
			reminderTime *= 60;
		}
		if (reminderTimeUnit.equals("days")) {
			reminderTime *= 60 * 24;
		}
		this.calendarEvent = new CalendarEvent(this.start, this.end, entities.get("title").getString(), location, 
				description, entities.get("remindertype").getString(), reminderTime, "", this.allDay);
		this.calendar.setEvent(this.calendarEvent);
	}

}
