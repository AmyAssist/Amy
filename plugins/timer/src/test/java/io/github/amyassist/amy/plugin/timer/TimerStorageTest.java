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

package io.github.amyassist.amy.plugin.timer;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test class for the timer storage class
 * 
 * @author Patrick Gebhardt
 *
 */
@ExtendWith(FrameworkExtension.class)
public class TimerStorageTest {

	private TimerRegistry timerStorage;

	private TimerRegistryImpl timerRegImpl;

	@Reference
	private TestFramework framework;

	/**
	 * Initializes the class variables before every test
	 */
	@BeforeEach
	void setup() {
		this.timerStorage = this.framework.mockService(TimerRegistry.class);
		this.timerRegImpl = this.framework.mockService(TimerRegistryImpl.class);
		reset(this.timerStorage);
	}

	/**
	 * Tests the store timer method
	 */
	@Test
	void testStoreTimer() {
		LocalDateTime timerTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Timer timer = new Timer(1, timerTime, null, true);

		this.timerStorage.save(timer);
		verify(this.timerStorage).save(timer);
	}

	/**
	 * Tests deleteTimer with normal case
	 */
	@Test
	public void testDeleteTimer() {
		LocalDateTime timerTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Timer timer = new Timer(1, timerTime, null, true);
		this.timerStorage.save(timer);
		this.timerStorage.deleteById(timer.getPersistentId());
		verify(this.timerStorage).deleteById(timer.getPersistentId());
		assertThat(true, is(this.timerStorage.getAll().isEmpty()));
	}

	/**
	 * Tests deleteTimer with non existent timer
	 */
	@Test
	public void testDeleteTimerNotFound() {
		LocalDateTime timerTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Timer timer = new Timer(1, timerTime, null, true);
		this.timerStorage.save(timer);
		this.timerStorage.deleteById(timer.getPersistentId() + 1);
		assertThat(true, is(this.timerStorage.getAll().isEmpty()));
	}

	/**
	 * tests the getPersistenceUnitName method
	 */
	@Test
	public void getPersistenceUnitNameTest() {
		when(this.timerRegImpl.getPersistenceUnitName()).thenReturn("TimerRegistry");
		assertThat(this.timerRegImpl.getPersistenceUnitName(), is("TimerRegistry"));
	}

}
