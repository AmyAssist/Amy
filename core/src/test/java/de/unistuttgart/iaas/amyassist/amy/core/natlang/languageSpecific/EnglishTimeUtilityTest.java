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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.languageSpecific;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.en.EnglishTimeUtility;

/**
 * 
 * test class for the English Time Utility
 * @author
 */
class EnglishTimeUtilityTest {
	
	private EnglishTimeUtility timeUtility = new EnglishTimeUtility();
	
	@BeforeEach
	void init() {
		
	}

	@Test
	void testFromatTimeOClock() {
		assertThat(this.timeUtility.parseTime("9 o clock am"), equalTo(LocalTime.MIN.plusHours(9)));
		assertThat(this.timeUtility.parseTime("23 o clock"), equalTo(LocalTime.MIN.plusHours(23)));
		assertThat(this.timeUtility.parseTime("9 o clock pm"), equalTo(LocalTime.MIN.plusHours(21)));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("25 o clock"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("-1 o clock"));
	}

	@Test
	void testFromatTimeQuarterPast() {
		assertThat(this.timeUtility.parseTime("quarter past 2 am"), equalTo(LocalTime.MIN.plusHours(2).plusMinutes(15)));
		assertThat(this.timeUtility.parseTime("quarter past 2 pm"), equalTo(LocalTime.MIN.plusHours(14).plusMinutes(15)));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("quarter past 22"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("quarter past 25"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("quarter past -1"));
	}

	@Test
	void testFromatTimeQuarterTo() {
		assertThat(this.timeUtility.parseTime("quarter to 2 am"), equalTo(LocalTime.MIN.plusHours(1).plusMinutes(45)));
		assertThat(this.timeUtility.parseTime("quarter to 2 pm"), equalTo(LocalTime.MIN.plusHours(13).plusMinutes(45)));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("quarter to 22"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("quarter to 25"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("quarter to -1"));
	}

	@Test
	void testFromatTimeTo() {
		assertThat(this.timeUtility.parseTime("10 to 2 am"), equalTo(LocalTime.MIN.plusHours(1).plusMinutes(50)));
		assertThat(this.timeUtility.parseTime("10 to 2 pm"), equalTo(LocalTime.MIN.plusHours(13).plusMinutes(50)));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("21 to 21"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("1 to 25"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("10 to -1"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("10 to -1"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("21 to 25"));
	}

	@Test
	void testFromatTimePast() {
		assertThat(this.timeUtility.parseTime("10 past 2 am"), equalTo(LocalTime.MIN.plusHours(2).plusMinutes(10)));
		assertThat(this.timeUtility.parseTime("10 past 2 pm"), equalTo(LocalTime.MIN.plusHours(14).plusMinutes(10)));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("21 past 22"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("-1 past 25"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("61 past 20"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("10 past -1"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("21 past 25"));
	}

	@Test
	void testFromatTimeGoogle() {
		assertThat(this.timeUtility.parseTime("10 x 2 am"), equalTo(LocalTime.MIN.plusHours(10).plusMinutes(02)));
		assertThat(this.timeUtility.parseTime("10 x 2 pm"), equalTo(LocalTime.MIN.plusHours(22).plusMinutes(02)));
		assertThat(this.timeUtility.parseTime("10 x 10 pm"), equalTo(LocalTime.MIN.plusHours(22).plusMinutes(10)));
		assertThat(this.timeUtility.parseTime("9 x 22"), equalTo(LocalTime.MIN.plusHours(9).plusMinutes(22)));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("-1 x 25"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("-1 x 25"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("61 x 20"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("13 x 00 pm"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("13 x 00 am"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseTime("21 x 25 pm"));
		assertThat(this.timeUtility.parseTime("12 x 00 am"), equalTo(LocalTime.MIN.plusHours(0).plusMinutes(0)));
		assertThat(this.timeUtility.parseTime("12 x 00 pm"), equalTo(LocalTime.MIN.plusHours(12).plusMinutes(0)));
		assertThat(this.timeUtility.parseTime("20 x 22"), equalTo(LocalTime.MIN.plusHours(20).plusMinutes(22)));
	}
	
	@Test
	void testTimeFormatter() {
		assertThat(this.timeUtility.formatTime("10 a.m."), equalTo("10 am"));
		assertThat(this.timeUtility.formatTime("10 p.m."), equalTo("10 pm"));
		assertThat(this.timeUtility.formatTime("1:02 a.m."), equalTo("1 x 02 am"));
		assertThat(this.timeUtility.formatTime("10:10 a.m."), equalTo("10 x 10 am"));
		assertThat(this.timeUtility.formatTime("10:10 a.m. and 12:00 p.m."), equalTo("10 x 10 am and 12 x 00 pm"));
	}
	
	@Test
	void testformatDateWithPoints() {
		assertThat(this.timeUtility.formatDate(". today is 10.10.2018 hello"), equalTo(". today is 10 10 2018 hello"));
		assertThat(this.timeUtility.formatDate(". today is 1.1.2018 hello"), equalTo(". today is 1 1 2018 hello"));
		assertThat(this.timeUtility.formatDate(". today is 1.1.2018 and 1st july"), equalTo(". today is 1 1 2018 and 1 july"));
		assertThat(this.timeUtility.formatDate(". today is 1.1.2018 and 1st july and 4th july"), equalTo(". today is 1 1 2018 and 1 july and 4 july"));
		assertThat(this.timeUtility.formatDate("first 2nd july and 4th july"), equalTo("first 2 july and 4 july"));	
		assertThat(this.timeUtility.formatDate(". today is 10.10.2018 to 12.10.2018 hello"), equalTo(". today is 10 10 2018 to 12 10 2018 hello"));
	}
	
	@Test
	void testParseDate() {
		assertThat(this.timeUtility.parseDate("monday the 1 july of 2018"), equalTo(LocalDate.of(2018, 7, 1)));
		assertThat(this.timeUtility.parseDate("monday the 1 july"), equalTo(LocalDate.of(LocalDate.now().getYear(), 7, 1)));
		assertThat(this.timeUtility.parseDate("1 july"), equalTo(LocalDate.of(LocalDate.now().getYear(), 7, 1)));
		assertThat(this.timeUtility.parseDate("10 10 2018"), equalTo(LocalDate.of(2018, 10, 10)));
		assertThat(this.timeUtility.parseDate("1 1 2018"), equalTo(LocalDate.of(2018, 1, 1)));
		assertThat(this.timeUtility.parseDate("10 10 2018"), equalTo(LocalDate.of(2018, 10, 10)));
		assertThat(this.timeUtility.parseDate("10 10"), equalTo(LocalDate.of(LocalDate.now().getYear(), 10, 10)));
		assertThat(this.timeUtility.parseDate("monday 10 10 2018"), equalTo(LocalDate.of(2018, 10, 10)));	
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseDate("bla"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseDate("32 10 2018"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseDate("monday the 40 july of 2018"));
	}
	
	@Test
	void testDateTime() {
		assertThat(this.timeUtility.parseDateTime("monday the 1 july of 2018 at 10 x 00 pm"), equalTo(LocalDateTime.of(2018, 7, 1, 22, 00)));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseDateTime("monday 10 10 2018"));
		assertThrows(DateTimeParseException.class, () -> this.timeUtility.parseDateTime("10 x 2 am"));
	}
	
}
