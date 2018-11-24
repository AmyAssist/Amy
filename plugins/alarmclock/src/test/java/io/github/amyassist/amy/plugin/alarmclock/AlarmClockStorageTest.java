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

package io.github.amyassist.amy.plugin.alarmclock;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.plugin.api.IStorage;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test class for the alarm clock storage class
 * 
 * @author Patrick Singer, Patrick Gebhardt
 *
 */
@ExtendWith(FrameworkExtension.class)
public class AlarmClockStorageTest {

	private AlarmRegistry alarmStorage;

	@Reference
	private TestFramework framework;

	private IStorage storage;

	private static final String TIMERCOUNTER = "timerCounter";

	/**
	 * Initializes the class variables before every test
	 */
	@BeforeEach
	void setup() {
		this.storage = this.framework.storage();
		this.alarmStorage = this.framework.mockService(AlarmRegistry.class);

		reset(this.storage);
		reset(this.alarmStorage);
	}

	/**
	 * Tests the store alarm method
	 */
	@Test
	void testStoreAlarm() {
		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Alarm alarm = new Alarm(2, alarmTime, true);

		this.alarmStorage.save(alarm);
		verify(this.alarmStorage).save(alarm);
	}

	/**
	 * Tests deleteAlarm with normal case
	 */
	@Test
	public void testDeleteAlarm() {

		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Alarm alarm = new Alarm(1, alarmTime, true);
		this.alarmStorage.save(alarm);
		this.alarmStorage.deleteById(alarm.getPersistentId());
		verify(this.alarmStorage).deleteById(alarm.getPersistentId());
		assertThat(true, is(this.alarmStorage.getAll().isEmpty()));
	}

	/**
	 * Tests deleteAlarm with non existent alarm
	 */
	@Test
	public void testDeleteAlarmNotFound() {
		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Alarm alarm = new Alarm(1, alarmTime, true);
		this.alarmStorage.save(alarm);
		this.alarmStorage.deleteById(alarm.getPersistentId() + 1);
		assertThat(true, is(this.alarmStorage.getAll().isEmpty()));
	}

	/**
	 * Tests the getAlarm method
	 */
	@Test
	void testGetAlarm() {
		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Alarm alarm = new Alarm(1, alarmTime, true);
		this.alarmStorage.save(alarm);

		assertThat(alarm.toString(), is("1:2018-08-15T11:11:true"));
	}
}
