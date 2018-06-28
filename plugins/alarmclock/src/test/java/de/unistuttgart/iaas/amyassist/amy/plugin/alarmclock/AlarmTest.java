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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;

import org.junit.jupiter.api.Test;

/**
 * Class to test the alarm class
 * 
 * @author Patrick Singer
 */
public class AlarmTest {

	/**
	 * Tests toString
	 */
	@Test
	public void toStringTest() {
		assertEquals("1:4:20:true", new Alarm(1, 4, 20, true).toString());
	}

	/**
	 * Tests reconstructObject
	 */
	@Test
	public void reconstructObjectTest() {
		assertThrows(IllegalArgumentException.class, () -> Alarm.reconstructObject("foo"));

		Alarm a1 = Alarm.reconstructObject("10:4:20:false");
		assertEquals(10, a1.getId());
		assertEquals(4, a1.getAlarmDate().get(Calendar.HOUR_OF_DAY));
		assertEquals(20, a1.getAlarmDate().get(Calendar.MINUTE));
		assertEquals(0, a1.getAlarmDate().get(Calendar.SECOND));
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
