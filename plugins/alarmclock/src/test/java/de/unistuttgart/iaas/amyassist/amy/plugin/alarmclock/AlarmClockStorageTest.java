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
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

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

	private TimerStorage acs;

	private IStorage storage;

	private static final String TIMERCOUNTER = "timerCounter";

	/**
	 * Initializes the class variables before every test
	 */
	@BeforeEach
	void setup() {
		this.storage = this.framework.storage();
		this.acs = this.framework.setServiceUnderTest(TimerStorage.class);
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
	 * Tests the store timer method
	 */
	@Test
	void testStoreTimer() {
		Timer timer = new Timer(3, 1, 0, 1, false);

		this.acs.storeTimer(timer);
		verify(this.storage).put("timer3", timer.toString());
	}

	/**
	 * Tests the getTimerCounter method
	 */
	@Test
	void testGetTimerCounter() {
		this.storage.put(TIMERCOUNTER, "20");
		reset(this.storage);

		assertThat(this.acs.getTimerCounter(), is(20));
		verify(this.storage, only()).get(TIMERCOUNTER);
	}

	/**
	 * Tests the putAlarmCounter
	 */
	@Test
	void testPutTimerCounter() {
		this.storage.put(TIMERCOUNTER, "10");
		reset(this.storage);

		this.acs.putTimerCounter(20);
		verify(this.storage, only()).put(TIMERCOUNTER, "20");
		assertThat(this.storage.get(TIMERCOUNTER), is("20"));
	}

	/**
	 * Tests the incrementTimerCounter method
	 */
	@Test
	void testIncrementTimerCounter() {
		this.storage.put(TIMERCOUNTER, "9");
		reset(this.storage);

		assertThat(10, is(this.acs.incrementTimerCounter()));
		verify(this.storage).get(TIMERCOUNTER);
		verify(this.storage).put(TIMERCOUNTER, "10");
		assertThat(this.storage.get(TIMERCOUNTER), is("10"));
	}

	/**
	 * Tests hasTimer
	 */
	@Test
	public void testHasTimer() {
		this.storage.put("timer1", "foo");
		assertThat(true, is(this.acs.hasTimer(1)));
		assertThat(false, is(this.acs.hasTimer(2)));
		verify(this.storage, times(2)).has(ArgumentMatchers.anyString());
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
	 * Tests deleteTimer with normal case
	 */
	@Test
	public void testDeleteTimer() {
		this.storage.put("timer1", "foo");
		this.acs.deleteTimer(1);
		verify(this.storage).has("timer1");
		verify(this.storage).delete("timer1");
		assertThat(false, is(this.storage.has("timer1")));
	}

	/**
	 * Tests deleteTimer with non existent timer
	 */
	@Test
	public void testDeleteTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acs.deleteTimer(1));
		verify(this.storage, only()).has("timer1");
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

	/**
	 * Tests the getTimer
	 */
	@Test
	void testGetTimer() {
		// wrong id
		assertThrows(NoSuchElementException.class, () -> this.acs.getTimer(1));

		// usual case
		this.storage.put("timer1", "1:20:20:20:true");
	}
}
