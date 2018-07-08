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

package de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;

/**
 * Test case for timestamp
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
class TimestampTest {
	
	/**
	 * timestamp object
	 */
	private Timestamp timestamp;

	/**
	 * init for constructor
	 */
	@Test @BeforeEach
	void init() {
		this.timestamp = new Timestamp(2020, 1, 2, 20, 20, 20); // 2020.01.02 20:20:20
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#getYear()}.
	 */
	@Test
	void testGetYear() {
		assertEquals(this.timestamp.getYear(), 2020);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#setYear(int)}.
	 */
	@Test
	void testSetYear() {
		this.timestamp.setYear(2021);
		assertEquals(this.timestamp.getYear(), 2021);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#getMonth()}.
	 */
	@Test
	void testGetMonth() {
		assertEquals(this.timestamp.getMonth(), 1);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#setMonth(int)}.
	 */
	@Test
	void testSetMonth() {
		this.timestamp.setMonth(2);
		assertEquals(this.timestamp.getMonth(), 2);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#getDay()}.
	 */
	@Test
	void testGetDay() {
		assertEquals(this.timestamp.getDay(), 2);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#setDay(int)}.
	 */
	@Test
	void testSetDay() {
		this.timestamp.setDay(3);
		assertEquals(this.timestamp.getDay(), 3);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#getHour()}.
	 */
	@Test
	void testGetHour() {
		assertEquals(this.timestamp.getHour(), 20);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#setHour(int)}.
	 */
	@Test
	void testSetHour() {
		this.timestamp.setHour(40);
		assertEquals(this.timestamp.getHour(), 40);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#getMinute()}.
	 */
	@Test
	void testGetMinute() {
		assertEquals(this.timestamp.getMinute(), 20);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#setMinute(int)}.
	 */
	@Test
	void testSetMinute() {
		this.timestamp.setMinute(40);
		assertEquals(this.timestamp.getMinute(), 40);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#getSecond()}.
	 */
	@Test
	void testGetSecond() {
		assertEquals(this.timestamp.getSecond(), 20);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#setSecond(int)}.
	 */
	@Test
	void testSetSecond() {
		this.timestamp.setSecond(40);
		assertEquals(this.timestamp.getSecond(), 40);
	}
	
	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp#equals(Object)}.
	 */
	@Test
	void testEquals() {
		Timestamp ts = new Timestamp();
		ts.setYear(2020);
		ts.setMonth(1);
		ts.setDay(2);
		ts.setHour(20);
		ts.setMinute(20);
		ts.setSecond(20);
		assertTrue(ts.equals(new Timestamp(2020, 1, 2, 20, 20, 20)));
		assertFalse(ts.equals(new Timestamp()));
	}

}
