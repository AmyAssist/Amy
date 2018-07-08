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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * This class is for the Calendar Authentication and Logic, parts of the Code are from
 * https://developers.google.com/calendar/quickstart/java
 *
 * @author Patrick Gebhardt, Florian Bauer
 */
@Service
public class CalendarLogic {

	@Reference
	private CalendarService calendarService;
	@Reference
	private Logger logger;

	private enum OutputCase {
		STARTINPAST, STARTTODAY, STARTINFUTURE, ENDINFUTURE, SINGLEDAY, ALLDAYLONG
	}

	private List<String> eventList = new ArrayList<>();

	/**
	 * This method lists the next events from the calendar
	 *
	 * @param number
	 *            number of events the user wants to get
	 * @return event summary
	 */
	public String getEvents(String number) {
		try {
			this.eventList.clear();
			DateTime current = new DateTime(System.currentTimeMillis());
			Events events = this.calendarService.getService().events().list("primary")
					.setMaxResults(Integer.valueOf(number)).setTimeMin(current).setOrderBy("startTime")
					.setSingleEvents(true).execute();
			List<Event> items = events.getItems();
			if (items.isEmpty()) {
				return "No upcoming events found.";
			}
			LocalDateTime now = LocalDateTime.now();
			for (Event event : items) {
				checkDay(now, event, true);
			}
			return "You have following upcoming events:\n" + String.join("\n", this.eventList);
		} catch (IOException e) {
			this.logger.error("Sorry, I am not able to get your events.", e);
			return "An error occured.";
		}

	}

	/**
	 * This method contains the logic to show the calendar events today and tomorrow
	 *
	 * @param day
	 *            true if it is today, false for tomorrow
	 * @return the events of the chosen day
	 */
	public String getEventsByDay(boolean today) {
		try {
			this.eventList.clear();
			LocalDateTime now = LocalDateTime.now();
			DateTime setup = new DateTime(System.currentTimeMillis());
			Events events = this.calendarService.getService().events().list("primary").setTimeMin(setup)
					.setOrderBy("startTime").setSingleEvents(true).execute();
			List<Event> items = events.getItems();
			if (items.isEmpty()) {
				return "No upcoming events found.";
			}
			if (!today) {
				now = now.plusDays(1);
			}
			for (Event event : items) {
				checkDay(now, event, false);
			}
			if (today) {
				if (this.eventList.isEmpty()) {
					return "There are no events today.";
				}
				return "You have following events today:\n" + String.join("\n", this.eventList);
			}
			if (this.eventList.isEmpty()) {
				return "There are no events tomorrow.";
			}
			return "You have following events tomorrow:\n" + String.join("\n", this.eventList);
		} catch (IOException e) {
			this.logger.error("Sorry, I am not able to get your events.", e);
			return "An error occured.";
		}
	}

	/**
	 * @param dayToCheck
	 * @param event
	 */
	public void checkDay(LocalDateTime dayToCheck, Event event, boolean withDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("XXX");
		String timeZone = sdf.format(Date.from(dayToCheck.atZone(ZoneId.systemDefault()).toInstant()));
		LocalDateTime startDateTime, endDateTime;
		LocalDate startDate, checkDate, endDate;
		boolean allDay;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		if (event.getStart().getDate() != null) {
			startDateTime = LocalDateTime.parse(event.getStart().getDate().toString() + "T00:00:00.000" + timeZone,
					formatter);
			endDateTime = LocalDateTime.parse(event.getEnd().getDate().toString() + "T23:59:59.999" + timeZone,
					formatter);
			allDay = true;
		} else {
			startDateTime = LocalDateTime.parse(event.getStart().getDateTime().toString(), formatter);
			endDateTime = LocalDateTime.parse(event.getEnd().getDateTime().toString(), formatter);
			allDay = false;
		}
		startDate = startDateTime.toLocalDate();
		checkDate = dayToCheck.toLocalDate();
		endDate = endDateTime.toLocalDate();
		// check if the beginning and the end of the event is on another day as the current day
		if (checkDate.isAfter(startDate) && checkDate.isBefore(endDate)) {
			eventToString(startDateTime, endDateTime, event, withDate, OutputCase.STARTINPAST);
		}
		if (dayToCheck.isAfter(startDateTime) && dayToCheck.isBefore(endDateTime)) {
			if (checkDate.isEqual(startDate)) {
				eventToString(startDateTime, endDateTime, event, withDate, OutputCase.STARTTODAY);
			}
		} else if (checkDate.isEqual(startDate)) {
			if (allDay) {
				eventToString(startDateTime, endDateTime, event, withDate, OutputCase.ALLDAYLONG);
			} else if (checkDate.isEqual(endDate)) {
				eventToString(startDateTime, endDateTime, event, withDate, OutputCase.SINGLEDAY);
			} else {
				eventToString(startDateTime, endDateTime, event, withDate, OutputCase.ENDINFUTURE);
			}
		}

	}

	/**
	 * This method formats the date and time of the events and adds them into the list
	 *
	 * @param startDate
	 *            start time of the event
	 * @param endDate
	 *            end time of the event
	 * @param event
	 *            data of the event
	 * @param withDate
	 *            determines if event output is with or without the date
	 * @param outputCase
	 */
	public void eventToString(LocalDateTime startDate, LocalDateTime endDate, Event event, boolean withDate,
			OutputCase outputCase) {
		String eventData = "";
		switch (outputCase) {
		case STARTINPAST:
			eventData = event.getSummary() + " since the " + ordinal(startDate.getDayOfMonth()) + " of "
					+ startDate.getMonth() + " at " + startDate.getHour() + ":" + startDate.getMinute() + " until the "
					+ ordinal(endDate.getDayOfMonth()) + " of " + endDate.getMonth() + " " + endDate.getYear() + " at "
					+ endDate.getHour() + ":" + endDate.getMinute() + ". \n";
			break;
		case STARTINFUTURE:
			eventData = event.getSummary() + " from the " + ordinal(startDate.getDayOfMonth()) + " of "
					+ startDate.getMonth() + " at " + startDate.getHour() + ":" + startDate.getMinute() + " until the "
					+ ordinal(endDate.getDayOfMonth()) + " of " + endDate.getMonth() + " " + endDate.getYear() + " at "
					+ endDate.getHour() + ":" + endDate.getMinute() + ". \n";
			break;
		case ENDINFUTURE:
			eventData = event.getSummary() + " from " + startDate.getHour() + ":" + startDate.getMinute()
					+ " until the " + ordinal(endDate.getDayOfMonth()) + " of " + endDate.getMonth() + " at "
					+ endDate.getHour() + ":" + endDate.getMinute() + ". \n";
			break;
		case ALLDAYLONG:
			eventData = event.getSummary() + " on the " + ordinal(startDate.getDayOfMonth()) + " of "
					+ startDate.getMonth() + " all day long. \n";
			break;
		case SINGLEDAY:
			eventData = event.getSummary() + " on the " + ordinal(startDate.getDayOfMonth()) + " of "
					+ startDate.getMonth() + " at " + startDate.getHour() + ":" + startDate.getMinute() + " until "
					+ endDate.getHour() + ":" + endDate.getMinute() + ". \n";
			break;

		default:
			break;
		}

		this.eventList.add(eventData);
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
		}
		return i + ordinals[i % 10];
	}
}
