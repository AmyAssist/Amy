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
import java.util.HashMap;
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
	
	private CalendarLogic logic;
	private CalendarSpeech speech;
	
	
	private EntityData number;
	private EntityData day;
	private Environment environment;
	
	
	/**
	 * 
	 */
	@BeforeEach
	void init() {
		this.logic = this.framework.mockService(CalendarLogic.class);
		this.environment = this.framework.mockService(Environment.class);
		this.speech = this.framework.setServiceUnderTest(CalendarSpeech.class);
		this.number = Mockito.mock(EntityData.class);
		this.day = Mockito.mock(EntityData.class);
	}
	
	/**
	 * test get events
	 */
	@Test
	void testGetEvents() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(1);
		map.put("number", this.number);
		this.speech.getEvents(map);
		verify(this.logic).getEvents(1);
	}
	
	/**
	 * test getEventsToday() with today
	 */
	@Test
	void testGetEventsToday() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.day.getString()).thenReturn("today");
		map.put("day", this.day);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		this.speech.getEventsToday(map);
		verify(this.logic).getEventsAt(LocalDateTime.of(2018, 9, 20, 10, 00));	
	}
	
	/**
	 * test getEventsToday() with tomorrow
	 */
	@Test
	void testGetEventsTomorrow() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.day.getString()).thenReturn("tomorrow");
		map.put("day", this.day);
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 10, 00));
		this.speech.getEventsToday(map);
		verify(this.logic).getEventsAt(LocalDateTime.of(2018, 9, 20, 10, 00).plusDays(1));
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
						"write tests in Java on the 28th of may at 12:59 pm until 20:30 pm."),
				Pair.of(event("event start in past", "2015-05-12T13:05:00", "2015-05-29T15:30:00"),
						"event start in past since the 12th of may at 13:05 pm until the 29th of may at 15:30 pm."),
				Pair.of(event("event same day start past", "2015-05-28T04:35:02", "2015-05-28T10:15:00"),
						"event same day start past on the 28th of may at 04:35 am until 10:15 am."),
				Pair.of(event("event end in future", "2015-05-28T07:30:00", "2015-05-29T12:00:00"),
						"event end in future since the 28th of may at 07:30 am until the 29th of may at 12:00 pm."),
				Pair.of(event("event end today", "2015-05-27T22:00:00", "2015-05-28T10:00:00"),
						"event end today since the 27th of may at 22:00 pm until the 28th of may at 10:00 am."),
				Pair.of(event("event start today end tomorrow", "2015-05-28T15:30:00", "2015-05-29T15:30:00"),
						"event start today end tomorrow from the 28th of may at 15:30 pm until the 29th of may at 15:30 pm."),
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
						"event later that day from 14:00 pm until 16:30 pm."),
				Pair.of(eventAllDay("event all day", "2015-05-28", "2015-05-28"), "event all day all day long."),
				Pair.of(event("event finish today", "2015-05-27T23:00:00", "2015-05-28T10:00:00"),
						"event finish today since the 27th of may at 23:00 pm until 10:00 am."));
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
	
    /**
     * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarSpeech#isAllDay(java.lang.String)}.
     */
    @Test
    void testIsAllDay() {
        assertThat(CalendarSpeech.isAllDay("yes"), is(true));
        assertThat(CalendarSpeech.isAllDay("true"), is(true));
        assertThat(CalendarSpeech.isAllDay("no"), is(false));
        assertThat(CalendarSpeech.isAllDay("false"), is(false));
    }


}
