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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for the AlarmClockLogic class
 * 
 * @author Patrick Singer
 */
@ExtendWith(FrameworkExtension.class)
public class AlarmClockLogicTest {

	@Reference
	private TestFramework framework;

	private AlarmClockLogic acl;

	private AlarmBeepService abs;

	private TaskSchedulerAPI scheduler;

	private IAlarmClockStorage acStorage;

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.scheduler = this.framework.mockService(TaskSchedulerAPI.class);
		this.acStorage = this.framework.mockService(IAlarmClockStorage.class);
		this.abs = this.framework.mockService(AlarmBeepService.class);
		this.acl = this.framework.setServiceUnderTest(AlarmClockLogic.class);
	}

	/**
	 * Tests set Alarm with normal arguments
	 */
	@Test
	public void testSetAlarm() {
		when(this.acStorage.incrementAlarmCounter()).thenReturn(1);
		this.acl.setAlarm(4, 20);
		verify(this.acStorage).incrementAlarmCounter();
		verify(this.acStorage).storeAlarm(ArgumentMatchers.any(Alarm.class));
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
	}

	/**
	 * Tests setAlarm with invalid arguments
	 */
	@Test
	public void testSetAlarmInvalid() {
		assertThrows(IllegalArgumentException.class, () -> this.acl.setAlarm(12, 60));
		assertThrows(IllegalArgumentException.class, () -> this.acl.setAlarm(24, 40));
	}

	/**
	 * Tests setTimer with normal arguments
	 */
	@Test
	public void testSetTimer() {

		when(this.acStorage.incrementTimerCounter()).thenReturn(1);
		this.acl.setTimer(12, 35, 40);
		verify(this.acStorage).incrementTimerCounter();
		verify(this.acStorage).storeTimer(ArgumentMatchers.any(Timer.class));
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));

	}

	/**
	 * Tests setTimer with invalid arguments
	 */
	@Test
	public void testSetTimerInvalid() {
		assertThrows(IllegalArgumentException.class, () -> this.acl.setTimer(0, 0, 0));
	}

	/**
	 * Tests resetAlarms
	 */
	@Test
	public void testResetAlarms() {
		when(this.acStorage.getAlarmCounter()).thenReturn(10);
		when(this.acStorage.hasAlarm(4)).thenReturn(true);
		when(this.acStorage.hasAlarm(8)).thenReturn(true);

		assertThat(this.acl.resetAlarms(), is("2 alarms deleted"));
		verify(this.acStorage).putAlarmCounter(0);
		verify(this.acStorage, times(10)).hasAlarm(ArgumentMatchers.anyInt());
		verify(this.acStorage).deleteAlarm(4);
		verify(this.acStorage).deleteAlarm(8);
	}

	/**
	 * Tests resetAlarms with no alarms
	 */
	@Test
	public void testResetAlarmsNoAlarms() {
		assertThat(this.acl.resetAlarms(), is("No alarms found"));
		verify(this.acStorage, never()).putAlarmCounter(0);
		verify(this.acStorage, never()).deleteAlarm(ArgumentMatchers.anyInt());
	}

	/**
	 * Tests resetTimers
	 */
	@Test
	protected void testResetTimers() {
		when(this.acStorage.getTimerCounter()).thenReturn(10);
		when(this.acStorage.hasTimer(2)).thenReturn(true);
		when(this.acStorage.hasTimer(6)).thenReturn(true);

		assertThat(this.acl.resetTimers(), is("2 timers deleted"));
		verify(this.acStorage).putTimerCounter(0);
		verify(this.acStorage, times(10)).hasTimer(ArgumentMatchers.anyInt());
		verify(this.acStorage).deleteTimer(2);
		verify(this.acStorage).deleteTimer(6);
	}

	/**
	 * Tests resetTimers with no timers
	 */
	@Test
	protected void testResetTimersNoTimers() {
		assertThat(this.acl.resetTimers(), is("No timers found"));
		verify(this.acStorage, never()).putTimerCounter(0);
		verify(this.acStorage, never()).deleteAlarm(ArgumentMatchers.anyInt());
	}

	/**
	 * Tests deleteAlarm
	 */
	@Test
	protected void testDeleteAlarm() {
		when(this.acStorage.hasAlarm(2)).thenReturn(true);
		assertThat(this.acl.deleteAlarm(2), is("Alarm 2 deleted"));
		verify(this.acStorage).hasAlarm(2);
		verify(this.acStorage).deleteAlarm(2);
	}

	/**
	 * Tests deleteAlarm with non existent alarm
	 */
	@Test
	protected void testDeleteAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deleteAlarm(4));
		verify(this.acStorage).hasAlarm(4);
		verify(this.acStorage, never()).deleteAlarm(4);
	}

	/**
	 * Tests deleteTimer
	 */
	@Test
	protected void testDeleteTimer() {
		when(this.acStorage.hasTimer(2)).thenReturn(true);
		assertThat(this.acl.deleteTimer(2), is("Timer 2 deleted"));
		verify(this.acStorage).hasTimer(2);
		verify(this.acStorage).deleteTimer(2);
	}

	/**
	 * Tests deleteTimer with non existent timer
	 */
	@Test
	protected void testDeleteTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deleteTimer(4));
		verify(this.acStorage).hasTimer(4);
		verify(this.acStorage, never()).deleteTimer(4);
	}

	/**
	 * Tests deactivateAlarm with active alarm
	 */
	@Test
	protected void testDeactivateAlarmActive() {
		Alarm alarm2 = new Alarm(2, 12, 25, true);

		when(this.acStorage.hasAlarm(2)).thenReturn(true);
		when(this.acStorage.getAlarm(2)).thenReturn(alarm2);

		assertThat(this.acl.deactivateAlarm(2), is("Alarm 2 deactivated"));
		verify(this.acStorage).hasAlarm(2);
		verify(this.acStorage).getAlarm(2);
		alarm2.setActive(false);
		verify(this.acStorage).storeAlarm(alarm2);
	}

	/**
	 * Tests deactivateAlarm with inactive alarm
	 */
	@Test
	protected void testDeactivateAlarmInactive() {
		Alarm alarm8 = new Alarm(8, 22, 57, false);

		when(this.acStorage.hasAlarm(8)).thenReturn(true);
		when(this.acStorage.getAlarm(8)).thenReturn(alarm8);

		assertThat(this.acl.deactivateAlarm(8), is("Alarm 8 is already inactive"));
		verify(this.acStorage).hasAlarm(8);
		verify(this.acStorage).getAlarm(8);
		verify(this.acStorage, never()).storeAlarm(ArgumentMatchers.any(Alarm.class));
	}

	/**
	 * Tests deactivateAlarm with non existent alarm
	 */
	@Test
	protected void testDeactivateAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateAlarm(10));
		verify(this.acStorage, only()).hasAlarm(10);
	}

	/**
	 * Tests deactivateTimer with active timer
	 */
	@Test
	protected void testDeactivateTimerActive() {
		Timer timer2 = new Timer(2, 12, 0, 0, true);

		when(this.acStorage.hasTimer(2)).thenReturn(true);
		when(this.acStorage.getTimer(2)).thenReturn(timer2);

		assertThat(this.acl.deactivateTimer(2), is("Timer 2 deactivated"));
		verify(this.acStorage).hasTimer(2);
		verify(this.acStorage).getTimer(2);
		verify(this.acStorage).storeTimer(timer2);
	}

	/**
	 * Tests deactivateTimer with inactive timer
	 */
	@Test
	protected void testDeactivateTimerInactive() {
		Timer timer8 = new Timer(8, 22, 57, 44, false);

		when(this.acStorage.hasTimer(8)).thenReturn(true);
		when(this.acStorage.getTimer(8)).thenReturn(timer8);

		assertThat(this.acl.deactivateTimer(8), is("Timer 8 is already inactive"));
		verify(this.acStorage).hasTimer(8);
		verify(this.acStorage).getTimer(8);
		verify(this.acStorage, never()).storeTimer(ArgumentMatchers.any(Timer.class));
	}

	/**
	 * Tests deactivateTimer with non existent timer
	 */
	@Test
	protected void testDeactivateTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateTimer(10));
		verify(this.acStorage, only()).hasTimer(10);
	}

	/**
	 * Tests activateAlarm with active alarm
	 */
	@Test
	protected void testActivateAlarmActive() {
		Alarm alarm2 = new Alarm(2, 12, 25, true);

		when(this.acStorage.hasAlarm(2)).thenReturn(true);
		when(this.acStorage.getAlarm(2)).thenReturn(alarm2);

		assertThat(this.acl.activateAlarm(2), is("Alarm 2 is already active"));
		verify(this.acStorage).hasAlarm(2);
		verify(this.acStorage).getAlarm(2);
		verify(this.acStorage, never()).storeAlarm(ArgumentMatchers.any(Alarm.class));
	}

	/**
	 * Tests activateAlarm with inactive alarm
	 */
	@Test
	protected void testActivateAlarmInactive() {
		Alarm alarm8 = new Alarm(8, 22, 57, false);

		when(this.acStorage.hasAlarm(8)).thenReturn(true);
		when(this.acStorage.getAlarm(8)).thenReturn(alarm8);

		assertThat(this.acl.activateAlarm(8), is("Alarm 8 activated"));
		verify(this.acStorage).hasAlarm(8);
		verify(this.acStorage).getAlarm(8);
		verify(this.acStorage).storeAlarm(alarm8);
	}

	/**
	 * Tests activateAlarm with non existent alarm
	 */
	@Test
	protected void testActivateAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.activateAlarm(10));
		verify(this.acStorage, only()).hasAlarm(10);
	}

	/**
	 * Tests activateTimer with non existent alarm
	 */
	@Test
	protected void testActivateTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateTimer(10));
		verify(this.acStorage, only()).hasTimer(10);
	}

	/**
	 * Tests getAlarm with valid argument
	 */
	@Test
	protected void testGetAlarm() {
		Alarm alarm1 = new Alarm(1, 20, 15, true);

		when(this.acStorage.hasAlarm(1)).thenReturn(true);
		when(this.acStorage.getAlarm(1)).thenReturn(alarm1);
		assertThat(this.acl.getAlarm(1), is(alarm1));
		verify(this.acStorage).hasAlarm(1);
		verify(this.acStorage).getAlarm(1);
		reset(this.acStorage);
	}

	/**
	 * Tests getAlarm with non existent alarm
	 */
	@Test
	protected void testGetAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.getAlarm(3));
		verify(this.acStorage, only()).hasAlarm(3);
	}

	/**
	 * Tests getTimer with valid argument
	 */
	@Test
	protected void testGetTimer() {
		Timer timer1 = new Timer(1, 15, 20, 15, true);

		when(this.acStorage.hasTimer(1)).thenReturn(true);
		when(this.acStorage.getTimer(1)).thenReturn(timer1);
		assertThat(this.acl.getTimer(1), is(timer1));
		verify(this.acStorage).hasTimer(1);
		verify(this.acStorage).getTimer(1);
		reset(this.acStorage);
	}

	/**
	 * Tests getTimer with non existent timer
	 */
	@Test
	protected void testGetTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.getTimer(3));
		verify(this.acStorage, only()).hasTimer(3);
	}

	/**
	 * Tests getAllAlarms with some alarms
	 */
	@Test
	protected void testGetAllAlarms() {
		Alarm alarm1 = new Alarm(1, 20, 15, true);
		Alarm alarm2 = new Alarm(2, 6, 30, false);
		Alarm alarm5 = new Alarm(5, 10, 0, true);
		List<Alarm> alarms = new ArrayList<Alarm>();
		alarms.add(alarm1);
		alarms.add(alarm2);
		alarms.add(alarm5);
		when(this.acStorage.getAlarmCounter()).thenReturn(10);
		when(this.acStorage.hasAlarm(1)).thenReturn(true);
		when(this.acStorage.getAlarm(1)).thenReturn(alarm1);
		when(this.acStorage.hasAlarm(2)).thenReturn(true);
		when(this.acStorage.getAlarm(2)).thenReturn(alarm2);
		when(this.acStorage.hasAlarm(5)).thenReturn(true);
		when(this.acStorage.getAlarm(5)).thenReturn(alarm5);

		assertThat(this.acl.getAllAlarms(), is(alarms));
		verify(this.acStorage, times(10)).hasAlarm(ArgumentMatchers.anyInt());
	}

	/**
	 * Tests getAllAlarms with no alarms
	 */
	@Test
	protected void testGetAllAlarmsNoAlarms() {
		when(this.acStorage.getAlarmCounter()).thenReturn(10);
		assertThat(this.acl.getAllAlarms(), is(new ArrayList<Alarm>()));
	}

	/**
	 * Tests getAllTimers with some timers
	 */
	@Test
	protected void testGetAllTimers() {
		Timer timer1 = new Timer(1, 20, 5, 15, true);
		Timer timer2 = new Timer(2, 0, 6, 30, false);
		Timer timer5 = new Timer(5, 30, 10, 0, true);
		List<Timer> timers = new ArrayList<Timer>();
		timers.add(timer1);
		timers.add(timer2);
		timers.add(timer5);
		when(this.acStorage.getTimerCounter()).thenReturn(10);
		when(this.acStorage.hasTimer(1)).thenReturn(true);
		when(this.acStorage.getTimer(1)).thenReturn(timer1);
		when(this.acStorage.hasTimer(2)).thenReturn(true);
		when(this.acStorage.getTimer(2)).thenReturn(timer2);
		when(this.acStorage.hasTimer(5)).thenReturn(true);
		when(this.acStorage.getTimer(5)).thenReturn(timer5);

		assertThat(this.acl.getAllTimers(), is(timers));
		verify(this.acStorage, times(10)).hasTimer(ArgumentMatchers.anyInt());
	}

	/**
	 * Tests getAllTimers with no timers
	 */
	@Test
	protected void testGetAllTimersNoTimers() {
		when(this.acStorage.getTimerCounter()).thenReturn(10);
		assertThat(this.acl.getAllTimers(), is(new ArrayList<Timer>()));
	}

	/**
	 * Tests editAlarm with valid arguments
	 */
	@Test
	protected void testEditAlarm() {
		Alarm alarm1 = new Alarm(1, 15, 50, true);
		when(this.acStorage.hasAlarm(1)).thenReturn(true);
		when(this.acStorage.getAlarm(1)).thenReturn(alarm1);

		alarm1.setTime(4, 20);
		assertThat(this.acl.editAlarm(1, 4, 20), is(alarm1));
		verify(this.acStorage).hasAlarm(1);
		verify(this.acStorage).getAlarm(1);
		verify(this.acStorage).storeAlarm(alarm1);
	}

	/**
	 * Tests editAlarm with non existent alarm
	 */
	@Test
	protected void testEditAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.editAlarm(1, 15, 20));
	}
}
