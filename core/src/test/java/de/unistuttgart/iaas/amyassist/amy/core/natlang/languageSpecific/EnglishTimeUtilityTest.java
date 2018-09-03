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

import java.time.LocalTime;

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
		assertThat(this.timeUtility.parseTime("25 o clock"), equalTo(null));
		assertThat(this.timeUtility.parseTime("-1 o clock"), equalTo(null));
	}

	@Test
	void testFromatTimeQuarterPast() {
		assertThat(this.timeUtility.parseTime("quarter past 2 am"), equalTo(LocalTime.MIN.plusHours(2).plusMinutes(15)));
		assertThat(this.timeUtility.parseTime("quarter past 2 pm"), equalTo(LocalTime.MIN.plusHours(14).plusMinutes(15)));
		assertThat(this.timeUtility.parseTime("quarter past 22"), equalTo(null));
		assertThat(this.timeUtility.parseTime("quarter past 25"), equalTo(null));
		assertThat(this.timeUtility.parseTime("quarter past -1"), equalTo(null));
	}

	@Test
	void testFromatTimeQuarterTo() {
		assertThat(this.timeUtility.parseTime("quarter to 2 am"), equalTo(LocalTime.MIN.plusHours(1).plusMinutes(45)));
		assertThat(this.timeUtility.parseTime("quarter to 2 pm"), equalTo(LocalTime.MIN.plusHours(13).plusMinutes(45)));
		assertThat(this.timeUtility.parseTime("quarter to 22"), equalTo(null));
		assertThat(this.timeUtility.parseTime("quarter to 25"), equalTo(null));
		assertThat(this.timeUtility.parseTime("quarter to -1"), equalTo(null));
	}

	@Test
	void testFromatTimeTo() {
		assertThat(this.timeUtility.parseTime("10 to 2 am"), equalTo(LocalTime.MIN.plusHours(1).plusMinutes(50)));
		assertThat(this.timeUtility.parseTime("10 to 2 pm"), equalTo(LocalTime.MIN.plusHours(13).plusMinutes(50)));
		assertThat(this.timeUtility.parseTime("21 to 21"), equalTo(null));
		assertThat(this.timeUtility.parseTime("1 to 25"), equalTo(null));
		assertThat(this.timeUtility.parseTime("10 to -1"), equalTo(null));
		assertThat(this.timeUtility.parseTime("10 to -1"), equalTo(null));
		assertThat(this.timeUtility.parseTime("21 to 25"), equalTo(null));
	}

	@Test
	void testFromatTimePast() {
		assertThat(this.timeUtility.parseTime("10 past 2 am"), equalTo(LocalTime.MIN.plusHours(2).plusMinutes(10)));
		assertThat(this.timeUtility.parseTime("10 past 2 pm"), equalTo(LocalTime.MIN.plusHours(14).plusMinutes(10)));
		assertThat(this.timeUtility.parseTime("21 past 22"), equalTo(null));
		assertThat(this.timeUtility.parseTime("-1 past 25"), equalTo(null));
		assertThat(this.timeUtility.parseTime("61 past 20"), equalTo(null));
		assertThat(this.timeUtility.parseTime("10 past -1"), equalTo(null));
		assertThat(this.timeUtility.parseTime("21 past 25"), equalTo(null));
	}

	@Test
	void testFromatTimeGoogle() {
		assertThat(this.timeUtility.parseTime("10 x 2 am"), equalTo(LocalTime.MIN.plusHours(10).plusMinutes(02)));
		assertThat(this.timeUtility.parseTime("10 x 2 pm"), equalTo(LocalTime.MIN.plusHours(22).plusMinutes(02)));
		assertThat(this.timeUtility.parseTime("9 x 22"), equalTo(LocalTime.MIN.plusHours(9).plusMinutes(22)));
		assertThat(this.timeUtility.parseTime("-1 x 25"), equalTo(null));
		assertThat(this.timeUtility.parseTime("61 x 20"), equalTo(null));
		assertThat(this.timeUtility.parseTime("10 x -1"), equalTo(null));
		assertThat(this.timeUtility.parseTime("13 x 00 pm"), equalTo(null));
		assertThat(this.timeUtility.parseTime("13 x 00 am"), equalTo(null));
		assertThat(this.timeUtility.parseTime("21 x 25 pm"), equalTo(null));
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
	
}
