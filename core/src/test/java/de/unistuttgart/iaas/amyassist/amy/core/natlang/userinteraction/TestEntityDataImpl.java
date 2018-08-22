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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.userinteraction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;


/**
 * test class for the entityDataImpl
 * @author Lars Buttgereit
 *
 */
public class TestEntityDataImpl {
	
	@Test
	void testgetNumber() {
		EntityDataImpl input = new EntityDataImpl("10");
		assertThat(input.getNumber(), equalTo(10));
	}
	@Test
	void testgetInvalidNumber() {
		EntityDataImpl input = new EntityDataImpl("ABC");
		assertThat(input.getNumber(), equalTo(Integer.MIN_VALUE));
	}
	
	@Test
	void testgetString() {
		EntityDataImpl input = new EntityDataImpl("ABC");
		assertThat(input.getString(), equalTo("ABC"));
	}

	@Test
	void testFormatTimeNull() {
		assertThat(new EntityDataImpl(null).getString(), equalTo(null));
	}

	@Test
	void testFromatTimeOClock() {
		EntityDataImpl input1 = new EntityDataImpl("9 o clock am");
		EntityDataImpl input2 = new EntityDataImpl("23 o clock");
		EntityDataImpl input3 = new EntityDataImpl("9 o clock pm");
		EntityDataImpl input4 = new EntityDataImpl("25 o clock");
		EntityDataImpl input5 = new EntityDataImpl("-1 o clock");
		assertThat(input1.getTime(), equalTo(LocalTime.MIN.plusHours(9)));
		assertThat(input2.getTime(), equalTo(LocalTime.MIN.plusHours(23)));
		assertThat(input3.getTime(), equalTo(LocalTime.MIN.plusHours(21)));
		assertThat(input4.getTime(), equalTo(null));
		assertThat(input5.getTime(), equalTo(null));
	}

	@Test
	void testFromatTimeQuarterPast() {
		EntityDataImpl input1 = new EntityDataImpl("quarter past 2 am");
		EntityDataImpl input2 = new EntityDataImpl("quarter past 2 pm");
		EntityDataImpl input3 = new EntityDataImpl("quarter past 22");
		EntityDataImpl input4 = new EntityDataImpl("quarter past 25");
		EntityDataImpl input5 = new EntityDataImpl("quarter past -1");
		assertThat(input1.getTime(), equalTo(LocalTime.MIN.plusHours(2).plusMinutes(15)));
		assertThat(input2.getTime(), equalTo(LocalTime.MIN.plusHours(14).plusMinutes(15)));
		assertThat(input3.getTime(), equalTo(null));
		assertThat(input4.getTime(), equalTo(null));
		assertThat(input5.getTime(), equalTo(null));
	}

	@Test
	void testFromatTimeQuarterTo() {
		EntityDataImpl input1 = new EntityDataImpl("quarter to 2 am");
		EntityDataImpl input2 = new EntityDataImpl("quarter to 2 pm");
		EntityDataImpl input3 = new EntityDataImpl("quarter to 22");
		EntityDataImpl input4 = new EntityDataImpl("quarter to 25");
		EntityDataImpl input5 = new EntityDataImpl("quarter to -1");
		assertThat(input1.getTime(), equalTo(LocalTime.MIN.plusHours(1).plusMinutes(45)));
		assertThat(input2.getTime(), equalTo(LocalTime.MIN.plusHours(13).plusMinutes(45)));
		assertThat(input3.getTime(), equalTo(null));
		assertThat(input4.getTime(), equalTo(null));
		assertThat(input5.getTime(), equalTo(null));
	}

