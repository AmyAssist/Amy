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

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import io.github.amyassist.amy.messagehub.MessageHub;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test class for the AlarmClockLogic class
 * 
 * @author Patrick Singer, Patrick Gebhardt
 */
@ExtendWith(FrameworkExtension.class)
public class AlarmClockLogicTest {

	@Reference
	private TestFramework framework;

	private AlarmClockLogic acl;

	private AlarmBeepService abs;

	private TaskScheduler scheduler;

	private AlarmRegistry alarmStorage;

	private Environment env;

	private MessageHub messageHub;

	private List<Alarm> alarms = new ArrayList<>();

	private int alarmNumber;

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.env = this.framework.mockService(Environment.class);
		this.scheduler = this.framework.mockService(TaskScheduler.class);
		this.setAbs(this.framework.mockService(AlarmBeepService.class));
		this.alarmStorage = this.framework.mockService(AlarmRegistry.class);
		this.messageHub = this.framework.mockService(MessageHub.class);
		this.acl = this.framework.setServiceUnderTest(AlarmClockLogic.class);

		when(this.alarmStorage.getAll()).thenReturn(this.alarms);
	}

	/**
	 * Tests set Alarm with normal arguments
	 */
	@Test
	public void testSetAlarm() {
		when(this.env.getCurrentDateTime()).thenReturn(ZonedDateTime.of(2018, 2, 1, 4, 21, 55, 987, ZoneId.of("Z")));
		this.acl.setAlarm(1, 4, 20);
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Instant.class));
	}

	/**
	 * Tests setAlarm with invalid arguments
	 */
	@Test
	public void testSetAlarmInvalid() {
		assertThrows(IllegalArgumentException.class, () -> this.acl.setAlarm(1, 12, 60));
		assertThrows(IllegalArgumentException.class, () -> this.acl.setAlarm(1, 24, 40));
	}

	/**
	 * Tests resetAlarms
	 */
	@Test
	public void testResetAlarms() {
		List<Alarm> returnedAlarms = createAlarms(3, false);
		this.alarmNumber = 2;
		when(this.alarmStorage.getAll()).thenReturn(returnedAlarms);
		assertThat(this.acl.resetAlarms(), is("3 alarms deleted"));
	}

	/**
	 * Tests resetAlarms with no alarms
	 */
	@Test
	public void testResetAlarmsNoAlarms() {
		assertThat(this.acl.resetAlarms(), is("No alarms found"));
	}

	/**
	 * Tests deleteAlarm
	 */
	@Test
	protected void testDeleteAlarm() {
		List<Alarm> returnedAlarms = createAlarms(3, false);
		this.alarmNumber = 2;
		when(this.alarmStorage.getAll()).thenReturn(returnedAlarms);
		assertThat(this.acl.deleteAlarm(2), is("Alarm " + this.alarmNumber + " deleted"));
	}

	/**
	 * Tests deleteAlarm with non existent alarm
	 */
	@Test
	protected void testDeleteAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deleteAlarm(4));
	}

	/**
	 * Tests deactivateAlarm with active alarm
	 */
	@Test
	protected void testDeactivateAlarmActive() {
		List<Alarm> returnedAlarms = createAlarms(3, true);
		this.alarmNumber = 2;
		when(this.alarmStorage.getAll()).thenReturn(returnedAlarms);
		assertThat(this.acl.deactivateAlarm(2), is("Alarm " + this.alarmNumber + " deactivated"));
	}

	/**
	 * Tests deactivateAlarm with inactive alarm
	 */
	@Test
	protected void testDeactivateAlarmInactive() {
		List<Alarm> returnedAlarms = createAlarms(3, false);
		this.alarmNumber = 2;
		when(this.alarmStorage.getAll()).thenReturn(returnedAlarms);
		assertThat(this.acl.deactivateAlarm(2), is("Alarm " + this.alarmNumber + " is already inactive"));
	}

	/**
	 * Tests deactivateAlarm with non existent alarm
	 */
	@Test
	protected void testDeactivateAlarmNotFound() {
		when(this.alarmStorage.getAll()).thenReturn(createAlarms(3, true));
		this.alarmNumber = 4;
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateAlarm(this.alarmNumber));
	}

	/**
	 * Tests activateAlarm with active alarm
	 */
	@Test
	protected void testActivateAlarmActive() {
		List<Alarm> returnedAlarms = createAlarms(3, true);
		this.alarmNumber = 2;
		when(this.alarmStorage.getAll()).thenReturn(returnedAlarms);
		assertThat(this.acl.activateAlarm(2), is("Alarm " + this.alarmNumber + " is already active"));
	}

	/**
	 * Tests activateAlarm with inactive alarm
	 */
	@Test
	protected void testActivateAlarmInactive() {
		List<Alarm> returnedAlarms = createAlarms(3, false);
		this.alarmNumber = 2;
		when(this.alarmStorage.getAll()).thenReturn(returnedAlarms);
		assertThat(this.acl.activateAlarm(2), is("Alarm " + this.alarmNumber + " activated"));
	}

	/**
	 * Tests activateAlarm with non existent alarm
	 */
	@Test
	protected void testActivateAlarmNotFound() {
		when(this.alarmStorage.getAll()).thenReturn(createAlarms(3, true));
		this.alarmNumber = 4;
		assertThrows(NoSuchElementException.class, () -> this.acl.activateAlarm(this.alarmNumber));
	}

	/**
	 * Tests getAlarm with valid argument
	 */
	@Test
	protected void testGetAlarm() {
		when(this.alarmStorage.getAll()).thenReturn(createAlarms(3, true));
		this.alarmNumber = 2;
		Alarm returnedAlarm = this.acl.getAlarm(this.alarmNumber);
		assertThat(returnedAlarm.getId(), is(2));
	}

	/**
	 * Tests getAlarm with non existent alarm
	 */
	@Test
	protected void testGetAlarmNotFound() {
		when(this.alarmStorage.getAll()).thenReturn(createAlarms(3, true));
		this.alarmNumber = 4;
		assertThrows(NoSuchElementException.class, () -> this.acl.getAlarm(this.alarmNumber));
	}

	/**
	 * Tests getAllAlarms with some alarms
	 */
	@Test
	protected void testGetAllAlarms() {
		createAlarms(3, true);
		List<Alarm> returnedAlarms = this.acl.getAllAlarms();
		assertThat(returnedAlarms, hasSize(3));
	}

	/**
	 * Tests getAllAlarms with no alarms
	 */
	@Test
	protected void testGetAllAlarmsNoAlarms() {
		List<Alarm> returnedAlarms = this.acl.getAllAlarms();
		assertThat(returnedAlarms, hasSize(0));
	}

	/**
	 * Tests editAlarm with valid arguments
	 */
	@Test
	protected void testEditAlarm() {
		List<Alarm> returnedAlarms = createAlarms(3, true);
		when(this.alarmStorage.getAll()).thenReturn(returnedAlarms);
		this.alarmNumber = 2;
		Alarm a = new Alarm(this.alarmNumber, LocalDateTime.of(LocalDateTime.now().getYear(),
				LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth(), 12, 12), true);
		when(this.env.getCurrentDateTime()).thenReturn(ZonedDateTime.of(2018, 2, 1, 4, 21, 55, 987, ZoneId.of("Z")));
		this.acl.setAlarm(1, 4, 20);
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Instant.class));
		assertThat(this.acl.editAlarm(this.alarmNumber, -1, 12, 12), is(a));
	}

	/**
	 * Tests editAlarm with non existent alarm
	 */
	@Test
	protected void testEditAlarmNotFound() {
		when(this.alarmStorage.getAll()).thenReturn(createAlarms(3, true));
		this.alarmNumber = 4;
		assertThrows(NoSuchElementException.class, () -> this.acl.editAlarm(this.alarmNumber, 1, 12, 12));
	}

	private List<Alarm> createAlarms(int amount, boolean active) {
		for (int i = 1; i <= amount; i++) {
			Alarm mockAlarm = new Alarm(i, LocalDateTime.of(2018, 8, 21, 21, 21), active);
			this.alarms.add(mockAlarm);
		}
		return this.alarms;
	}

	/**
	 * Get's {@link #abs abs}
	 * 
	 * @return abs
	 */
	public AlarmBeepService getAbs() {
		return this.abs;
	}

	/**
	 * Set's {@link #abs abs}
	 * 
	 * @param abs
	 *            abs
	 */
	public void setAbs(AlarmBeepService abs) {
		this.abs = abs;
	}
}
