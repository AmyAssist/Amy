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
import java.time.LocalDateTime;
import java.time.ZoneId;
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
			DateTime now = new DateTime(System.currentTimeMillis());
			Events events = this.calendarService.getService().events().list("primary").setMaxResults(Integer.valueOf(number)).setTimeMin(now)
					.setOrderBy("startTime").setSingleEvents(true).execute();
			List<Event> items = events.getItems();
			if (items.isEmpty()) {
				return "No upcoming events found.";
			}
			for (Event event : items) {
				DateTime start = event.getStart().getDateTime();
				eventCalc(start, event, true);
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
	 * 			true if it is today, false for tomorrow	
	 * @return the events of the chosen day
	 */
	public String getEventsByDay(boolean day) {
		try {
			this.eventList.clear();
			DateTime now = new DateTime(System.currentTimeMillis());
			Events events = this.calendarService.getService().events().list("primary").setTimeMin(now).setOrderBy("startTime")
					.setSingleEvents(true).execute();
			List<Event> items = events.getItems();
			if (items.isEmpty()) {
				return "No upcoming events found.";
			}
			if(!day) {
				Date today = new Date();
				LocalDateTime plusOne = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				plusOne = plusOne.plusDays(1);
				now = new DateTime(Date.from(plusOne.atZone(ZoneId.systemDefault()).toInstant()));
			}
			for (Event event : items) {
				String eventdatetime = "";
				String eventdate = "";
				if (event.getStart().getDateTime() != null) {
					eventdatetime = event.getStart().getDateTime().toString().substring(0, 10);
				} else if (event.getStart().getDate() != null) {
					eventdate = event.getStart().getDate().toString().substring(0, 10);
				}
				if (eventdatetime.equals(now.toString().substring(0, 10))) {
					DateTime start = event.getStart().getDateTime();
					eventCalc(start, event, false);
				} else if (eventdate.equals(now.toString().substring(0, 10))) {
					eventCalc(null, event, false);
				}
			}
			if(day) {
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
	 * This method formats the date and time of the events and adds them into the list
	 * 
	 * @param start
	 *            start time of the event
	 * @param event
	 *            data of the event
	 * @param withDate
	 *            determines if event output is with or without the date
	 */
	public void eventCalc(DateTime start, Event event, boolean withDate) {
		String eventData;
		String datePart = "";
		if (start != null) {
			String startData = start.toString();
			String[] parts = startData.split("T");
			String startDate = parts[0];
			String[] dayParts = startDate.split("-");
			String day = dayParts[2];
			String month = dayParts[1];
			String year = dayParts[0];
			String yearpart1 = year.substring(0, 2);
			String yearpart2 = year.substring(2, 4);
			String startTime = parts[1];
			DateTime endtime = event.getEnd().getDateTime();
			if(withDate) {
				datePart = " on the " + ordinal(Integer.parseInt(day)) + " of " + getMonth(month)
				+ " " + Integer.parseInt(yearpart1) + Integer.parseInt(yearpart2);
			}
			eventData = event.getSummary() + datePart  + " at "
					+ startTime.substring(0, 5) + " until " + endtime.toString().substring(11, 16) + "\n";
			this.eventList.add(eventData);
		}
		if (start == null) {
			DateTime date = event.getStart().getDate();
			String startData = date.toString();
			String[] dayParts = startData.split("-");
			String day = dayParts[2];
			String month = dayParts[1];
			String year = dayParts[0];
			String yearpart1 = year.substring(0, 2);
			String yearpart2 = year.substring(2, 4);
			if(withDate) {
				datePart = " on the " + ordinal(Integer.parseInt(day)) + " of " + getMonth(month)
				+ " " + Integer.parseInt(yearpart1) + Integer.parseInt(yearpart2);
			}
			eventData = event.getSummary() + datePart + " all day long." + "\n";
			this.eventList.add(eventData);
		}
	}

	/**
	 * This method decides in which month the event is
	 * 
	 * @param month
	 *            month of the event as a number in a String
	 * @return current month of year as String, e.g. May
	 */
	public String getMonth(String month) {
		String monthString = "";
		if (month.equals("01")) {
			monthString = "January";
		} else if (month.equals("02")) {
			monthString = "February";
		} else if (month.equals("03")) {
			monthString = "March";
		} else if (month.equals("04")) {
			monthString = "April";
		} else if (month.equals("05")) {
			monthString = "May";
		} else if (month.equals("06")) {
			monthString = "June";
		} else if (month.equals("07")) {
			monthString = "July";
		} else if (month.equals("08")) {
			monthString = "August";
		} else if (month.equals("09")) {
			monthString = "September";
		} else if (month.equals("10")) {
			monthString = "October";
		} else if (month.equals("11")) {
			monthString = "November";
		} else if (month.equals("12")) {
			monthString = "December";
		}
		return monthString;
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
