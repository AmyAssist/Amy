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
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
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
	private CalendarLogic logic;	
	@Reference
	private Logger logger;
	@Reference
	private Environment environment;
	
	private enum OutputCase {
		STARTINPAST, STARTINFUTURE, ALLDAYLONG, SINGLEDAY
	}

	private static final LocalTime ZERO = LocalTime.of(0, 0, 0, 0);
	private static final String TODAY = "today";

	/**
	 *
	 * @param entities
	 *            from the speech
	 *
	 * @return the upcoming X events from the calendar
	 */
	@Intent
	public String getEvents(Map<String, EntityData> entities) {
		List<CalendarEvent> events;
		List<String> eventList = new ArrayList<>();
		int number = entities.get("number").getNumber();
		LocalDateTime now = this.environment.getCurrentLocalDateTime();
		events = this.logic.getEvents(number);
		if (events.isEmpty()) {
			return "No events found.";
		}
		for (CalendarEvent event : events) {
			eventList.add(this.checkDate(now, event, false));
		}
		if (number == 1) {
			return "You have following upcoming event:\n" + String.join("\n", eventList);
		}
		return "You have following upcoming " + number + " events:\n" + String.join("\n", eventList);
	}

	/**
	 * @param entities
	 *            from the speech
	 * @return upcoming events today or tomorrow depending on input
	 */
	@Intent
	public String getEventsToday(Map<String, EntityData> entities) {
		List<CalendarEvent> events;
		List<String> eventList = new ArrayList<>();
		LocalDateTime now = this.environment.getCurrentLocalDateTime();
		String todayOrTomorrow = TODAY;
		if (!entities.get("day").getString().equalsIgnoreCase(TODAY)) {
			now = now.plusDays(1);
			todayOrTomorrow = "tomorrow";
		}
		events = this.logic.getEventsAt(now);
		if (events.isEmpty()) {
			return "You have no events " + todayOrTomorrow + ".";
		}
		for (CalendarEvent event : events) {
			eventList.add(this.checkDate(now, event, false));
		}
		return "You have following events " + todayOrTomorrow +":\n" + String.join("\n", eventList);

	}

	/**
	 * @param entities
	 *            from the speech
	 * @return events on the chosen date
	 */
	@Intent
	public String getEventsAt(Map<String, EntityData> entities) {
		List<CalendarEvent> events;
		List<String> eventList = new ArrayList<>();
		LocalDateTime chosenDate = LocalDateTime.of(entities.get("date").getDate(), ZERO);
		if (!entities.get("date").getString().equals(TODAY) && !entities.get("date").getString().equals("tomorrow")
				&& entities.get("eventyear") == null && entities.get("date").getDate()
				.isBefore(this.environment.getCurrentLocalDateTime().toLocalDate())) {
			chosenDate = chosenDate.withYear(this.environment.getCurrentLocalDateTime().getYear() + 1);
		}
		events = this.logic.getEventsAt(chosenDate);
		if (events.isEmpty()) {
			return "No events found for the " + getDate(chosenDate) + " " + chosenDate.getYear() + ".";
		}
		for (CalendarEvent event : events) {
			eventList.add(this.checkDate(chosenDate, event, false));
		}
		return "You have following events on the " + getDate(chosenDate) + " " + chosenDate.getYear() + ":\n"
		+ String.join("\n", eventList);
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
		boolean allDay = isAllDay(entities.get("allday").getString());
		LocalDateTime start;
		LocalDateTime end;

		if (allDay) {
			start = LocalDateTime.of(entities.get("startdate").getDate().plusDays(1), ZERO);
			end = LocalDateTime.of(entities.get("enddate").getDate().plusDays(2), ZERO);
		} else if(entities.get("starttime") == null || entities.get("endtime") == null) {
			return "You have to restart the creation of a new event and please make sure that you add a time to the "
					+ "start and to the end if you choose an non all day event.";
		} else {
    		start = LocalDateTime.of(entities.get("startdate").getDate(), entities.get("starttime").getTime());
    		end = LocalDateTime.of(entities.get("enddate").getDate(), entities.get("endtime").getTime());
		}
		LocalDateTime now = this.environment.getCurrentLocalDateTime();
		if (entities.get("startyear") == null && entities.get("endyear") == null && start.isBefore(now)) {
			start = start.withYear(now.getYear() + 1);
			end = end.withYear(now.getYear() + 1);
		}
		
		if (start == null || end == null) {
			return "You have to restart the creation of a new event and please make sure that you add a time to the "
					+ "start and to the end if you choose an non all day event.";
		}
		
		if (end.isBefore(start)) {
			return  "You have to restart the creation of a new event and please make sure that the start of the event "
					+ "is before the end.";
		}
		
		String location = entities.get("location").getString();
		if (location.equals("no")) { location = ""; }
		String description = entities.get("description").getString();
		if (description.equals("no")) { description = ""; }
		
		int reminderTime = entities.get("remindertimevalue").getNumber();
		String reminderTimeUnit = entities.get("remindertimeunit").getString();
		if (reminderTimeUnit.equals("hours")) {
			reminderTime *= 60;
		}
		if (reminderTimeUnit.equals("days")) {
			reminderTime *= 60 * 24;
		}
		
		String title = entities.get("title").getString();
		this.logic.setEvent(new CalendarEvent(start, end, title, location, description,
				entities.get("remindertype").getString(), reminderTime, "", allDay));
		return "I set up the event " + title + " for you."; 
	}	
	
	/**
	 * @param date
	 *            a date as LocalDate
	 * @return String in Natural Language of the date, e.g. "12th of May"
	 */
	public String getDate(LocalDateTime date) {
		return ordinal(date.getDayOfMonth()) + " of " + date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
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
	
	/**
	 * This method checks if and how an event is to be displayed in the output.
	 *
	 * @param dayToCheck
	 *            the day from which we want to know how the current event belongs to it
	 * @param event
	 *            the current chosen event
	 * @param withDate
	 *            if the date should be displayed (or only the time)
	 * @return the event as natural language text
	 */
	public String checkDate(LocalDateTime dayToCheck, CalendarEvent event, boolean withDate) {
		LocalDateTime startDateTime = event.getStart();
		LocalDateTime endDateTime = event.getEnd();
		LocalDate startDate = event.getStart().toLocalDate();
		LocalDate checkDate = dayToCheck.toLocalDate();
		LocalDate endDate = event.getEnd().toLocalDate();
		OutputCase outputCase;
		boolean withTime = !event.isAllDay();
		boolean withStartDate = withDate;
		boolean withEndDate = withDate;
		// check if the beginning and the end of the event is on another day as the current day
		if (dayToCheck.isAfter(startDateTime)) {
			// event already started
			outputCase = OutputCase.STARTINPAST;
			if (checkDate.isAfter(startDate) && checkDate.isBefore(endDate)) {
				// event didn't start at dayToCheck and won't finish at dayToCheck
				withStartDate = true;
				withEndDate = true;
			} else if (checkDate.isEqual(startDate)) {
				// event started at dayToCheck
				outputCase = eventType(checkDate, endDate, event, OutputCase.STARTINPAST);
				// event will finish on a different date
				withEndDate = !checkDate.isEqual(endDate);
			} else {
				// event ends on dayToCheck
				withStartDate = true;
			}
		} else {
			// event will start in future
			outputCase = eventType(startDate, endDate, event, OutputCase.STARTINFUTURE);
			// event will finish on an other day in the future
			withEndDate = !startDate.isEqual(endDate);
		}

		return eventToString(startDateTime, endDateTime, event, withStartDate, withEndDate, withTime, outputCase);
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
	 * @return the string generated for the event
	 */
	public String eventToString(LocalDateTime startDate, LocalDateTime endDate, CalendarEvent event, boolean withStartDate,
			boolean withEndDate, boolean withTime, OutputCase outputCase) {
		String eventData = convertEventTitle(event.getSummary());
		String eventStartDate = dateOutput(startDate, withStartDate, withTime);
		String eventEndDate = dateOutput(endDate, withEndDate, withTime);
		String eventStartTime = "";
		String eventEndTime = "";
		if (withTime) {
			eventStartTime = " " + getCorrectTime(startDate);
			eventEndTime = " " + getCorrectTime(endDate);
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
			eventData += " from" + eventStartDate + eventStartTime;
			eventData += " until" + eventEndDate + eventEndTime + ". \n";
			break;
		case ALLDAYLONG:
			if (withStartDate) {
				eventStartDate = " on the " + getDate(startDate);
			}
			eventData += eventStartDate + " all day long. \n";
			break;
		case SINGLEDAY:
			if (withStartDate) {
				eventData += " on the " + getDate(startDate) + " at " + getCorrectTime(startDate) + " until "
						+ getCorrectTime(endDate) + ". \n";
			} else {
				eventData += " from " + getCorrectTime(startDate) + " until " + getCorrectTime(endDate) + ". \n";
			}
			break;

		default:
			this.logger.error("Missing or wrong output case.");
			break;
		}

		return eventData;
	}

	/**
	 *
	 * @param localDateTime
	 *            input time as LocalDateTime
	 * @return correct time in 24 hour format with am/pm
	 */
	public String getCorrectTime(LocalDateTime localDateTime) {
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		if (localDateTime.getHour() > 11) {
			return timeFormatter.format(localDateTime) + " pm";
		}
		return timeFormatter.format(localDateTime) + " am";

	}

	/**
	 * Checks if given event summary is empty/ null and returns an alternative String or the title
	 *
	 * @param title
	 *            the title String of the event
	 * @return the title part of the event for Amy output
	 */
	private String convertEventTitle(String title) {
		if (title == null || title.equals("")) {
			return "An event without a title";
		}
		return title;
	}

	/**
	 * @param date
	 *            the date as LocalDateTime
	 * @param withDate
	 *            if the date should be displayed
	 * @param withTime
	 *            if there is a time following the date
	 * @return the date String part of Amys natural language output
	 */
	public String dateOutput(LocalDateTime date, boolean withDate, boolean withTime) {
		String output = "";
		if (withDate) {
			output = " the " + getDate(date);
			if (withTime) {
				output += " at";
			}
		}
		return output;
	}

	/**
	 * @param date1
	 *            the first date
	 * @param date2
	 *            the second date
	 * @param event
	 *            the current event
	 * @param defaultCase
	 *            the default OutputCase
	 * @return which OutputCase is needed
	 */
	public static OutputCase eventType(ChronoLocalDate date1, ChronoLocalDate date2, CalendarEvent event,
			OutputCase defaultCase) {
		if (date1.isEqual(date2)) {
			if (event.isAllDay()) {
				return OutputCase.ALLDAYLONG;
			}
			return OutputCase.SINGLEDAY;
		}
		return defaultCase;
	}
	
	/**
	 * @param allDayString
	 *            the String from which the boolean is parsed.
	 * @return if the event is all day
	 */
	public static boolean isAllDay(String allDayString) {
		return allDayString.equals("yes") || allDayString.equals("true");
	}
}
