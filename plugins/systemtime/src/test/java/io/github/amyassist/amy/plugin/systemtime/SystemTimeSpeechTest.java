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

package io.github.amyassist.amy.plugin.systemtime;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
 * test class for system Time speech
 *
 * @author Lars Buttgereit, Florian Bauer
 */
@ExtendWith(FrameworkExtension.class)
public class SystemTimeSpeechTest {

	@Reference
	private TestFramework testFramework;

	private Environment environment;
	private SystemTimeSpeech speech;

	private LocalDateTime ldt;

	/**
	 * initialize the test
	 */
	@BeforeEach
	void init() {
		this.environment = this.testFramework.mockService(Environment.class);
		this.speech = this.testFramework.setServiceUnderTest(SystemTimeSpeech.class);
		this.ldt = LocalDateTime.of(2018, 8, 20, 20, 15, 28);
	}

	/**
	 * Test time(Map<String, EntityData>)
	 */
	@Test
	void testTime() {
		when(this.environment.getCurrentLocalDateTime()).thenReturn(this.ldt);
		assertThat(this.speech.time(new HashMap<>()), equalToIgnoringWhiteSpace("It is 20:15."));
	}

	/**
	 * Test date(Map<String, EntityData>)
	 */
	@Test
	void testDate() {
		when(this.environment.getCurrentLocalDateTime()).thenReturn(this.ldt);
		assertThat(this.speech.date(new HashMap<>()), equalToIgnoringWhiteSpace("It is the 20th of august."));
	}

	/**
	 * Test year(Map<String, EntityData>)
	 */
	@Test
	void testYear() {
		when(this.environment.getCurrentLocalDateTime()).thenReturn(this.ldt);
		assertThat(this.speech.year(new HashMap<>()), equalToIgnoringWhiteSpace("It is 2018."));
	}

	/**
	 * @param testCase
	 *            a combination of the input variables and the expected outcome
	 */
	@ParameterizedTest
	@MethodSource("dayOfWeekPairs")
	public void testDayOfWeek(Pair<Map<String, EntityData>, String> testCase) {
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 20, 00, 00));
		String setEvent = this.speech.dayOfWeek(testCase.getLeft());
		assertThat(setEvent, equalToIgnoringWhiteSpace(testCase.getRight()));
	}

	/**
	 *
	 * @return the test cases used in the {@link #testDayOfWeek(Pair)} test
	 */
	public static Stream<Pair<Map<String, EntityData>, String>> dayOfWeekPairs() {
		return Stream.of(Pair.of(speechMap("", LocalDate.of(2018, 9, 24), "today", ""), "Today is monday."),
				Pair.of(speechMap("", LocalDate.of(2018, 9, 25), "tomorrow", "2018"), "Tomorrow is tuesday."),
				Pair.of(speechMap("", LocalDate.of(2019, 1, 2), "", "2019"), "The 2nd of january 2019 is a wednesday."),
				Pair.of(speechMap("", LocalDate.of(2018, 4, 11), "", ""), "The 11th of april 2019 is a thursday."),
				Pair.of(speechMap("", LocalDate.of(2018, 4, 13), "", "2018"), "The 13th of april was a friday."),
				Pair.of(speechMap("", LocalDate.of(2018, 8, 18), "", "2018"), "The 18th of august was a saturday."),
				Pair.of(speechMap("", LocalDate.of(2017, 12, 24), "", "2017"), "The 24th of december 2017 was a sunday."));
	}

	private static Map<String, EntityData>  speechMap(String timeString, LocalDate localDate, String dateString, String yearString) {
		EntityData date = Mockito.mock(EntityData.class);
		EntityData year = Mockito.mock(EntityData.class);
		EntityData time = Mockito.mock(EntityData.class);
		Map<String, EntityData> map = new HashMap<>();
		when(time.getString()).thenReturn(timeString);
		map.put("time", time);
		when(date.getDate()).thenReturn(localDate);
		when(date.getString()).thenReturn(dateString);
		map.put("date", date);
		if (yearString.isEmpty()) {
			map.put("year", null);
		} else {
			map.put("year", year);
		}
		return map;
	}
	
	/**
	 * @param testCase
	 *            a combination of the input variables and the expected outcome
	 */
	@ParameterizedTest
	@MethodSource("howManyDaysPairs")
	public void testHowManyDays(Pair<Map<String, EntityData>, String> testCase) {
		when(this.environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(2018, 9, 24, 00, 00));
		String setEvent = this.speech.howManyDays(testCase.getLeft());
		assertThat(setEvent, equalToIgnoringWhiteSpace(testCase.getRight()));
	}

	/**
	 *
	 * @return the test cases used in the {@link #testHowManyDays(Pair)} test
	 */
	public static Stream<Pair<Map<String, EntityData>, String>> howManyDaysPairs() {
		return Stream.of(Pair.of(speechMap("until", LocalDate.of(2018, 9, 24), "today", ""), "0 days until the 24th of september 2018."),
				Pair.of(speechMap("until", LocalDate.of(2018, 9, 25), "tomorrow", "2018"), "1 day until the 25th of september 2018."),
				Pair.of(speechMap("until", LocalDate.of(2018, 9, 19), "", ""), "360 days until the 19th of september 2019."),
				Pair.of(speechMap("since", LocalDate.of(2018, 9, 10), "", "2018"), "14 days have passed since the 10th of september 2018."));
	}
}
