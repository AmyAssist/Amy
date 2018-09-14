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

import java.time.LocalDate;
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
 * @author Patrick Gebhardt, Florian Bauer
 */
@Service
@SpeechCommand
public class CalendarSpeech {

	@Reference
	private CalendarLogic calendar;
	
	private boolean allDay;
	private LocalDateTime start;
	private LocalDateTime end;
	private LocalTime zero = LocalTime.of(0, 0, 0, 0);
	

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
	 * This method creates a new event
	 * 
	 * @param entities
	 * 			from the speech
	 * @return if event could be created
	 */
	@Intent
	public String createNewEvent(Map<String, EntityData> entities) {
//		this.setAllDay(entities.get("allday").getString());
//		this.setStartAndEnd(entities.get("startdate").getDate(), entities.get("enddate").getDate(), entities.get("starttime").getTime(), entities.get("endtime").getTime());
//		if(this.start == null || this.end == null) {
//			return "You have to restart the creation of a new event and please make sure that you add a time to the start and to the end if you choose an non all day event.";
//		}
		return "created event: " + entities.get("title").getString(); 
	}
	
	private void setAllDay(String allDayString) {
		if(allDayString.equals("yes")) {
			this.allDay = true;
		} else {
			this.allDay = false;
		}
	}
	
	
	private void setStartAndEnd(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
		if(this.allDay) {
			this.start = LocalDateTime.of(startDate, this.zero);
			this.end = LocalDateTime.of(endDate, this.zero);
		} else {
			if(startTime != null) {
				this.start = LocalDateTime.of(startDate, startTime);
			} else {
				this.start = null;
			}
			if(endTime != null) {
				this.end = LocalDateTime.of(endDate, endTime);
			}
		}
	}

	/**
	 * example method
	 * 
	 * @param entities
	 * @return
	 */
	@Intent
	public String getADate(Map<String, EntityData> entities) {
		return "Date: " + entities.get("date").getDate().toString();
	}

	/**
	 * example method
	 * 
	 * @param entities
	 * @return
	 */
	@Intent
	public String getADateTime(Map<String, EntityData> entities) {
		return "DateTime: " + entities.get("datetime").getDateTime().toString();
	}

}
