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
import java.time.LocalTime;
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
		STARTINPAST, STARTINFUTURE, ALLDAYLONG, SINGLEDAY
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
			if (number.equals("1")) {
				return "You have following upcoming event:\n" + String.join("\n", this.eventList);
			}
			return "You have following upcoming " + number + " events:\n" + String.join("\n", this.eventList);
		} catch (IOException e) {
			this.logger.error("Sorry, I am not able to get your events.", e);
			return "An error occured.";
		}

	}

	/**
	 * This method contains the logic to show the calendar events today and tomorrow
	 *
	 * @param today
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
				LocalTime zero = LocalTime.of(0, 0, 0, 0);
				LocalDate nextDay = now.plusDays(1).toLocalDate();
				now = LocalDateTime.of(nextDay, zero);
			}
			LocalDate startDate;
			LocalDate nowDate = now.toLocalDate();
			LocalDate endDate;
			for (Event event : items) {
				if (event.getStart().getDate() != null) {
					startDate = LocalDate.parse(event.getStart().getDate().toString());
					endDate = LocalDate.parse(event.getEnd().getDate().toString()).minusDays(1);
				} else {
					startDate = LocalDate.parse(event.getStart().getDateTime().toString().substring(0, 10));
					endDate = LocalDate.parse(event.getEnd().getDateTime().toString().substring(0, 10));
				}
				if (nowDate.isAfter(startDate) || nowDate.equals(startDate)) {
					if (nowDate.isBefore(endDate) || nowDate.equals(endDate)) {
						checkDay(now, event, false);
					}
				} else {
					break;
				}
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
		} catch (

		IOException e) {
			this.logger.error("Sorry, I am not able to get your events.", e);
			return "An error occured.";
		}
	}

	/**
	 * This method checks if and how an event is to be displayed in the output.
	 *
	 * @param dayToCheck
	 *            the day from which we want to know how the current event belongs to it
	 * @param event
	 *            the current chosen event
	 * @param withDate
	 *            if the date should be displayed (or only the time)
	 *
	 */
	public void checkDay(LocalDateTime dayToCheck, Event event, boolean withDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("XXX");
		String timeZone = sdf.format(Date.from(dayToCheck.atZone(ZoneId.systemDefault()).toInstant()));
		LocalDateTime startDateTime;
		LocalDateTime endDateTime;
		LocalDate startDate;
		LocalDate checkDate;
		LocalDate endDate;
		OutputCase outputCase = OutputCase.SINGLEDAY;
		boolean withTime;
		boolean withStartDate = withDate;
		boolean withEndDate = withDate;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		// check if the day has a timestamp or only a date
		if (event.getStart().getDate() != null) {
			startDateTime = LocalDateTime.parse(event.getStart().getDate().toString() + "T00:00:00.000" + timeZone,
					formatter);
			endDateTime = LocalDateTime
					.parse(event.getEnd().getDate().toString() + "T23:59:59.999" + timeZone, formatter).minusDays(1);
			withTime = false;
		} else {
			startDateTime = LocalDateTime.parse(event.getStart().getDateTime().toString(), formatter);
			endDateTime = LocalDateTime.parse(event.getEnd().getDateTime().toString(), formatter);
			withTime = true;
		}
		startDate = startDateTime.toLocalDate();
		checkDate = dayToCheck.toLocalDate();
		endDate = endDateTime.toLocalDate();
		// check if the beginning and the end of the event is on another day as the current day
		if (dayToCheck.isAfter(startDateTime) && dayToCheck.isBefore(endDateTime)) {
			// event already started
			outputCase = OutputCase.STARTINPAST;
			if (checkDate.isAfter(startDate) && checkDate.isBefore(endDate)) {
				// event didn't start at dayToCheck and won't finish at dayToCheck
				withStartDate = true;
				withEndDate = true;
			} else if (checkDate.isEqual(startDate)) {
				// event started at dayToCheck
				if (checkDate.isEqual(endDate)) {
					// event will also finish at dayToCheck
					if (withTime) {
						// event has specific time stamps
						outputCase = OutputCase.SINGLEDAY;
						withStartDate = withDate;
					} else {
						// event is defined as all day long and has no specific time stamps
						outputCase = OutputCase.ALLDAYLONG;
						withStartDate = withDate;
					}
				} else {
					// event will finish on a different date
					withStartDate = withDate;
					withEndDate = true;
				}
			} else {
				// event ends on dayToCheck
				withStartDate = true;
			}
		} else if (dayToCheck.isBefore(startDateTime)) {
			// event will start in future
			outputCase = OutputCase.STARTINFUTURE;
			if (checkDate.isEqual(startDate)) {
				// event will start at dayToCheck
				withStartDate = withDate;
				if (checkDate.isEqual(endDate)) {
					// event will also finish at the same day
					outputCase = OutputCase.SINGLEDAY;
				} else {
					// event will finish on an other day in the future
					withEndDate = true;
				}
			} else {
				// event will start on different date in the future
				withStartDate = true;
			}
		}

		eventToString(startDateTime, endDateTime, event, withStartDate, withEndDate, withTime, outputCase);

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
	 * @param withStartDate
	 *            determines if event output is with or without the start date
	 * @param withEndDate
	 *            determines if event output is with or without the end date
	 * @param withTime
	 *            distinguishes if it is an event with or without a time stamp
	 * @param outputCase
	 *            distinguishes between different output cases
	 */
	public void eventToString(LocalDateTime startDate, LocalDateTime endDate, Event event, boolean withStartDate,
			boolean withEndDate, boolean withTime, OutputCase outputCase) {
		String eventData = event.getSummary();
		String eventStartDate = "";
		String eventEndDate = "";
		String eventStartTime = "";
		String eventEndTime = "";
		DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
		if (withStartDate) {
			eventStartDate = " the " + ordinal(startDate.getDayOfMonth()) + " of "
					+ startDate.getMonth().toString().toLowerCase();
			if (withTime) {
				eventStartDate += " at";
			}
		}
		if (withEndDate) {
			eventEndDate = " the " + ordinal(endDate.getDayOfMonth()) + " of "
					+ endDate.getMonth().toString().toLowerCase();
			if (withTime) {
				eventEndDate += " at";
			}
		}
		if (withTime) {
			eventStartTime = " " + time.format(startDate);
			eventEndTime = " " + time.format(endDate);
		}
		switch (outputCase) {
		case STARTINPAST:
			if (withStartDate || withTime) {
				eventData += " since" + eventStartDate + eventStartTime;
			}
			if (withEndDate || withTime) {
				eventData += " until" + eventEndDate + eventEndTime;
			}
			eventData += ". \n";
			break;
		case STARTINFUTURE:
			eventData += " from" + eventStartDate + eventStartTime + " until" + eventEndDate + eventEndTime + ". \n";
			break;
		case ALLDAYLONG:
			if (withStartDate) {
				eventStartDate = " on the " + ordinal(startDate.getDayOfMonth()) + " of " + startDate.getMonth();
			}
			eventData += eventStartDate + " all day long. \n";
			break;
		case SINGLEDAY:
			if (withStartDate) {
				eventData += " on the " + ordinal(startDate.getDayOfMonth()) + " of " + startDate.getMonth() + " at "
						+ time.format(startDate) + " until " + time.format(endDate) + ". \n";
			} else {
				eventData += " from " + time.format(startDate) + " until " + time.format(endDate) + ". \n";
			}
			break;

		default:
			this.logger.error("Missing or wrong output case.");
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
