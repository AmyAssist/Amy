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

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.Alarm;

class TimestampTest {

	@Test
	void testConstructors() {
		Timestamp ts = new Timestamp();
		assertEquals(0, ts.getHour());
		assertEquals(0, ts.getMinute());
		assertEquals("", ts.getLink().toString());
		
		ts = new Timestamp(15, 20);
		assertEquals(15, ts.getHour());
		assertEquals(20, ts.getMinute());
		assertEquals("", ts.getLink().toString());
		
		ts = new Timestamp("16:55");
		assertEquals(16, ts.getHour());
		assertEquals(55, ts.getMinute());
		assertEquals("", ts.getLink().toString());
		
		Alarm alarm = new Alarm(2, 5, 17, true);
		ts = new Timestamp(alarm);
		ts.setLink(URI.create("/2"));
		assertEquals(5, ts.getHour());
		assertEquals(17, ts.getMinute());
		assertEquals("/2", ts.getLink().toString());
		
		try {
			alarm = null;
			ts = new Timestamp(alarm);
			fail("");
		} catch (IllegalArgumentException e) {
			
		}
		
		try {
			ts = new Timestamp("abc");
			fail("");
		} catch (IllegalArgumentException e) {
			assertEquals("abc", e.getMessage());
		}		
	}

	@Test
	void testToString() {
		Timestamp ts = new Timestamp(0, 15);
		assertEquals("00:15", ts.toString());
		
		ts = new Timestamp(15, 20);
		assertEquals("15:20", ts.toString());
		
		ts = new Timestamp(15, 2);
		assertEquals("15:02", ts.toString());
		ts = new Timestamp(6, 5);
		assertEquals("06:05", ts.toString());
	}

	@Test
	void testIsValid() {
		Timestamp ts = new Timestamp(15, 20);
		assertTrue(ts.isValid());
		
		ts = new Timestamp(15, 90);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(60, 30);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(60, 90);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(-1, 30);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(10, -90);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(-60, -90);
		assertFalse(ts.isValid());
	}
	
	@Test
	void testEquals() {
		Timestamp ts = new Timestamp();
		ts.setHour(15);
		ts.setMinute(15);
		assertTrue(ts.equals(new Timestamp(15, 15)));
		assertFalse(ts.equals("15:15"));
		assertFalse(ts.equals(new Timestamp()));
	}

}
