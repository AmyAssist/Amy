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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for calendar speech
 * @author Lars Buttgereit, Florian Bauer
 */
@ExtendWith(FrameworkExtension.class)
class CalendarSpeechTest {

	@Reference
	private TestFramework framework;

	private Environment environment;
	
	private CalendarLogic logic;
	private CalendarSpeech speech;
	private CalendarEvent event;

	private List<CalendarEvent> events;
	private List<CalendarEvent> events2;
	
	private EntityData number;
	private EntityData date;
	private static EntityData allDay;
	private static EntityData startdate;
	private static EntityData enddate;
	private static EntityData location;
	private static EntityData description;
	private static EntityData remindertype;
	private static EntityData remindertimevalue;
	private static EntityData remindertimeunit;
	private static EntityData title;
	
	
	/**
	 * 
	 */
	@BeforeEach
	void init() {
		this.logic = this.framework.mockService(CalendarLogic.class);
		this.environment = this.framework.mockService(Environment.class);
		this.speech = this.framework.setServiceUnderTest(CalendarSpeech.class);
		this.event = new CalendarEvent("ID", LocalDateTime.of(2018, 9, 26, 10, 00),	
				LocalDateTime.of(2018, 9, 26, 12, 00), "Summary", "", "", false);
		this.events = Arrays.asList(this.event);
		this.events2 = Arrays.asList(this.event, this.event);
		mockEntityData();
	}
	
	private void mockEntityData() {
		this.number = Mockito.mock(EntityData.class);
		this.date = Mockito.mock(EntityData.class);
		allDay = Mockito.mock(EntityData.class);
		startdate = Mockito.mock(EntityData.class);
		enddate = Mockito.mock(EntityData.class);
		location = Mockito.mock(EntityData.class);
		description = Mockito.mock(EntityData.class);
		remindertype = Mockito.mock(EntityData.class);
		remindertimevalue = Mockito.mock(EntityData.class);
		remindertimeunit = Mockito.mock(EntityData.class);
		title = Mockito.mock(EntityData.class);		
	}
	
