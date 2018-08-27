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
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;

/**
 * Class to test the Timer class
 * 
 * @author Patrick Singer
 */
@ExtendWith(FrameworkExtension.class)
public class TimerTest {

	/**
	 * Tests toString
	 */
	@Test
	public void toStringTest() {
		Calendar c = Calendar.getInstance();
		assertEquals("1:" + c.getTimeInMillis() + ":true", new Timer(1, c, true).toString());
	}

	/**
	 * Tests reconstructObject
	 */
	@Test
	public void reconstructObjectTest() {
		assertThrows(IllegalArgumentException.class, () -> Timer.reconstructObject("foo"));

		Timer t1 = Timer.reconstructObject("1:1000000:false");
		assertEquals(1, t1.getId());
		assertEquals(1000000, t1.getTimerDate().getTimeInMillis());
		assertEquals(false, t1.isActive());
	}

	/**
	 * Tests delayValid
	 */
	@Test
	public void delayValidTest() {
		assertFalse(Timer.delayValid(0, 0, 0));
		assertFalse(Timer.delayValid(-1, 0, 0));
		assertFalse(Timer.delayValid(0, -1, 0));
		assertFalse(Timer.delayValid(0, 0, -1));
		assertFalse(Timer.delayValid(-1, 5, 5));
	}

}
