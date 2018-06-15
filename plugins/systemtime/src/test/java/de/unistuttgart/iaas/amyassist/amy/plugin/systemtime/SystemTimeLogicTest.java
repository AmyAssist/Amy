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

package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * TODO: Description
 * 
 * @author Leon Kiefer, Florian Bauer
 */
class SystemTimeLogicTest {
	private SystemTimeLogic systemTimeLogic;
	private Calendar calendar;

	@BeforeEach
	public void setup() {
		this.systemTimeLogic = Mockito.mock(SystemTimeLogic.class, Mockito.CALLS_REAL_METHODS);

		this.calendar = Calendar.getInstance();
	}

	@Test
	void test() {
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getTimeStamp(), equalTo(time));
	}

	@Test
	void test2() {
		this.calendar.set(Calendar.DAY_OF_MONTH, 2);
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getDay(), equalTo("02"));
	}

	@Test
	void test3() {
		this.calendar.set(Calendar.MONTH, 3);
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getMonth(), equalTo("04"));
	}

	@Test
	void test4() {
		this.calendar.set(Calendar.YEAR, 1574);
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getYear(), equalTo("1574"));

	}

	@Test
	void test5() {
		this.calendar.set(1492, 4, 5);
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getDate(), equalTo("05 05 92"));
	}

	@Test
	void test6() {
		this.calendar.set(Calendar.HOUR_OF_DAY, 6);
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getHour(), equalTo("06"));
	}

	@Test
	void test7() {
		this.calendar.set(Calendar.MINUTE, 7);
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getMinute(), equalTo("07"));
	}

	@Test
	void test8() {
		this.calendar.set(Calendar.SECOND, 8);
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getSecond(), equalTo("08"));
	}

	@Test
	void test9() {
		this.calendar.set(0, 0, 0, 9, 30, 0);
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getTime(), equalTo("09 30 00"));
	}

}
