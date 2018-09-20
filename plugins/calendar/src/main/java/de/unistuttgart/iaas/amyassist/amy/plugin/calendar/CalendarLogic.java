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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * This class implements all the functions that our calendar plugin is capable of, e.g. get calendar events and set
 * calendar events.
 *
 * @author Florian Bauer, Patrick Gebhardt
 */
@Service
public class CalendarLogic {

	@Reference
	private CalendarService calendarService;
	@Reference
	private Logger logger;
	@Reference
	private Environment environment;

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private LocalTime zero = LocalTime.of(0, 0, 0, 0);

	/**
	 * This method creates an event for the connected google calendar, modified version from
	 * https://developers.google.com/calendar/create-events
	 *
	 * @param calendarEvent
	 *            the event which will be created in the google calendar
	 */
	public void setEvent(CalendarEvent calendarEvent) {
		Event event = new Event().setSummary(calendarEvent.getSummary()).setLocation(calendarEvent.getLocation())
				.setDescription(calendarEvent.getDescription());

		EventDateTime start = new EventDateTime();
		EventDateTime end = new EventDateTime();
		ZonedDateTime startZDT = calendarEvent.getStart().atZone(ZoneId.systemDefault());
		DateTime startDT = new DateTime(startZDT.toInstant().toEpochMilli());
		ZonedDateTime endZDT = calendarEvent.getEnd().atZone(ZoneId.systemDefault());
		DateTime endDT = new DateTime(endZDT.toInstant().toEpochMilli());
		if (calendarEvent.isAllDay()) {
			start.setDate(new DateTime(true, startZDT.toInstant().toEpochMilli(), 0));
			end.setDate(new DateTime(true, endZDT.toInstant().toEpochMilli(), 0));
		} else {
			start.setDateTime(startDT);
			end.setDateTime(endDT);
		}
		start.setTimeZone(ZoneId.systemDefault().toString());
		end.setTimeZone(ZoneId.systemDefault().toString());

		event.setStart(start);
		event.setEnd(end);

		event.setReminders(calendarEvent.getReminders());
		this.calendarService.addEvent("primary", event);
	}

	/**
	 * This method lists the next events from the calendar
	 *
	 * @param number
	 *            number of events the user wants to get
	 * @return event summary
	 */
	public List<CalendarEvent> getEvents(int number) {
		List<CalendarEvent> eventList = new ArrayList<>();
		LocalDateTime now = this.environment.getCurrentLocalDateTime();		
		DateTime current = getDateTime(now, 0);
		Events events = this.calendarService.getEvents(current, number);
		List<Event> items = events.getItems();
		for (Event event : items) {
			CalendarEvent calendarEvent = new CalendarEvent(event.getId(), getLocalDateTimeStart(event),
					getLocalDateTimeEnd(event), event.getSummary(), event.getLocation(), event.getDescription(),
					isAllDay(event));
			eventList.add(calendarEvent);
		}
		return eventList;
	}

	/**
	 * This method contains the logic to show the calendar events on a specific date as a list of Events
	 *
	 * @param chosenDay
	 *            LocalDateTime variable
	 * @return the events of the chosen day as a List<Event>
	 */
	public List<CalendarEvent> getEventsAt(LocalDateTime chosenDay) {
		List<CalendarEvent> eventList = new ArrayList<>();
		DateTime min = getDateTime(chosenDay, 0);
		DateTime max = getDateTime(chosenDay, 1);
		Events events = this.calendarService.getEvents(min, max);
		List<Event> items = events.getItems();
		for (Event event : items) {
			CalendarEvent calendarEvent = new CalendarEvent(event.getId(), getLocalDateTimeStart(event),
					getLocalDateTimeEnd(event), event.getSummary(), event.getLocation(), event.getDescription(),
					isAllDay(event));
			eventList.add(calendarEvent);
		}
		return eventList;
	}

	/**
	 * This method converts a LocalDateTime to a google api DateTime
	 *
	 * @param ldt
	 *            LocalDateTime variable
	 * @param plusDays
	 *            the number of days you want to add
	 * @return DateTime of input
	 */
	private DateTime getDateTime(LocalDateTime ldt, int plusDays) {
		LocalDate nextDay = ldt.plusDays(plusDays).toLocalDate();
		LocalDateTime endOfDay = LocalDateTime.of(nextDay, this.zero);
		ZonedDateTime zdt = endOfDay.atZone(ZoneId.systemDefault());
		return new DateTime(zdt.toInstant().toEpochMilli());
	}

	/**
	 * @param event
	 *            google calendar event
	 * @return if the event has no time stamps (all day event) or not
	 */
	public static boolean isAllDay(Event event) {
		return event.getStart().getDate() != null;
	}
	
	/**
	 * @param event
	 *            google calendar event
	 * @return the start of the event as LocalDateTime
	 */
	public LocalDateTime getLocalDateTimeStart(Event event) {
		if (isAllDay(event)) {
			return LocalDateTime.parse(event.getStart().getDate().toString() + "T00:00:00.000", formatter);
		}
		return ZonedDateTime.parse(event.getStart().getDateTime().toString())
				.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * @param event
	 *            google calendar event
	 * @return the end of the event as LocalDateTime
	 */
	public LocalDateTime getLocalDateTimeEnd(Event event) {
		if (isAllDay(event)) {
			return LocalDateTime.parse(event.getEnd().getDate().toString() + "T23:59:59.999", formatter).minusDays(1);
		}
		return ZonedDateTime.parse(event.getEnd().getDateTime().toString()).withZoneSameInstant(ZoneId.systemDefault())
				.toLocalDateTime();
	}
}
