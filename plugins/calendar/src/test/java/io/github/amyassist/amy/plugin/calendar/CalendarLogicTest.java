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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Tests for the CalendarLogic class
 *
 * @author Patrick Gebhardt, Florian Bauer
 */
@ExtendWith(FrameworkExtension.class)
public class CalendarLogicTest {

	@Reference
	private TestFramework framework;

	private CalendarLogic callog;
	private CalendarService calendarService;
	
	private Event event1;
	private Event event2;

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.calendarService = this.framework.mockService(CalendarService.class);
		this.framework.mockService(Environment.class);
		this.callog = this.framework.setServiceUnderTest(CalendarLogic.class);
		this.event1 = eventAllDay("All Day Event", "2015-05-29", "2015-05-30");
        this.event2 = event("Normal Event", "2015-05-28T15:30:00", "2015-05-29T15:30:00");
	}

	/**
	 * Tests the setEvent() method
	 *
	 * @param calendarEvent an Test Event of type CalendarEvent
	 */
	@ParameterizedTest
	@MethodSource("testSetEvent")
	public void testCheckSetEvent(CalendarEvent calendarEvent) {
		this.callog.setEvent(calendarEvent);
		boolean allDay = calendarEvent.isAllDay();
		Mockito.verify(this.calendarService).addEvent(ArgumentMatchers.eq("primary"),
				ArgumentMatchers.argThat(event -> {
					return event.getSummary().equals(calendarEvent.getSummary()) // compare summary
							&& event.getLocation().equals(calendarEvent.getLocation()) // compare location
							&& event.getDescription().equals(calendarEvent.getDescription()) // compare description
							&& ((event.getStart().getDate() != null) == allDay) // compare all day status
							&& checkDates(calendarEvent, event, allDay) // compare start and end date
							&& event.getReminders().equals(calendarEvent.getReminders()); // compare reminders
				}));
	}

	/**
	 *
	 * @return the test cases used in the {@link #testCheckSetEvent(CalendarEvent)} test
	 */
	public static Stream<CalendarEvent> testSetEvent() {
		return Stream.of(
				new CalendarEvent(LocalDateTime.parse("2018-08-12T15:00:00"),
						LocalDateTime.parse("2018-08-12T18:00:00"), "Testing", "home", "test the addEvent code", "mail",
						20, "RRULE:FREQ=WEEKLY", false),
				new CalendarEvent(LocalDateTime.parse("2018-08-15T00:00"), LocalDateTime.parse("2018-08-16T00:00"),
						"Testing", "home", "test the addEvent code", "popup", 85, "", true));
	}

	/**
	 *
	 * @param localDateTime
	 *            the LocalDateTime which will be converted into EventDateTime
	 * @param allDay
	 *            differentiate between only Date oder DateTime
	 * @return EventDateTime
	 */
	private static EventDateTime EDTfromLocalDateTime(LocalDateTime localDateTime, boolean allDay) {
		ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
		EventDateTime edt = new EventDateTime();
		if (allDay) {
			edt.setDate(new DateTime(true, zdt.toInstant().toEpochMilli(), 0));
		} else {
			edt.setDateTime(new DateTime(zdt.toInstant().toEpochMilli()));
		}
		return edt;
	}

	/**
	 *
	 * @param calendarEvent an Event of CalendarEvent type
	 * @param event an Event of google api Event type
	 * @param allDay if it is an all day event
	 * @return if both events have the exact same start and end date
	 */
	private static boolean checkDates(CalendarEvent calendarEvent, Event event, boolean allDay){
		boolean startCorrect = false;
		boolean endCorrect = false;
		if (allDay) {
			startCorrect = event.getStart().getDate()
					.equals(EDTfromLocalDateTime(calendarEvent.getStart(), allDay).getDate());
			endCorrect = event.getEnd().getDate()
					.equals(EDTfromLocalDateTime(calendarEvent.getEnd(), allDay).getDate());
		} else {
			startCorrect = event.getStart().getDateTime()
					.equals(EDTfromLocalDateTime(calendarEvent.getStart(), allDay).getDateTime());
			endCorrect = event.getEnd().getDateTime()
					.equals(EDTfromLocalDateTime(calendarEvent.getEnd(), allDay).getDateTime());
		}
		return startCorrect && endCorrect;
	}
	
	/**
     *  Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarLogic#getDateTime(LocalDateTime, int)}.
     */
    @Test
    void testGetDateTime() {
        assertThat(this.callog.getDateTime(LocalDateTime.of(2018, 9, 20, 12, 00), 0), 
        		is(new DateTime(ZonedDateTime.of(2018, 9, 20, 0, 0, 0, 0, ZoneId.systemDefault())
        				.toInstant().toEpochMilli())));
    }
	
    /**
     *  Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarLogic#isAllDay(Event)}.
     */
    @Test
    void testIsAllDay() {
        assertThat(CalendarLogic.isAllDay(this.event1), is(true));
        assertThat(CalendarLogic.isAllDay(this.event2), is(false));
    }
    
    /**
     *  Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarLogic#getLocalDateTimeStart(Event)}.
     */
    @Test
    void testGetLocalDateTimeStart() {
        assertThat(this.callog.getLocalDateTimeStart(this.event1), is(LocalDateTime.of(2015, 05, 29, 00, 00)));
        assertThat(this.callog.getLocalDateTimeStart(this.event2), is(LocalDateTime.of(2015, 05, 28, 15, 30)));
    }
    
    /**
     *  Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarLogic#getLocalDateTimeEnd(Event)}.
     */
    @Test
    void testGetLocalDateTimeEnd() {
        assertThat(this.callog.getLocalDateTimeEnd(this.event1), 
        		is(LocalDateTime.of(2015, 05, 29, 23, 59, 59, 999000000)));
        assertThat(this.callog.getLocalDateTimeEnd(this.event2), is(LocalDateTime.of(2015, 05, 29, 15, 30)));
    }
    
    private static Event event(String summary, String start, String end) {
        Event event = new Event();
        event.setSummary(summary);
        event.setStart(new EventDateTime().setDateTime(fromISO(start)));
        event.setEnd(new EventDateTime().setDateTime(fromISO(end)));
        return event;
    }

    private static Event eventAllDay(String summary, String start, String end) {
        Event event = new Event();
        event.setSummary(summary);
        event.setStart(new EventDateTime().setDate(new DateTime(start)));
        event.setEnd(new EventDateTime().setDate(new DateTime(end)));
        return event;
    }

    
    /**
     * @param iso
     *            date-time without a time-zone
     * @return the DateTime of a String with correct time-zone
     */
    private static DateTime fromISO(String iso) {
        return fromLocalDateTime(LocalDateTime.parse(iso));
    }

    /**
     *
     * @param localDateTime
     *            the LocalDateTime which will be converted into EventDateTime
     * @return DateTime
     */
    private static DateTime fromLocalDateTime(LocalDateTime localDateTime) {
        return new DateTime(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }


}
