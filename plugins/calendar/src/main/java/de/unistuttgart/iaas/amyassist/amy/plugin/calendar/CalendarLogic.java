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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.api.services.calendar.model.EventReminder;
import org.slf4j.Logger;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * This class is for the Calendar Authentication and Logic, parts of the Code are from
 * https://developers.google.com/calendar/quickstart/java
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

    private enum OutputCase {
        STARTINPAST, STARTINFUTURE, ALLDAYLONG, SINGLEDAY
    }

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private LocalTime zero = LocalTime.of(0, 0, 0, 0);

    /**
     * Natural language response of Amy when the event list is empty.
     */
    String noEventsFound = "No upcoming events found.";

    /**
     * This method creates an event for the connected google calendar
     *
     * @param calendarEvent the event which will be created in the google calendar
     */
    public void setEvent(CalendarEvent calendarEvent) {
        Event event = new Event()
                .setSummary(calendarEvent.getSummary())
                .setLocation(calendarEvent.getLocation())
                .setDescription(calendarEvent.getDescription());

        EventDateTime start = new EventDateTime();
        EventDateTime end = new EventDateTime();
        ZonedDateTime startZDT = calendarEvent.getStart().atZone(ZoneId.systemDefault());
        DateTime startDT = new DateTime(startZDT.toInstant().toEpochMilli());
        ZonedDateTime endZDT = calendarEvent.getEnd().atZone(ZoneId.systemDefault());
        DateTime endDT = new DateTime(endZDT.toInstant().toEpochMilli());
        if (calendarEvent.isAllDay()) {
            start.setDate(startDT);
            end.setDate(endDT);
        } else {
            start.setDateTime(startDT);
            end.setDateTime(endDT);
        }
        start.setTimeZone(ZoneId.systemDefault().toString());
        end.setTimeZone(ZoneId.systemDefault().toString());

        event.setRecurrence(Arrays.asList(calendarEvent.getRecurrence()));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder()
                        .setMethod(calendarEvent.getReminderType())
                        .setMinutes(calendarEvent.getReminderTime()),
        };

        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        this.calendarService.addEvent("primary", event);
    }

    /**
     * This method lists the next events from the calendar
     *
     * @param number number of events the user wants to get
     * @return event summary
     */
    public String getEvents(int number) {
        List<String> eventList = new ArrayList<>();
        DateTime current = new DateTime(System.currentTimeMillis());
        Events events = this.calendarService.getEvents(current, number);
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            return this.noEventsFound;
        }
        LocalDateTime now = LocalDateTime.now();
        for (Event event : items) {
            eventList.add(checkDay(now, event, true));
        }
        if (number == 1) {
            return "You have following upcoming event:\n" + String.join("\n", eventList);
        }
        return "You have following upcoming " + number + " events:\n" + String.join("\n", eventList);

    }

    /**
     * This method contains the logic to show the calendar events today
     *
     * @return the events of today
     */
    public String getEventsToday() {
        List<String> eventList = new ArrayList<>();
        LocalDateTime now = this.environment.getCurrentLocalDateTime();
        DateTime min = new DateTime(System.currentTimeMillis());
        DateTime max = getDateTime(now, 1);
        Events events = this.calendarService.getEvents(min, max);
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            return this.noEventsFound;
        }
        for (Event event : items) {
            eventList.add(this.checkDay(now, event, false));
        }
        if (eventList.isEmpty()) {
            return "There are no events today.";
        }
        return "You have following events today:\n" + String.join("\n", eventList);
    }

    /**
     * This method contains the logic to show the calendar events tomorrow
     *
     * @return the events of tomorrow
     */
    public String getEventsTomorrow() {
        List<String> eventList = new ArrayList<>();
        LocalDateTime now = this.environment.getCurrentLocalDateTime();
        DateTime min = getDateTime(now, 1);
        DateTime max = getDateTime(now, 2);
        Events events = this.calendarService.getEvents(min, max);
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            return this.noEventsFound;
        }
        for (Event event : items) {
            eventList.add(this.checkDay(now, event, false));
        }
        if (eventList.isEmpty()) {
            return "There are no events tomorrow.";
        }
        return "You have following events tomorrow:\n" + String.join("\n", eventList);
    }

    /**
     * This method contains the logic to show the calendar events on a specific date as natural language output
     *
     * @param chosenDay LocalDateTime variable
     * @return the events of the chosen day
     */
    public String getEventsAtAsString(LocalDateTime chosenDay) {
        List<String> eventList = new ArrayList<>();
        DateTime min = getDateTime(chosenDay, 0);
        DateTime max = getDateTime(chosenDay, 1);
        Events events = this.calendarService.getEvents(min, max);
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            return this.noEventsFound;
        }
        for (Event event : items) {
            eventList.add(this.checkDay(chosenDay, event, false));
        }
        if (eventList.isEmpty()) {
            return "There are no events on the " + getDate(chosenDay) + ".";
        }
        return "You have following events on the " + getDate(chosenDay) + ":\n" + String.join("\n", eventList);
    }

    /**
     * This method contains the logic to show the calendar events on a specific date as a list of Events
     *
     * @param chosenDay LocalDateTime variable
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
     * @param ldt      LocalDateTime variable
     * @param plusDays the number of days you want to add
     * @return
     */
    private DateTime getDateTime(LocalDateTime ldt, int plusDays) {
        LocalDate nextDay = ldt.plusDays(plusDays).toLocalDate();
        LocalDateTime endOfDay = LocalDateTime.of(nextDay, this.zero);
        ZonedDateTime zdt = endOfDay.atZone(ZoneId.systemDefault());
        return new DateTime(zdt.toInstant().toEpochMilli());
    }

    /**
     * This method checks if and how an event is to be displayed in the output.
     *
     * @param dayToCheck the day from which we want to know how the current event belongs to it
     * @param event      the current chosen event
     * @param withDate   if the date should be displayed (or only the time)
     * @return the event as natural language text
     */
    public String checkDay(LocalDateTime dayToCheck, Event event, boolean withDate) {
        LocalDateTime startDateTime = getLocalDateTimeStart(event);
        LocalDateTime endDateTime = getLocalDateTimeEnd(event);
        LocalDate startDate = getLocalDateStart(event);
        LocalDate checkDate = dayToCheck.toLocalDate();
        LocalDate endDate = getLocalDateEnd(event);
        OutputCase outputCase;
        boolean withTime = !isAllDay(event);
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
     * @param startDate     start time of the event
     * @param endDate       end time of the event
     * @param event         data of the event
     * @param withStartDate determines if event output is with or without the start date
     * @param withEndDate   determines if event output is with or without the end date
     * @param withTime      distinguishes if it is an event with or without a time stamp
     * @param outputCase    distinguishes between different output cases
     * @return the string generated for the event
     */
    public String eventToString(LocalDateTime startDate, LocalDateTime endDate, Event event, boolean withStartDate,
                                boolean withEndDate, boolean withTime, OutputCase outputCase) {
        String eventData = event.getSummary();
        String eventStartDate = dateOutput(startDate, withStartDate, withTime);
        String eventEndDate = dateOutput(endDate, withEndDate, withTime);
        String eventStartTime = "";
        String eventEndTime = "";
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
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
                    eventStartDate = " on the " + getDate(startDate);
                }
                eventData += eventStartDate + " all day long. \n";
                break;
            case SINGLEDAY:
                if (withStartDate) {
                    eventData += " on the " + getDate(startDate) + " at " + time.format(startDate) + " until "
                            + time.format(endDate) + ". \n";
                } else {
                    eventData += " from " + time.format(startDate) + " until " + time.format(endDate) + ". \n";
                }
                break;

            default:
                this.logger.error("Missing or wrong output case.");
                break;
        }

        return eventData;
    }

    /**
     * @param date     the date as LocalDateTime
     * @param withDate if the date should be displayed
     * @param withTime if there is a time following the date
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
     * @param date1       the first date
     * @param date2       the second date
     * @param event       the current event
     * @param defaultCase the default OutputCase
     * @return which OutputCase is needed
     */
    public static OutputCase eventType(ChronoLocalDate date1, ChronoLocalDate date2, Event event,
                                       OutputCase defaultCase) {
        if (date1.isEqual(date2)) {
            if (isAllDay(event)) {
                return OutputCase.ALLDAYLONG;
            }
            return OutputCase.SINGLEDAY;
        }
        return defaultCase;
    }

    /**
     * @param event google calendar event
     * @return if the event has no time stamps (all day event) or not
     */
    public static boolean isAllDay(Event event) {
        return event.getStart().getDate() != null;
    }

    /**
     * @param event google calendar event
     * @return the start of the event as LocalDate
     */
    public static LocalDate getLocalDateStart(Event event) {
        if (isAllDay(event)) {
            return LocalDate.parse(event.getStart().getDate().toString());
        }
        return LocalDate.parse(event.getStart().getDateTime().toString().substring(0, 10));
    }

    /**
     * @param event google calendar event
     * @return the end of the event as LocalDate
     */
    public static LocalDate getLocalDateEnd(Event event) {
        if (isAllDay(event)) {
            return LocalDate.parse(event.getEnd().getDate().toString()).minusDays(1);
        }
        return LocalDate.parse(event.getEnd().getDateTime().toString().substring(0, 10));
    }

    /**
     * @param event google calendar event
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
     * @param event google calendar event
     * @return the end of the event as LocalDateTime
     */
    public LocalDateTime getLocalDateTimeEnd(Event event) {
        if (isAllDay(event)) {
            return LocalDateTime.parse(event.getEnd().getDate().toString() + "T23:59:59.999", formatter).minusDays(1);
        }
        return ZonedDateTime.parse(event.getEnd().getDateTime().toString()).withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * @param date a date as LocalDate
     * @return String in Natural Language of the date, e.g. "12th of May"
     */
    public String getDate(LocalDateTime date) {
        return ordinal(date.getDayOfMonth()) + " of " + date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    /**
     * A method to convert the integer day to an ordinal (from 1 to 31)
     *
     * @param i the day as integer
     * @return the day as ordinal, e.g. 1st
     */
    public static String ordinal(int i) {
        String[] ordinals = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        if (10 < i && i < 14) {
            return i + "th";
        }
        return i + ordinals[i % 10];
    }
}
