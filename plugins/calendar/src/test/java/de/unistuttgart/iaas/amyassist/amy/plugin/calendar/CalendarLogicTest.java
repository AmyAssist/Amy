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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
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

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.calendarService = this.framework.mockService(CalendarService.class);
		this.framework.mockService(Environment.class);
		this.callog = this.framework.setServiceUnderTest(CalendarLogic.class);
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
	


}