	@Test
	void testFromatTimeTo() {
		EntityDataImpl input1 = new EntityDataImpl("10 to 2 am");
		EntityDataImpl input2 = new EntityDataImpl("10 to 2 pm");
		EntityDataImpl input3 = new EntityDataImpl("21 to 21");
		EntityDataImpl input4 = new EntityDataImpl("1 to 25");
		EntityDataImpl input5 = new EntityDataImpl("10 to -1");
		EntityDataImpl input6 = new EntityDataImpl("10 to -1");
		EntityDataImpl input7 = new EntityDataImpl("21 to 25");
		assertThat(input1.getTime(), equalTo(LocalTime.MIN.plusHours(1).plusMinutes(50)));
		assertThat(input2.getTime(), equalTo(LocalTime.MIN.plusHours(13).plusMinutes(50)));
		assertThat(input3.getTime(), equalTo(null));
		assertThat(input4.getTime(), equalTo(null));
		assertThat(input5.getTime(), equalTo(null));
		assertThat(input6.getTime(), equalTo(null));
		assertThat(input7.getTime(), equalTo(null));
	}

	@Test
	void testFromatTimePast() {
		EntityDataImpl input1 = new EntityDataImpl("10 past 2 am");
		EntityDataImpl input2 = new EntityDataImpl("10 past 2 pm");
		EntityDataImpl input3 = new EntityDataImpl("21 past 22");
		EntityDataImpl input4 = new EntityDataImpl("-1 past 25");
		EntityDataImpl input5 = new EntityDataImpl("61 past 20");
		EntityDataImpl input6 = new EntityDataImpl("10 past -1");
		EntityDataImpl input7 = new EntityDataImpl("21 past 25");
		assertThat(input1.getTime(), equalTo(LocalTime.MIN.plusHours(2).plusMinutes(10)));
		assertThat(input2.getTime(), equalTo(LocalTime.MIN.plusHours(14).plusMinutes(10)));
		assertThat(input3.getTime(), equalTo(null));
		assertThat(input4.getTime(), equalTo(null));
		assertThat(input5.getTime(), equalTo(null));
		assertThat(input6.getTime(), equalTo(null));
		assertThat(input7.getTime(), equalTo(null));
	}

	@Test
	void testFromatTimeGoogle() {
		EntityDataImpl input1 = new EntityDataImpl("10 x 2 am");
		EntityDataImpl input2 = new EntityDataImpl("10 x 2 pm");
		EntityDataImpl input3 = new EntityDataImpl("9 x 22");
		EntityDataImpl input4 = new EntityDataImpl("-1 x 25");
		EntityDataImpl input5 = new EntityDataImpl("61 x 20");
		EntityDataImpl input6 = new EntityDataImpl("10 x -1");
		EntityDataImpl input8 = new EntityDataImpl("13 x 00 pm");
		EntityDataImpl input9 = new EntityDataImpl("13 x 00 am");
		EntityDataImpl input7 = new EntityDataImpl("21 x 25 pm");
		EntityDataImpl input10 = new EntityDataImpl("12 x 00 am");
		EntityDataImpl input11 = new EntityDataImpl("12 x 00 pm");
		EntityDataImpl input12 = new EntityDataImpl("20 x 22");
		assertThat(input1.getTime(), equalTo(LocalTime.MIN.plusHours(10).plusMinutes(02)));
		assertThat(input2.getTime(), equalTo(LocalTime.MIN.plusHours(22).plusMinutes(02)));
		assertThat(input3.getTime(), equalTo(LocalTime.MIN.plusHours(9).plusMinutes(22)));
		assertThat(input4.getTime(), equalTo(null));
		assertThat(input5.getTime(), equalTo(null));
		assertThat(input6.getTime(), equalTo(null));
		assertThat(input7.getTime(), equalTo(null));
		assertThat(input8.getTime(), equalTo(null));
		assertThat(input9.getTime(), equalTo(null));
		assertThat(input10.getTime(), equalTo(LocalTime.MIN.plusHours(0).plusMinutes(0)));
		assertThat(input11.getTime(), equalTo(LocalTime.MIN.plusHours(12).plusMinutes(0)));
		assertThat(input12.getTime(), equalTo(LocalTime.MIN.plusHours(20).plusMinutes(22)));
	}
}
