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
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
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

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.framework.mockService(CalendarService.class);
		this.callog = this.framework.setServiceUnderTest(CalendarLogic.class);
	}

	@ParameterizedTest
	@MethodSource("testEventsWithDate")
	public void testCheckDayWithDate(Pair<Event, String> testCase) {
		String checkDay = this.callog.checkDay(LocalDateTime.parse("2015-05-28T09:00:00"), testCase.getLeft(), true);
		assertThat(checkDay, equalToIgnoringWhiteSpace(testCase.getRight()));
	}

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
				Pair.of(event("test event", new DateTime("2015-05-29T08:00:00"), new DateTime("2015-05-29T09:30:00")),
						"test event from the 29th of may at 10:00 until the 29th of may at 11:30."),
				Pair.of(event("write tests in Java", new DateTime("2015-05-28T12:59:15"),
						new DateTime("2015-05-28T20:30:00")),
						"write tests in Java on the 28th of MAY at 14:59 until 22:30."));
	}
	/**
	 * 
	 * @return the test cases used in the {@link #testCheckDayWithoutDate(Pair)} test
	 */
	public static Stream<Pair<Event, String>> testEventsWithoutDate() {
		return Stream.of(
				Pair.of(event("test event", new DateTime("2015-05-29T08:00:00"), new DateTime("2015-05-29T09:30:00")),
						"test event from the 29th of may at 10:00 until 11:30."));
	}

	private static Event event(String summary, DateTime start, DateTime end) {
		Event event = new Event();
		event.setSummary(summary);
		event.setStart(new EventDateTime().setDateTime(start));
		event.setEnd(new EventDateTime().setDateTime(end));
		return event;
	}

	private static Event eventAllDay(String summary, DateTime start, DateTime end) {
		Event event = new Event();
		event.setSummary(summary);
		event.setStart(new EventDateTime().setDate(start));
		event.setEnd(new EventDateTime().setDate(end));
		return event;
	}

}
