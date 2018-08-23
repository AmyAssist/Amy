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

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;

/**
 * Class to test the alarm class
 * 
 * @author Patrick Singer, Leon Kiefer, Patrick Gebhardt
 */
@ExtendWith(FrameworkExtension.class)
public class AlarmTest {

	/**
	 * Tests alarm constructor
	 */
	@Test
	public void alarmTest() {
		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Alarm a1 = new Alarm(5, alarmTime, false);
		assertEquals(5, a1.getId());

		LocalDateTime of = LocalDateTime.of(2018, 8, 15, 11, 11);

		assertThat(a1.getAlarmTime(), is(equalTo(of)));
		assertThat(a1.isActive(), is(false));
	}

	@Test
	public void testBadAlarmInput() {
		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		assertThrows(IllegalArgumentException.class, () -> new Alarm(-1, alarmTime, true));
	}

	/**
	 * Tests toString
	 */
	@Test
	public void toStringTest() {
		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		assertEquals("1:2018-08-15T11:11:true", new Alarm(1, alarmTime, true).toString());
	}

	/**
	 * Tests reconstructObject
	 */
	@Test
	public void reconstructObjectTest() {
		assertThrows(IllegalArgumentException.class, () -> Alarm.reconstructObject("foo"));

		Alarm a1 = Alarm.reconstructObject("10:2018-08-15T10:41:false");
		assertEquals(10, a1.getId());
		assertEquals(2018, a1.getAlarmTime().getYear());
		assertEquals(8, a1.getAlarmTime().getMonthValue());
		assertEquals(15, a1.getAlarmTime().getDayOfMonth());
		assertEquals(10, a1.getAlarmTime().getHour());
		assertEquals(41, a1.getAlarmTime().getMinute());
		assertEquals(0, a1.getAlarmTime().getSecond());
		assertEquals(false, a1.isActive());
	}

	/**
	 * Tests timeValid
	 */
	@Test
	public void timeValidTest() {
		assertFalse(Alarm.timeValid(-1, 0));
		assertFalse(Alarm.timeValid(0, -1));
		assertFalse(Alarm.timeValid(-1, -1));

	}
}