	/**
	 * test getEvents()
	 */
	@Test
	void testGetEvents() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(1);
		map.put("number", this.number);
		String response = this.speech.getEvents(map);
		assertThat(response, equalToIgnoringWhiteSpace("No events found."));
	}
	
	/**
	 * test getEvents()
	 */
	@Test
	void testGetEvents2() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(1);
		map.put("number", this.number);
		when(this.logic.getEvents(1)).thenReturn(this.events);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		String response = this.speech.getEvents(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have following upcoming event: Summary from 10:00 am until 12:00 pm."));
	}
	
	/**
	 * test getEvents()
	 */
	@Test
	void testGetEvents3() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(2);
		map.put("number", this.number);
		when(this.logic.getEvents(2)).thenReturn(this.events2);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		String response = this.speech.getEvents(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have following upcoming 2 events: Summary from 10:00 am "
				+ "until 12:00 pm. Summary from 10:00 am until 12:00 pm."));
	}
	
	/**
	 * test getEventsAt() with today
	 */
	@Test
	void testGetEventsToday() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.date.getDate()).thenReturn(LocalDate.of(2018, 9, 20));
		when(this.date.getString()).thenReturn("today");
		map.put("date", this.date);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		String response = this.speech.getEventsAt(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have no events today."));
	}
	
	/**
	 * test getEventsAt() with today and events
	 */
	@Test
	void testGetEventsToday2() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.date.getDate()).thenReturn(LocalDate.of(2018, 9, 26));
		when(this.date.getString()).thenReturn("today");
		map.put("date", this.date);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 26, 8, 00));
		when(this.logic.getEventsAt(LocalDateTime.of(2018, 9, 26, 00, 00))).thenReturn(this.events);
		String response = this.speech.getEventsAt(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have following events today: Summary from 10:00 am until 12:00 pm."));
	}
	
	/**
	 * test getEventsAt() with tomorrow
	 */
	@Test
	void testGetEventsTomorrow() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.date.getDate()).thenReturn(LocalDate.of(2018, 9, 21));
		when(this.date.getString()).thenReturn("tomorrow");
		map.put("date", this.date);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		String response = this.speech.getEventsAt(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have no events tomorrow."));
	}
	
	/**
	 * test getEventsAt() with 20/9/2018
	 */
	@Test
	void testGetEventsAt() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.date.getDate()).thenReturn(LocalDate.of(2018, 9, 20));
		when(this.date.getString()).thenReturn("");
		map.put("date", this.date);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 21, 10, 00));
		String response = this.speech.getEventsAt(map);
		assertThat(response, equalToIgnoringWhiteSpace("No events found for the 20th of september 2019."));
	}
	
	/**
	 * test getEventsToday() with 20/9/2018 and events
	 */
	@Test
	void testGetEventsAt2() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.date.getDate()).thenReturn(LocalDate.of(2018, 9, 20));
		when(this.date.getString()).thenReturn("");
		map.put("date", this.date);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		when(this.logic.getEventsAt(LocalDateTime.of(2018, 9, 20, 00, 00))).thenReturn(this.events);
		String response = this.speech.getEventsAt(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have following events on the 20th of september 2018: "
				+ "Summary from 10:00 am until 12:00 pm."));
}
	
	/**
	 * Test method for setEvent(Map<String,EntityData>)
	 */
	@Test
	void testSetEvent() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 00, 00));
		when(allDay.getString()).thenReturn("yes");
		map.put("allday", allDay);
		when(startdate.getDate()).thenReturn(LocalDate.of(2018, 9, 16));
		map.put("startdate", startdate);
		when(enddate.getDate()).thenReturn(LocalDate.of(2018, 9, 19));
		map.put("enddate", enddate);
		map.put("startyear", null);
		map.put("endyear", null);
		when(location.getString()).thenReturn("no");
		map.put("location", location);
		when(description.getString()).thenReturn("no");
		map.put("description", description);
		when(remindertimevalue.getNumber()).thenReturn(20);
		map.put("remindertimevalue", remindertimevalue);
		when(remindertimeunit.getString()).thenReturn("days");
		map.put("remindertimeunit", remindertimeunit);
		when(title.getString()).thenReturn("Okay");
		map.put("title", title);
		when(remindertype.getString()).thenReturn("email");
		map.put("remindertype", remindertype);
		CalendarEvent event2 = new CalendarEvent(LocalDateTime.of(2019, 9, 17, 00, 00), 
				LocalDateTime.of(2019, 9, 21, 00, 00), "Okay", "", "", "email", 28800, "", true);
		this.speech.setEvent(map);
		verify(this.logic).setEvent(event2);		
	}
	
	/**
	 * Test method for setEvent(Map<String,EntityData>)
	 */
	@Test
	void testSetEvent2() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 00, 00));
		when(allDay.getString()).thenReturn("yes");
		map.put("allday", allDay);
		when(startdate.getDate()).thenReturn(LocalDate.of(2018, 9, 16));
		map.put("startdate", startdate);
		when(enddate.getDate()).thenReturn(LocalDate.of(2018, 9, 19));
		map.put("enddate", enddate);
		map.put("startyear", null);
		map.put("endyear", null);
		when(location.getString()).thenReturn("location");
		map.put("location", location);
		when(description.getString()).thenReturn("description");
		map.put("description", description);
		when(remindertimevalue.getNumber()).thenReturn(20);
		map.put("remindertimevalue", remindertimevalue);
		when(remindertimeunit.getString()).thenReturn("hours");
		map.put("remindertimeunit", remindertimeunit);
		when(title.getString()).thenReturn("Okay");
		map.put("title", title);
		when(remindertype.getString()).thenReturn("email");
		map.put("remindertype", remindertype);
		CalendarEvent event2 = new CalendarEvent(LocalDateTime.of(2019, 9, 17, 00, 00), 
				LocalDateTime.of(2019, 9, 21, 00, 00), "Okay", "location", "description", "email", 1200, "", true);
		this.speech.setEvent(map);
		verify(this.logic).setEvent(event2);		
	}
	
	/**
	 * @param testCase
	 *            a combination of the input variables and the expected outcome
	 */
	@ParameterizedTest
	@MethodSource("testNewEvents")
	public void testSetEvent(Pair<Map<String, EntityData>, String> testCase) {
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 00, 00));
		String setEvent = this.speech.setEvent(testCase.getLeft());
		assertThat(setEvent, equalToIgnoringWhiteSpace(testCase.getRight()));
	}
	
	/**
	 *
	 * @return the test cases used in the {@link #testSetEvent(Pair)} test
	 */
	public static Stream<Pair<Map<String, EntityData>, String>> testNewEvents() {
		return Stream.of(Pair.of(speechMap("no", LocalDate.of(2018, 9, 21), LocalDate.of(2018, 9, 21), "", "", null, null),  
				"You have to restart the creation of a new event and please make sure that you add a time to the start"
				+ " and to the end if you choose an non all day event."),
				Pair.of(speechMap("no", LocalDate.of(2018, 9, 16), LocalDate.of(2018, 9, 16), "", "2018", LocalTime.of(12, 00), null),  
						"You have to restart the creation of a new event and please make sure that you add a time to the start"
						+ " and to the end if you choose an non all day event."),
				Pair.of(speechMap("false", LocalDate.of(2018, 9, 16), LocalDate.of(2018, 9, 16), "2018", "", LocalTime.of(12, 01), LocalTime.of(12, 00)),  
						"You have to restart the creation of a new event and please make sure that the start of the event "
								+ "is before the end."),
				Pair.of(speechMap("yes", LocalDate.of(2018, 9, 21), LocalDate.of(2018, 9, 15), "", "", LocalTime.of(12, 00), LocalTime.of(12, 00)),  
						"You have to restart the creation of a new event and please make sure that the start of the event "
								+ "is before the end."),
				Pair.of(speechMap("true", LocalDate.of(2018, 9, 21), LocalDate.of(2018, 9, 15), "2018", "", LocalTime.of(12, 00), LocalTime.of(12, 00)),  
						"You have to restart the creation of a new event and please make sure that the start of the event "
								+ "is before the end."),
				Pair.of(speechMap("all day", LocalDate.of(2018, 9, 21), LocalDate.of(2018, 9, 15), "", "2018", LocalTime.of(12, 00), LocalTime.of(12, 00)),  
						"You have to restart the creation of a new event and please make sure that the start of the event "
								+ "is before the end."));
	}
	
	private static Map<String, EntityData> speechMap(String alldayString, LocalDate localStartDate, LocalDate localEndDate, String startYear,
			String endYear, LocalTime startTime, LocalTime endTime) {
		EntityData allday = Mockito.mock(EntityData.class);
		EntityData startDate = Mockito.mock(EntityData.class);
		EntityData startyear = Mockito.mock(EntityData.class);
		EntityData starttime = Mockito.mock(EntityData.class);
		EntityData endDate = Mockito.mock(EntityData.class);
		EntityData endyear = Mockito.mock(EntityData.class);
		EntityData endtime = Mockito.mock(EntityData.class);
		EntityData Location = Mockito.mock(EntityData.class);
		EntityData Description = Mockito.mock(EntityData.class);
		EntityData reminderTimeValue = Mockito.mock(EntityData.class);
		EntityData reminderTimeUnit = Mockito.mock(EntityData.class);
		EntityData Title = Mockito.mock(EntityData.class);
		EntityData reminderType = Mockito.mock(EntityData.class);
		Map<String, EntityData> map = new HashMap<>();
		when(allday.getString()).thenReturn(alldayString);
		map.put("allday", allday);
		when(startDate.getDate()).thenReturn(localStartDate);
		map.put("startdate", startDate);
		when(endDate.getDate()).thenReturn(localEndDate);
		map.put("enddate", endDate);
		if (startYear.isEmpty()) {
			map.put("startyear", null);
		} else {
			map.put("startyear", startyear);
		}
		if (endYear.isEmpty()) {
			map.put("endyear", null);
		} else {
			map.put("endyear", endyear);
		}
		if (startTime == null) {
			map.put("starttime", null);
		} else {
			when(starttime.getTime()).thenReturn(startTime);
			map.put("starttime", starttime);
		}
		if (endTime == null) {
			map.put("endtime", null);
		} else {
			when(endtime.getTime()).thenReturn(endTime);
			map.put("endtime", endtime);
		}
		when(Location.getString()).thenReturn("test");
		map.put("location", Location);
		when(Description.getString()).thenReturn("test");
		map.put("description", Description);
		when(reminderTimeValue.getNumber()).thenReturn(20);
		map.put("remindertimevalue", reminderTimeValue);
		when(reminderTimeUnit.getString()).thenReturn("days");
		map.put("remindertimeunit", reminderTimeUnit);
		when(Title.getString()).thenReturn("Okay");
		map.put("title", Title);
		when(reminderType.getString()).thenReturn("email");
		map.put("remindertype", reminderType);
		return map;
	}	

	/**
	 * @param testCase
	 *            a combination of the input variables and the expected outcome
	 */
	@ParameterizedTest
	@MethodSource("testEventsWithDate")
	public void testCheckDayWithDate(Pair<CalendarEvent, String> testCase) {
		String checkDay = this.speech.checkDate(LocalDateTime.parse("2015-05-28T09:00:00"), testCase.getLeft(), true);
		assertThat(checkDay, equalToIgnoringWhiteSpace(testCase.getRight()));
	}

	/**
	 * @param testCase
	 *            a combination of the input variables and the expected outcome
	 */
	@ParameterizedTest
	@MethodSource("testEventsWithoutDate")
	public void testCheckDayWithoutDate(Pair<CalendarEvent, String> testCase) {
		String checkDay = this.speech.checkDate(LocalDateTime.parse("2015-05-28T09:00:00"), testCase.getLeft(), false);
		assertThat(checkDay, equalToIgnoringWhiteSpace(testCase.getRight()));
	}

	/**
	 *
	 * @return the test cases used in the {@link #testCheckDayWithDate(Pair)} test
	 */
	public static Stream<Pair<CalendarEvent, String>> testEventsWithDate() {
		return Stream.of(
				Pair.of(event("test event", "2015-05-29T08:00:00", "2015-05-29T09:30:00"),
						"test event on the 29th of may at 08:00 am until 09:30 am."),
				Pair.of(event("write tests in Java", "2015-05-28T12:59:15", "2015-05-28T20:30:00"),
						"write tests in Java on the 28th of may at 12:59 pm until 08:30 pm."),
				Pair.of(event("event start in past", "2015-05-12T13:05:00", "2015-05-29T15:30:00"),
						"event start in past since the 12th of may at 01:05 pm until the 29th of may at 03:30 pm."),
				Pair.of(event("event same day start past", "2015-05-28T04:35:02", "2015-05-28T10:15:00"),
						"event same day start past on the 28th of may at 04:35 am until 10:15 am."),
				Pair.of(event("event end in future", "2015-05-28T07:30:00", "2015-05-29T12:00:00"),
						"event end in future since the 28th of may at 07:30 am until the 29th of may at 12:00 pm."),
				Pair.of(event("event end today", "2015-05-27T22:00:00", "2015-05-28T10:00:00"),
						"event end today since the 27th of may at 10:00 pm until the 28th of may at 10:00 am."),
				Pair.of(event("event start today end tomorrow", "2015-05-28T15:30:00", "2015-05-29T15:30:00"),
						"event start today end tomorrow from the 28th of may at 03:30 pm until the 29th of may at 03:30 pm."),
				Pair.of(eventAllDay("event tomorrow all day", "2015-05-29", "2015-05-29"),
						"event tomorrow all day on the 29th of may all day long."),
				Pair.of(eventAllDay("", "2015-05-29", "2015-05-29"),
						"An event without a title on the 29th of may all day long."),
				Pair.of(eventAllDay("", "2015-05-29", "2015-05-29"),
						"An event without a title on the 29th of may all day long."));
	}

	/**
	 *
	 * @return the test cases used in the {@link #testCheckDayWithoutDate(Pair)} test
	 */
	public static Stream<Pair<CalendarEvent, String>> testEventsWithoutDate() {
		return Stream.of(
				Pair.of(event("event later that day", "2015-05-28T14:00:00", "2015-05-28T16:30:00"),
						"event later that day from 02:00 pm until 04:30 pm."),
				Pair.of(eventAllDay("event all day", "2015-05-28", "2015-05-28"), "event all day all day long."),
				Pair.of(event("event finish today", "2015-05-27T23:00:00", "2015-05-28T10:00:00"),
						"event finish today since the 27th of may at 11:00 pm until 10:00 am."));
	}

	private static CalendarEvent event(String summary, String start, String end) {
		return new CalendarEvent("", fromStart(start), fromEnd(end), summary, "", "", false);
	}

	private static CalendarEvent eventAllDay(String summary, String start, String end) {
		return new CalendarEvent("", fromStart(start), fromEnd(end), summary, "", "", true);
	}

	/**
	 * @param input
	 *            date string
	 * @return the LocalDateTime of a String
	 */
	private static LocalDateTime fromStart(String input) {
		if (input.contains("T")) {
			return LocalDateTime.parse(input);
		}
		String modifiedInput = input + "T00:00";
		return LocalDateTime.parse(modifiedInput);
	}
	
	/**
	 * @param input
	 *            date string
	 * @return the LocalDateTime of a String
	 */
	private static LocalDateTime fromEnd(String input) {
		if (input.contains("T")) {
			return LocalDateTime.parse(input);
		}
		String modifiedInput = input + "T23:59";
		return LocalDateTime.parse(modifiedInput);
	}


}
