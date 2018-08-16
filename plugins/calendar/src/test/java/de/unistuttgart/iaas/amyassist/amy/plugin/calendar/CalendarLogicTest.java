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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
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

	@ParameterizedTest
	@MethodSource("testSetEvent")
	public void testCheckSetEvent(CalendarEvent calendarEvent) {
		this.callog.setEvent(calendarEvent);
		boolean allDay = calendarEvent.isAllDay();
		Mockito.verify(this.calendarService).addEvent(ArgumentMatchers.eq("primary"),
				ArgumentMatchers.argThat(event -> {
					return event.getSummary().equals(calendarEvent.getSummary())
							&& event.getLocation().equals(calendarEvent.getLocation())
							&& event.getDescription().equals(calendarEvent.getDescription())
							&& ((event.getStart().getDate() != null) == allDay)
							&& allDay ? (event.getStart().getDate()
							.equals(EDTfromLocalDateTime(calendarEvent.getStart(),allDay).getDate())
							&& event.getEnd().getDate()
							.equals(EDTfromLocalDateTime(calendarEvent.getEnd(), allDay).getDate())) :
							(event.getStart().getDateTime()
									.equals(EDTfromLocalDateTime(calendarEvent.getStart(), allDay).getDateTime())
									&& event.getEnd().getDateTime()
									.equals(EDTfromLocalDateTime(calendarEvent.getEnd(), allDay).getDateTime()))
	 						&& event.getRecurrence().equals(Arrays.asList(calendarEvent.getRecurrence()))
							&& event.getReminders().equals(calendarEvent.getReminders())
 							;
				}));
	}

	/**
	 *
	 * @return the test cases used in the {@link #testCheckSetEvent(CalendarEvent)} test
	 */
	public static Stream<CalendarEvent> testSetEvent() {
		return Stream.of(new CalendarEvent(LocalDateTime.parse("2018-08-12T15:00:00"),
				LocalDateTime.parse("2018-08-12T18:00:00"), "Testing", "home", "test the addEvent code", "mail", 20,
				"RRULE:FREQ=WEEKLY", false),
				new CalendarEvent(LocalDateTime.parse("2018-08-15T00:00"),
						LocalDateTime.parse("2018-08-16T00:00"), "Testing", "home", "test the addEvent code", "popup", 85,
						"", true));
	}

	/**
	 * @param testCase
	 *            a combination of the input variables and the expected outcome
	 */
	@ParameterizedTest
	@MethodSource("testEventsWithDate")
	public void testCheckDayWithDate(Pair<Event, String> testCase) {
		String checkDay = this.callog.checkDay(LocalDateTime.parse("2015-05-28T09:00:00"), testCase.getLeft(), true);
		assertThat(checkDay, equalToIgnoringWhiteSpace(testCase.getRight()));
	}

	/**
	 * @param testCase
	 *            a combination of the input variables and the expected outcome
	 */
	@ParameterizedTest
	@MethodSource("testEventsWithoutDate")
	public void testCheckDayWithoutDate(Pair<Event, String> testCase) {
		String checkDay = this.callog.checkDay(LocalDateTime.parse("2015-05-28T09:00:00"), testCase.getLeft(), false);
		assertThat(checkDay, equalToIgnoringWhiteSpace(testCase.getRight()));
	}

	/**
	 *
	 * @return the test cases used in the {@link #testCheckDayWithDate(Pair)} test
	 */
	public static Stream<Pair<Event, String>> testEventsWithDate() {
		return Stream.of(
				Pair.of(event("test event", "2015-05-29T08:00:00", "2015-05-29T09:30:00"),
						"test event on the 29th of may at 08:00 until 09:30."),
				Pair.of(event("write tests in Java", "2015-05-28T12:59:15", "2015-05-28T20:30:00"),
						"write tests in Java on the 28th of may at 12:59 until 20:30."),
				Pair.of(event("event start in past", "2015-05-12T13:05:00", "2015-05-29T15:30:00"),
						"event start in past since the 12th of may at 13:05 until the 29th of may at 15:30."),
				Pair.of(event("event same day start past", "2015-05-28T04:35:02", "2015-05-28T10:15:00"),
						"event same day start past on the 28th of may at 04:35 until 10:15."),
				Pair.of(event("event end in future", "2015-05-28T07:30:00", "2015-05-29T12:00:00"),
						"event end in future since the 28th of may at 07:30 until the 29th of may at 12:00."),
				Pair.of(event("event end today", "2015-05-27T22:00:00", "2015-05-28T10:00:00"),
						"event end today since the 27th of may at 22:00 until the 28th of may at 10:00."),
				Pair.of(event("event start today end tomorrow", "2015-05-28T15:30:00", "2015-05-29T15:30:00"),
						"event start today end tomorrow from the 28th of may at 15:30 until the 29th of may at 15:30."),
				Pair.of(eventAllDay("event tomorrow all day", "2015-05-29", "2015-05-30"),
						"event tomorrow all day on the 29th of may all day long."));
	}

	/**
	 *
	 * @return the test cases used in the {@link #testCheckDayWithoutDate(Pair)} test
	 */
	public static Stream<Pair<Event, String>> testEventsWithoutDate() {
		return Stream.of(
				Pair.of(event("event later that day", "2015-05-28T14:00:00", "2015-05-28T16:30:00"),
						"event later that day from 14:00 until 16:30."),
				Pair.of(eventAllDay("event all day", "2015-05-28", "2015-05-29"), "event all day all day long."),
				Pair.of(event("event finish today", "2015-05-27T23:00:00", "2015-05-28T10:00:00"),
						"event finish today since the 27th of may at 23:00 until 10:00."));
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
	 * 				the LocalDateTime which will be converted into EventDateTime
	 * @return DateTime
	 */
	private static DateTime fromLocalDateTime(LocalDateTime localDateTime) {
		return new DateTime(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
	}

	/**
	 *
	 * @param localDateTime
	 * 				the LocalDateTime which will be converted into EventDateTime
	 * @param allDay
	 * 				differentiate between only Date oder DateTime
	 * @return EventDateTime
	 */
	private static EventDateTime EDTfromLocalDateTime(LocalDateTime localDateTime, boolean allDay) {
		DateTime dt = fromLocalDateTime(localDateTime);
		EventDateTime edt = new EventDateTime();
		if (allDay) {
			edt.setDate(dt);
		} else {
			edt.setDateTime(dt);
		}
		return edt;
	}

}
