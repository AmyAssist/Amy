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

package de.unistuttgart.iaas.amyassist.amy.plugin.timer;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for the TimerLogic class
 * 
 * @authorPatrick Gebhardt
 */
@ExtendWith(FrameworkExtension.class)
public class TimerLogicTest {

	@Reference
	private TestFramework framework;

	private TimerLogic tlogic;

	private TimerBeepService tbs;

	private TaskScheduler scheduler;

	private TimerRegistry timerStorage;

	private Environment env;

	private MessageHub messageHub;

	private List<Timer> timers = new ArrayList<>();

	private int timerNumber;

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.env = this.framework.mockService(Environment.class);
		this.scheduler = this.framework.mockService(TaskScheduler.class);
		this.setTbs(this.framework.mockService(TimerBeepService.class));
		this.timerStorage = this.framework.mockService(TimerRegistry.class);
		this.setMessageHub(this.framework.mockService(MessageHub.class));
		this.tlogic = this.framework.setServiceUnderTest(TimerLogic.class);

		when(this.timerStorage.getAll()).thenReturn(this.timers);
	}

	/**
	 * Tests set Timer with normal arguments
	 */
	@Test
	public void testSetTimer() {
		when(this.env.getCurrentDateTime()).thenReturn(ZonedDateTime.of(2018, 2, 1, 4, 21, 55, 987, ZoneId.of("Z")));
		LocalDateTime timerTime = LocalDateTime.of(2018, 2, 1, 4, 21, 55);
		Timer t = new Timer(1, timerTime, null, true);
		this.tlogic.setTimer(timerTime);
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Instant.class));
		assertThat(this.tlogic.setTimer(timerTime).toString(), is(t.toString()));
	}

	/**
	 * Tests resetTimers
	 */
	@Test
	public void testResetTimers() {
		List<Timer> returnedTimers = createTimers(3, true);
		this.timerNumber = 2;
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		assertThat(this.tlogic.deleteAllTimers(), is("3 timers deleted"));
	}

	/**
	 * Tests resetTimers with no timers
	 */
	@Test
	public void testResetTimersNoTimers() {
		assertThat(this.tlogic.deleteAllTimers(), is("No timers found"));
	}

	/**
	 * Tests deleteTimers
	 */
	@Test
	protected void testDeleteTimer() {
		List<Timer> returnedTimers = createTimers(3, true);
		this.timerNumber = 2;
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		assertThat(this.tlogic.deleteTimer(2), is("Timer " + this.timerNumber + " deleted"));
	}

	/**
	 * Tests deleteTimer with non existent timer
	 */
	@Test
	protected void testDeleteTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.tlogic.deleteTimer(4));
	}

	/**
	 * Tests getTimer with valid argument
	 */
	@Test
	protected void testGetTimer() {
		when(this.timerStorage.getAll()).thenReturn(createTimers(3, true));
		this.timerNumber = 2;
		Timer returnedTimer = this.tlogic.getTimer(this.timerNumber);
		assertThat(returnedTimer.getId(), is(2));
	}

	/**
	 * Tests getTimer with non existent timer
	 */
	@Test
	protected void testGetTimerNotFound() {
		when(this.timerStorage.getAll()).thenReturn(createTimers(3, true));
		this.timerNumber = 4;
		assertThrows(NoSuchElementException.class, () -> this.tlogic.getTimer(this.timerNumber));
	}

	/**
	 * Tests getAllTimers with some timers
	 */
	@Test
	protected void testGetAllTimers() {
		createTimers(3, true);
		List<Timer> returnedTimers = this.tlogic.getAllTimers();
		assertThat(returnedTimers, hasSize(3));
	}

	/**
	 * Tests getAllTimers with no timers
	 */
	@Test
	protected void testGetAllTimersNoTimers() {
		List<Timer> returnedTimers = this.tlogic.getAllTimers();
		assertThat(returnedTimers, hasSize(0));
	}

	/**
	 * Tests reactivateTimer with valid arguments
	 */
	@Test
	protected void testReactivateTimer() {
		List<Timer> returnedTimers = createTimers(3, true);
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		when(this.env.getCurrentDateTime()).thenReturn(ZonedDateTime.of(2018, 2, 1, 4, 21, 55, 987, ZoneId.of("Z")));
		Timer t2 = new Timer(3, LocalDateTime.of(2018, 9, 5, 12, 20, 0), null, true);
		this.tlogic.pauseTimer(returnedTimers.get(2));
		assertThat(this.tlogic.reactivateTimer(returnedTimers.get(2)).toString(), is(t2.toString()));
	}

	/**
	 * Tests pauseTimer with valid arguments
	 */
	@Test
	protected void testPauseTimer() {
		List<Timer> returnedTimers = createTimers(3, true);
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		when(this.env.getCurrentDateTime()).thenReturn(ZonedDateTime.of(2018, 2, 1, 4, 21, 55, 987, ZoneId.of("Z")));
		Timer t2 = new Timer(3, LocalDateTime.of(2018, 9, 5, 12, 20, 0), null, false);
		assertThat(this.tlogic.pauseTimer(returnedTimers.get(2)).toString(), is(t2.toString()));
	}

	/**
	 * Tests reactivateTimer with notvalid arguments
	 */
	@Test
	protected void testReactivateTimerNotValid() {
		List<Timer> returnedTimers = createTimers(3, true);
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		when(this.env.getCurrentDateTime()).thenReturn(ZonedDateTime.of(2018, 2, 1, 4, 21, 55, 987, ZoneId.of("Z")));
		assertThrows(IllegalArgumentException.class, () -> this.tlogic.reactivateTimer(returnedTimers.get(2)));
	}

	/**
	 * Tests pauseTimer with notvalid arguments
	 */
	@Test
	protected void testPauseTimerNotValid() {
		List<Timer> returnedTimers = createTimers(3, false);
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		when(this.env.getCurrentDateTime()).thenReturn(ZonedDateTime.of(2018, 2, 1, 4, 21, 55, 987, ZoneId.of("Z")));
		assertThrows(IllegalArgumentException.class, () -> this.tlogic.pauseTimer(returnedTimers.get(2)));
	}

	/**
	 * Tests SearchTimerId method
	 */
	@Test
	protected void testSearchTimerIdWithList() {
		List<Timer> returnedTimers = createTimers(3, false);
		returnedTimers.remove(1);
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		assertThat(this.tlogic.searchTimerId(), is(2));
	}

	/**
	 * Tests SearchTimerId method
	 */
	@Test
	protected void testSearchTimerIdWithoutList() {
		List<Timer> returnedTimers = new ArrayList<>();
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		assertThat(this.tlogic.searchTimerId(), is(1));
	}

	/**
	 * Tests the deleteAllTimers method
	 */
	@Test
	protected void testDeleteAllTimers() {
		List<Timer> returnedTimers = createTimers(3, false);
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		assertThat(this.tlogic.deleteAllTimers(), is("3 timers deleted"));
	}

	/**
	 * Tests the deleteAllTimers method
	 */
	@Test
	protected void testDeleteAllTimersNoTimers() {
		List<Timer> returnedTimers = createTimers(0, false);
		when(this.timerStorage.getAll()).thenReturn(returnedTimers);
		assertThat(this.tlogic.deleteAllTimers(), is("No timers found"));
	}

	private List<Timer> createTimers(int amount, boolean active) {
		for (int i = 1; i <= amount; i++) {
			Timer mockTimer = new Timer(i, LocalDateTime.of(2018, 9, 5, 12, 20, 0), null, active);
			this.timers.add(mockTimer);
		}
		return this.timers;
	}

	/**
	 * Get's {@link #tbs abs}
	 * 
	 * @return abs
	 */
	public TimerBeepService getTbs() {
		return this.tbs;
	}

	/**
	 * Set's {@link #tbs abs}
	 * 
	 * @param tbs
	 *            timerBeepService
	 */
	public void setTbs(TimerBeepService tbs) {
		this.tbs = tbs;
	}

	/**
	 * Get's {@link #messageHub messageHub}
	 * 
	 * @return messageHub
	 */
	@SuppressWarnings("unused")
	private MessageHub getMessageHub() {
		return this.messageHub;
	}

	/**
	 * Set's {@link #messageHub messageHub}
	 * 
	 * @param messageHub
	 *            messageHub
	 */
	private void setMessageHub(MessageHub messageHub) {
		this.messageHub = messageHub;
	}
}
