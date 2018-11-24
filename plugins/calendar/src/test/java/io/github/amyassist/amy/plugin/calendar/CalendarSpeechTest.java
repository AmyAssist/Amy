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

package io.github.amyassist.amy.plugin.calendar;

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

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.natlang.EntityData;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

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

	private List<CalendarEvent> oneEvent;
	private List<CalendarEvent> twoEvents;
	
	private EntityData numberEntity;
	private EntityData dateEntity;
	private static EntityData alldayEntity;
	private static EntityData startdateEntity;
	private static EntityData enddateEntity;
	private static EntityData locationEntity;
	private static EntityData descriptionEntity;
	private static EntityData remindertypeEntity;
	private static EntityData remindertimevalueEntity;
	private static EntityData remindertimeunitEntity;
	private static EntityData titleEntity;
	
	
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
		this.oneEvent = Arrays.asList(this.event);
		this.twoEvents = Arrays.asList(this.event, this.event);
		mockEntityData();
	}
	
	private void mockEntityData() {
		this.numberEntity = Mockito.mock(EntityData.class);
		this.dateEntity = Mockito.mock(EntityData.class);
		alldayEntity = Mockito.mock(EntityData.class);
		startdateEntity = Mockito.mock(EntityData.class);
		enddateEntity = Mockito.mock(EntityData.class);
		locationEntity = Mockito.mock(EntityData.class);
		descriptionEntity = Mockito.mock(EntityData.class);
		remindertypeEntity = Mockito.mock(EntityData.class);
		remindertimevalueEntity = Mockito.mock(EntityData.class);
		remindertimeunitEntity = Mockito.mock(EntityData.class);
		titleEntity = Mockito.mock(EntityData.class);		
	}
	
	/**
	 * test getEvents() with asking for one event and getting an empty list
	 */
	@Test
	void testGetEventsWithNoEvents() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.numberEntity.getNumber()).thenReturn(1);
		map.put("number", this.numberEntity);
		String response = this.speech.getEvents(map);
		assertThat(response, equalToIgnoringWhiteSpace("No events found."));
	}
	
	/**
	 * test getEvents() with asking for one event and getting an event as response
	 */
	@Test
	void testGetEventsWithOneEvent() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.numberEntity.getNumber()).thenReturn(1);
		map.put("number", this.numberEntity);
		when(this.logic.getEvents(1)).thenReturn(this.oneEvent);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		String response = this.speech.getEvents(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have following upcoming event: Summary from 10:00 am until 12:00 pm."));
	}
	
	/**
	 * test getEvents() with asking for two event and getting two events as response
	 */
	@Test
	void testGetEventsWithTwoEvents() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.numberEntity.getNumber()).thenReturn(2);
		map.put("number", this.numberEntity);
		when(this.logic.getEvents(2)).thenReturn(this.twoEvents);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		String response = this.speech.getEvents(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have following upcoming 2 events: Summary from 10:00 am "
				+ "until 12:00 pm. Summary from 10:00 am until 12:00 pm."));
	}
	
	/**
	 * test getEventsAt() with today and getting an empty list
	 */
	@Test
	void testGetEventsTodayWithNoEvent() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.dateEntity.getDate()).thenReturn(LocalDate.of(2018, 9, 20));
		when(this.dateEntity.getString()).thenReturn("today");
		map.put("date", this.dateEntity);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		String response = this.speech.getEventsAt(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have no events today."));
	}
	
	/**
	 * test getEventsAt() with today and getting one event as response
	 */
	@Test
	void testGetEventsTodayWithOneEvent() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.dateEntity.getDate()).thenReturn(LocalDate.of(2018, 9, 26));
		when(this.dateEntity.getString()).thenReturn("today");
		map.put("date", this.dateEntity);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 26, 8, 00));
		when(this.logic.getEventsAt(LocalDateTime.of(2018, 9, 26, 00, 00))).thenReturn(this.oneEvent);
		String response = this.speech.getEventsAt(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have following events today: Summary from 10:00 am until 12:00 pm."));
	}
	
	/**
	 * test getEventsAt() with tomorrow and getting an empty list
	 */
	@Test
	void testGetEventsTomorrow() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.dateEntity.getDate()).thenReturn(LocalDate.of(2018, 9, 21));
		when(this.dateEntity.getString()).thenReturn("tomorrow");
		map.put("date", this.dateEntity);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		String response = this.speech.getEventsAt(map);
		assertThat(response, equalToIgnoringWhiteSpace("You have no events tomorrow."));
	}
	
	/**
	 * test getEventsAt() with 20/9/2018 and getting an empty list
	 */
	@Test
	void testGetEventsAt() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.dateEntity.getDate()).thenReturn(LocalDate.of(2018, 9, 20));
		when(this.dateEntity.getString()).thenReturn("");
		map.put("date", this.dateEntity);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 21, 10, 00));
		String response = this.speech.getEventsAt(map);
		assertThat(response, equalToIgnoringWhiteSpace("No events found for the 20th of september 2019."));
	}
	
	/**
	 * test getEventsToday() with 20/9/2018 and getting one event as response
	 */
	@Test
	void testGetEventsAtWithOneEvent() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.dateEntity.getDate()).thenReturn(LocalDate.of(2018, 9, 20));
		when(this.dateEntity.getString()).thenReturn("");
		map.put("date", this.dateEntity);
		map.put("eventyear", null);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		when(this.logic.getEventsAt(LocalDateTime.of(2018, 9, 20, 00, 00))).thenReturn(this.oneEvent);
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
		when(alldayEntity.getString()).thenReturn("yes");
		map.put("allday", alldayEntity);
		when(startdateEntity.getDate()).thenReturn(LocalDate.of(2018, 9, 16));
		map.put("startdate", startdateEntity);
		when(enddateEntity.getDate()).thenReturn(LocalDate.of(2018, 9, 19));
		map.put("enddate", enddateEntity);
		map.put("startyear", null);
		map.put("endyear", null);
		when(locationEntity.getString()).thenReturn("no");
		map.put("location", locationEntity);
		when(descriptionEntity.getString()).thenReturn("no");
		map.put("description", descriptionEntity);
		when(remindertimevalueEntity.getNumber()).thenReturn(20);
		map.put("remindertimevalue", remindertimevalueEntity);
		when(remindertimeunitEntity.getString()).thenReturn("days");
		map.put("remindertimeunit", remindertimeunitEntity);
		when(titleEntity.getString()).thenReturn("Okay");
		map.put("title", titleEntity);
		when(remindertypeEntity.getString()).thenReturn("email");
		map.put("remindertype", remindertypeEntity);
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
		when(alldayEntity.getString()).thenReturn("yes");
		map.put("allday", alldayEntity);
		when(startdateEntity.getDate()).thenReturn(LocalDate.of(2018, 9, 16));
		map.put("startdate", startdateEntity);
		when(enddateEntity.getDate()).thenReturn(LocalDate.of(2018, 9, 19));
		map.put("enddate", enddateEntity);
		map.put("startyear", null);
		map.put("endyear", null);
		when(locationEntity.getString()).thenReturn("location");
		map.put("location", locationEntity);
		when(descriptionEntity.getString()).thenReturn("description");
		map.put("description", descriptionEntity);
		when(remindertimevalueEntity.getNumber()).thenReturn(20);
		map.put("remindertimevalue", remindertimevalueEntity);
		when(remindertimeunitEntity.getString()).thenReturn("hours");
		map.put("remindertimeunit", remindertimeunitEntity);
		when(titleEntity.getString()).thenReturn("Okay");
		map.put("title", titleEntity);
		when(remindertypeEntity.getString()).thenReturn("email");
		map.put("remindertype", remindertypeEntity);
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
	
	private static Map<String, EntityData> speechMap(String allDay, LocalDate startDate, LocalDate endDate, String startYear,
			String endYear, LocalTime startTime, LocalTime endTime) {
		EntityData allday = Mockito.mock(EntityData.class);
		EntityData startdate = Mockito.mock(EntityData.class);
		EntityData startyear = Mockito.mock(EntityData.class);
		EntityData starttime = Mockito.mock(EntityData.class);
		EntityData enddate = Mockito.mock(EntityData.class);
		EntityData endyear = Mockito.mock(EntityData.class);
		EntityData endtime = Mockito.mock(EntityData.class);
		EntityData location = Mockito.mock(EntityData.class);
		EntityData description = Mockito.mock(EntityData.class);
		EntityData remindertimetalue = Mockito.mock(EntityData.class);
		EntityData remindertimeunit = Mockito.mock(EntityData.class);
		EntityData title = Mockito.mock(EntityData.class);
		EntityData remindertype = Mockito.mock(EntityData.class);
		Map<String, EntityData> map = new HashMap<>();
		when(allday.getString()).thenReturn(allDay);
		map.put("allday", allday);
		when(startdate.getDate()).thenReturn(startDate);
		map.put("startdate", startdate);
		when(enddate.getDate()).thenReturn(endDate);
		map.put("enddate", enddate);
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
		when(location.getString()).thenReturn("test");
		map.put("location", location);
		when(description.getString()).thenReturn("test");
		map.put("description", description);
		when(remindertimetalue.getNumber()).thenReturn(20);
		map.put("remindertimevalue", remindertimetalue);
		when(remindertimeunit.getString()).thenReturn("days");
		map.put("remindertimeunit", remindertimeunit);
		when(title.getString()).thenReturn("Okay");
		map.put("title", title);
		when(remindertype.getString()).thenReturn("email");
		map.put("remindertype", remindertype);
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
