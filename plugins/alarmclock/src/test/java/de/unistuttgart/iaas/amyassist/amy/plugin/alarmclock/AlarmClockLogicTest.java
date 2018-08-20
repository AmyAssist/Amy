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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

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

	private ITimerStorage timerStorage;

	private Environment env;

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.env = this.framework.mockService(Environment.class);
		this.scheduler = this.framework.mockService(TaskScheduler.class);
		this.timerStorage = this.framework.mockService(ITimerStorage.class);
		this.abs = this.framework.mockService(AlarmBeepService.class);
		this.alarmStorage = this.framework.mockService(AlarmRegistry.class);
		this.acl = this.framework.setServiceUnderTest(AlarmClockLogic.class);
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
	 * Tests setTimer with normal arguments
	 */
	@Test
	public void testSetTimer() {

		when(this.timerStorage.incrementTimerCounter()).thenReturn(1);
		this.acl.setTimer(12, 35, 40);
		verify(this.timerStorage).incrementTimerCounter();
		verify(this.timerStorage).storeTimer(ArgumentMatchers.any(Timer.class));
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Instant.class));

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
		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Alarm a1 = new Alarm(5, alarmTime, false);
		this.alarmStorage.save(a1);
		Alarm a2 = new Alarm(5, alarmTime, false);
		this.alarmStorage.save(a2);
		Alarm a3 = new Alarm(5, alarmTime, false);
		this.alarmStorage.save(a3);

		for (Alarm alarm : this.alarmStorage.getAll()) {
			this.alarmStorage.deleteById(alarm.getPersistentId());
		}

		assertThat(this.acl.resetAlarms(), is("3 alarms deleted"));
		assertThat(this.alarmStorage.getAll().size(), is(0));
	}

	/**
	 * Tests resetAlarms with no alarms
	 */
	@Test
	public void testResetAlarmsNoAlarms() {
		assertThat(this.acl.resetAlarms(), is("No alarms found"));
		verify(this.timerStorage).putAlarmCounter(0);
		verify(this.timerStorage, never()).deleteAlarm(ArgumentMatchers.anyInt());
	}

	/**
	 * Tests resetTimers
	 */
	@Test
	protected void testResetTimers() {
		when(this.timerStorage.getTimerCounter()).thenReturn(10);
		when(this.timerStorage.hasTimer(2)).thenReturn(true);
		when(this.timerStorage.hasTimer(6)).thenReturn(true);
		when(this.timerStorage.getTimer(2)).thenReturn(new Timer(2, Calendar.getInstance(), true));
		when(this.timerStorage.getTimer(6)).thenReturn(new Timer(6, Calendar.getInstance(), true));

		assertThat(this.acl.resetTimers(), is("2 timers deleted"));
		verify(this.timerStorage).putTimerCounter(0);
		verify(this.timerStorage, times(12)).hasTimer(ArgumentMatchers.anyInt());
		verify(this.timerStorage).deleteTimer(2);
		verify(this.timerStorage).deleteTimer(6);
	}

	/**
	 * Tests resetTimers with no timers
	 */
	@Test
	protected void testResetTimersNoTimers() {
		assertThat(this.acl.resetTimers(), is("No timers found"));
		verify(this.timerStorage).putTimerCounter(0);
		verify(this.timerStorage, never()).deleteAlarm(ArgumentMatchers.anyInt());
	}

	/**
	 * Tests deleteAlarm
	 */
	@Test
	protected void testDeleteAlarm() {
		when(this.timerStorage.hasAlarm(2)).thenReturn(true);
		when(this.timerStorage.getAlarm(2)).thenReturn(new Alarm(2, 4, 20, true));
		assertThat(this.acl.deleteAlarm(2), is("Alarm 2 deleted"));
		verify(this.timerStorage, times(2)).hasAlarm(2);
		verify(this.timerStorage).deleteAlarm(2);
	}

	/**
	 * Tests deleteAlarm with non existent alarm
	 */
	@Test
	protected void testDeleteAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deleteAlarm(4));
		verify(this.timerStorage).hasAlarm(4);
		verify(this.timerStorage, never()).deleteAlarm(4);
	}

	/**
	 * Tests deleteTimer
	 */
	@Test
	protected void testDeleteTimer() {
		when(this.timerStorage.hasTimer(2)).thenReturn(true);
		when(this.timerStorage.getTimer(2)).thenReturn(new Timer(2, Calendar.getInstance(), true));
		assertThat(this.acl.deleteTimer(2), is("Timer 2 deleted"));
		verify(this.timerStorage, times(2)).hasTimer(2);
		verify(this.timerStorage).deleteTimer(2);
	}

	/**
	 * Tests deleteTimer with non existent timer
	 */
	@Test
	protected void testDeleteTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deleteTimer(4));
		verify(this.timerStorage).hasTimer(4);
		verify(this.timerStorage, never()).deleteTimer(4);
	}

	/**
	 * Tests deactivateAlarm with active alarm
	 */
	@Test
	protected void testDeactivateAlarmActive() {
		Alarm alarm2 = new Alarm(2, 12, 25, true);

		when(this.timerStorage.hasAlarm(2)).thenReturn(true);
		when(this.timerStorage.getAlarm(2)).thenReturn(alarm2);

		assertThat(this.acl.deactivateAlarm(2), is("Alarm 2 deactivated"));
		verify(this.timerStorage).hasAlarm(2);
		verify(this.timerStorage).getAlarm(2);
		alarm2.setActive(false);
		verify(this.timerStorage).storeAlarm(alarm2);
	}

	/**
	 * Tests deactivateAlarm with inactive alarm
	 */
	@Test
	protected void testDeactivateAlarmInactive() {
		Alarm alarm8 = new Alarm(8, 22, 57, false);

		when(this.timerStorage.hasAlarm(8)).thenReturn(true);
		when(this.timerStorage.getAlarm(8)).thenReturn(alarm8);

		assertThat(this.acl.deactivateAlarm(8), is("Alarm 8 is already inactive"));
		verify(this.timerStorage).hasAlarm(8);
		verify(this.timerStorage).getAlarm(8);
		verify(this.timerStorage, never()).storeAlarm(ArgumentMatchers.any(Alarm.class));
	}

	/**
	 * Tests deactivateAlarm with non existent alarm
	 */
	@Test
	protected void testDeactivateAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateAlarm(10));
		verify(this.timerStorage, only()).hasAlarm(10);
	}

	/**
	 * Tests deactivateTimer with active timer
	 */
	@Test
	protected void testDeactivateTimerActive() {
		Timer timer2 = new Timer(2, 12, 0, 0, true);

		when(this.timerStorage.hasTimer(2)).thenReturn(true);
		when(this.timerStorage.getTimer(2)).thenReturn(timer2);

		assertThat(this.acl.deactivateTimer(2), is("Timer 2 deactivated"));
		verify(this.timerStorage).hasTimer(2);
		verify(this.timerStorage).getTimer(2);
		verify(this.timerStorage).storeTimer(timer2);
	}

	/**
	 * Tests deactivateTimer with inactive timer
	 */
	@Test
	protected void testDeactivateTimerInactive() {
		Timer timer8 = new Timer(8, 22, 57, 44, false);

		when(this.timerStorage.hasTimer(8)).thenReturn(true);
		when(this.timerStorage.getTimer(8)).thenReturn(timer8);

		assertThat(this.acl.deactivateTimer(8), is("Timer 8 is already inactive"));
		verify(this.timerStorage).hasTimer(8);
		verify(this.timerStorage).getTimer(8);
		verify(this.timerStorage, never()).storeTimer(ArgumentMatchers.any(Timer.class));
	}

	/**
	 * Tests deactivateTimer with non existent timer
	 */
	@Test
	protected void testDeactivateTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateTimer(10));
		verify(this.timerStorage, only()).hasTimer(10);
	}

	/**
	 * Tests activateAlarm with active alarm
	 */
	@Test
	protected void testActivateAlarmActive() {
		Alarm alarm2 = new Alarm(2, 12, 25, true);

		when(this.timerStorage.hasAlarm(2)).thenReturn(true);
		when(this.timerStorage.getAlarm(2)).thenReturn(alarm2);

		assertThat(this.acl.activateAlarm(2), is("Alarm 2 is already active"));
		verify(this.timerStorage).hasAlarm(2);
		verify(this.timerStorage).getAlarm(2);
		verify(this.timerStorage, never()).storeAlarm(ArgumentMatchers.any(Alarm.class));
	}

	/**
	 * Tests activateAlarm with inactive alarm
	 */
	@Test
	protected void testActivateAlarmInactive() {
		Alarm alarm8 = new Alarm(8, 22, 57, false);

		when(this.timerStorage.hasAlarm(8)).thenReturn(true);
		when(this.timerStorage.getAlarm(8)).thenReturn(alarm8);

		assertThat(this.acl.activateAlarm(8), is("Alarm 8 activated"));
		verify(this.timerStorage).hasAlarm(8);
		verify(this.timerStorage).getAlarm(8);
		verify(this.timerStorage).storeAlarm(alarm8);
	}

	/**
	 * Tests activateAlarm with non existent alarm
	 */
	@Test
	protected void testActivateAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.activateAlarm(10));
		verify(this.timerStorage, only()).hasAlarm(10);
	}

	/**
	 * Tests activateTimer with non existent alarm
	 */
	@Test
	protected void testActivateTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateTimer(10));
		verify(this.timerStorage, only()).hasTimer(10);
	}

	/**
	 * Tests getAlarm with valid argument
	 */
	@Test
	protected void testGetAlarm() {
		Alarm alarm1 = new Alarm(1, 20, 15, true);

		when(this.timerStorage.hasAlarm(1)).thenReturn(true);
		when(this.timerStorage.getAlarm(1)).thenReturn(alarm1);
		assertThat(this.acl.getAlarm(1), is(alarm1));
		verify(this.timerStorage).hasAlarm(1);
		verify(this.timerStorage).getAlarm(1);
		reset(this.timerStorage);
	}

	/**
	 * Tests getAlarm with non existent alarm
	 */
	@Test
	protected void testGetAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.getAlarm(3));
		verify(this.timerStorage, only()).hasAlarm(3);
	}

	/**
	 * Tests getTimer with valid argument
	 */
	@Test
	protected void testGetTimer() {
		Timer timer1 = new Timer(1, 15, 20, 15, true);

		when(this.timerStorage.hasTimer(1)).thenReturn(true);
		when(this.timerStorage.getTimer(1)).thenReturn(timer1);
		assertThat(this.acl.getTimer(1), is(timer1));
		verify(this.timerStorage).hasTimer(1);
		verify(this.timerStorage).getTimer(1);
		reset(this.timerStorage);
	}

	/**
	 * Tests getTimer with non existent timer
	 */
	@Test
	protected void testGetTimerNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.getTimer(3));
		verify(this.timerStorage, only()).hasTimer(3);
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
		when(this.timerStorage.getAlarmCounter()).thenReturn(10);
		when(this.timerStorage.hasAlarm(1)).thenReturn(true);
		when(this.timerStorage.getAlarm(1)).thenReturn(alarm1);
		when(this.timerStorage.hasAlarm(2)).thenReturn(true);
		when(this.timerStorage.getAlarm(2)).thenReturn(alarm2);
		when(this.timerStorage.hasAlarm(5)).thenReturn(true);
		when(this.timerStorage.getAlarm(5)).thenReturn(alarm5);

		assertThat(this.acl.getAllAlarms(), is(alarms));
		verify(this.timerStorage, times(10)).hasAlarm(ArgumentMatchers.anyInt());
	}

	/**
	 * Tests getAllAlarms with no alarms
	 */
	@Test
	protected void testGetAllAlarmsNoAlarms() {
		when(this.timerStorage.getAlarmCounter()).thenReturn(10);
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
		when(this.timerStorage.getTimerCounter()).thenReturn(10);
		when(this.timerStorage.hasTimer(1)).thenReturn(true);
		when(this.timerStorage.getTimer(1)).thenReturn(timer1);
		when(this.timerStorage.hasTimer(2)).thenReturn(true);
		when(this.timerStorage.getTimer(2)).thenReturn(timer2);
		when(this.timerStorage.hasTimer(5)).thenReturn(true);
		when(this.timerStorage.getTimer(5)).thenReturn(timer5);

		assertThat(this.acl.getAllTimers(), is(timers));
		verify(this.timerStorage, times(10)).hasTimer(ArgumentMatchers.anyInt());
	}

	/**
	 * Tests getAllTimers with no timers
	 */
	@Test
	protected void testGetAllTimersNoTimers() {
		when(this.timerStorage.getTimerCounter()).thenReturn(10);
		assertThat(this.acl.getAllTimers(), is(new ArrayList<Timer>()));
	}

	/**
	 * Tests editAlarm with valid arguments
	 */
	@Test
	protected void testEditAlarm() {
		Alarm alarm1 = new Alarm(1, 15, 50, true);
		when(this.timerStorage.hasAlarm(1)).thenReturn(true);
		when(this.timerStorage.getAlarm(1)).thenReturn(alarm1);

		alarm1.setTime(4, 20);
		assertThat(this.acl.editAlarm(1, 4, 20), is(alarm1));
		verify(this.timerStorage).hasAlarm(1);
		verify(this.timerStorage).getAlarm(1);
		verify(this.timerStorage).storeAlarm(alarm1);
	}

	/**
	 * Tests editAlarm with non existent alarm
	 */
	@Test
	protected void testEditAlarmNotFound() {
		assertThrows(NoSuchElementException.class, () -> this.acl.editAlarm(1, 15, 20));
	}
}
