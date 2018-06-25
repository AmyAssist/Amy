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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
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

	private TaskSchedulerAPI scheduler;

	private IAlarmClockStorage acStorage;

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.scheduler = this.framework.mockService(TaskSchedulerAPI.class);
		this.acStorage = this.framework.mockService(IAlarmClockStorage.class);
		this.acl = this.framework.setServiceUnderTest(AlarmClockLogic.class);
	}

	/**
	 * Tests the setAlarm method
	 */
	@Test
	public void testSetAlarm() {
		// 1
		when(this.acStorage.incrementAlarmCounter()).thenReturn(1);
		this.acl.setAlarm(new int[] { 0, 0 });
		verify(this.acStorage).incrementAlarmCounter();
		verify(this.acStorage).storeAlarm(ArgumentMatchers.any(Alarm.class));
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
		reset(this.acStorage);
		reset(this.scheduler);

		// 2
		when(this.acStorage.incrementAlarmCounter()).thenReturn(1);
		this.acl.setAlarm(new int[] { 23, 59 });
		verify(this.acStorage).incrementAlarmCounter();
		verify(this.acStorage).storeAlarm(ArgumentMatchers.any(Alarm.class));
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
	}

	/**
	 * Tests the setTimer method
	 */
	@Test
	public void testSetTimer() {

		// 1
		when(this.acStorage.incrementTimerCounter()).thenReturn(1);
		this.acl.setTimer(0, 2, 0);
		verify(this.acStorage).incrementTimerCounter();
		verify(this.acStorage).storeTimer(ArgumentMatchers.any(Timer.class));
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
		reset(this.acStorage);
		reset(this.scheduler);

		// 2
		when(this.acStorage.incrementTimerCounter()).thenReturn(1);
		this.acl.setTimer(12, 35, 40);
		verify(this.acStorage).incrementTimerCounter();
		verify(this.acStorage).storeTimer(ArgumentMatchers.any(Timer.class));
		verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));

		// Illegal Arguments
		assertThrows(IllegalArgumentException.class, () -> this.acl.setTimer(0, 0, 0));
	}

	/**
	 * Tests the resetAlarms method
	 */
	@Test
	public void testResetAlarms() {

		// no alarms
		assertThat(this.acl.resetAlarms(), is("No alarms found"));
		verify(this.acStorage, never()).putAlarmCounter(0);
		verify(this.acStorage, never()).deleteKey(ArgumentMatchers.anyString());

		// 1
		when(this.acStorage.getAlarmCounter()).thenReturn(10);
		when(this.acStorage.hasKey("alarm4")).thenReturn(true);
		when(this.acStorage.hasKey("alarm8")).thenReturn(true);

		assertThat(this.acl.resetAlarms(), is("2 alarms deleted"));
		verify(this.acStorage).putAlarmCounter(0);
		verify(this.acStorage, times(10)).hasKey(ArgumentMatchers.anyString());
		verify(this.acStorage).deleteKey("alarm4");
		verify(this.acStorage).deleteKey("alarm8");
	}

	/**
	 * Tests the resetTimers method
	 */
	@Test
	protected void testResetTimers() {
		// no timers
		assertThat(this.acl.resetTimers(), is("No timers found"));
		verify(this.acStorage, never()).putTimerCounter(0);
		verify(this.acStorage, never()).deleteKey(ArgumentMatchers.anyString());

		// 1
		when(this.acStorage.getTimerCounter()).thenReturn(10);
		when(this.acStorage.hasKey("timer2")).thenReturn(true);
		when(this.acStorage.hasKey("timer6")).thenReturn(true);

		assertThat(this.acl.resetTimers(), is("2 timers deleted"));
		verify(this.acStorage).putTimerCounter(0);
		verify(this.acStorage, times(10)).hasKey(ArgumentMatchers.anyString());
		verify(this.acStorage).deleteKey("timer2");
		verify(this.acStorage).deleteKey("timer6");
	}

	//
	/**
	 * Tests the deleteAlarm method
	 */
	@Test
	protected void testDeleteAlarm() {
		// 1
		when(this.acStorage.hasKey("alarm2")).thenReturn(true);
		assertThat(this.acl.deleteAlarm(2), is("Alarm 2 deleted"));
		verify(this.acStorage).hasKey("alarm2");
		verify(this.acStorage).deleteKey("alarm2");

		// 2
		assertThrows(NoSuchElementException.class, () -> this.acl.deleteAlarm(4));
		verify(this.acStorage).hasKey("alarm4");
		verify(this.acStorage, never()).deleteKey("alarm4");
	}

	/**
	 * Tests the deleteTimer method
	 */
	@Test
	protected void testDeleteTimer() {
		// 1
		when(this.acStorage.hasKey("timer2")).thenReturn(true);
		assertThat(this.acl.deleteTimer(2), is("Timer 2 deleted"));
		verify(this.acStorage).hasKey("timer2");
		verify(this.acStorage).deleteKey("timer2");

		// 2
		assertThrows(NoSuchElementException.class, () -> this.acl.deleteTimer(4));
		verify(this.acStorage).hasKey("timer4");
		verify(this.acStorage, never()).deleteKey("timer4");
	}

	/**
	 * Tests the deactivateAlarm method
	 */
	@Test
	protected void testDeactivateAlarm() {
		Alarm alarm2 = new Alarm(2, 12, 25, true);
		Alarm alarm8 = new Alarm(8, 22, 57, false);

		// active
		when(this.acStorage.hasKey("alarm2")).thenReturn(true);
		when(this.acStorage.getAlarm(2)).thenReturn(alarm2);

		assertThat(this.acl.deactivateAlarm(2), is("Alarm 2 deactivated"));
		verify(this.acStorage).hasKey("alarm2");
		verify(this.acStorage).getAlarm(2);
		alarm2.setActive(false);
		verify(this.acStorage).storeAlarm(alarm2);
		reset(this.acStorage);

		// inactive
		when(this.acStorage.hasKey("alarm8")).thenReturn(true);
		when(this.acStorage.getAlarm(8)).thenReturn(alarm8);

		assertThat(this.acl.deactivateAlarm(8), is("Alarm 8 is already inactive"));
		verify(this.acStorage).hasKey("alarm8");
		verify(this.acStorage).getAlarm(8);
		verify(this.acStorage, never()).storeAlarm(ArgumentMatchers.any(Alarm.class));
		reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateAlarm(10));
		verify(this.acStorage, only()).hasKey("alarm10");
	}

	/**
	 * Tests the deactivateTimer method
	 */
	@Test
	protected void testDeactivateTimer() {
		Timer timer2 = new Timer(2, 12, 0, 0, true);
		Timer timer8 = new Timer(8, 22, 57, 44, false);

		// active
		when(this.acStorage.hasKey("timer2")).thenReturn(true);
		when(this.acStorage.getTimer(2)).thenReturn(timer2);

		assertThat(this.acl.deactivateTimer(2), is("Timer 2 deactivated"));
		verify(this.acStorage).hasKey("timer2");
		verify(this.acStorage).getTimer(2);
		timer2.setActive(false);
		verify(this.acStorage).storeTimer(timer2);
		reset(this.acStorage);

		// inactive
		when(this.acStorage.hasKey("timer8")).thenReturn(true);
		when(this.acStorage.getTimer(8)).thenReturn(timer8);

		assertThat(this.acl.deactivateTimer(8), is("Timer 8 is already inactive"));
		verify(this.acStorage).hasKey("timer8");
		verify(this.acStorage).getTimer(8);
		verify(this.acStorage, never()).storeTimer(ArgumentMatchers.any(Timer.class));
		reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateTimer(10));
		verify(this.acStorage, only()).hasKey("timer10");
	}

	/**
	 * Tests the activateAlarm method
	 */
	@Test
	protected void testActivateAlarm() {
		Alarm alarm2 = new Alarm(2, 12, 25, true);
		Alarm alarm8 = new Alarm(8, 22, 57, false);

		// active
		when(this.acStorage.hasKey("alarm2")).thenReturn(true);
		when(this.acStorage.getAlarm(2)).thenReturn(alarm2);

		assertThat(this.acl.activateAlarm(2), is("Alarm 2 is already active"));
		verify(this.acStorage).hasKey("alarm2");
		verify(this.acStorage).getAlarm(2);
		verify(this.acStorage, never()).storeAlarm(ArgumentMatchers.any(Alarm.class));
		reset(this.acStorage);

		// inactive
		when(this.acStorage.hasKey("alarm8")).thenReturn(true);
		when(this.acStorage.getAlarm(8)).thenReturn(alarm8);

		assertThat(this.acl.activateAlarm(8), is("Alarm 8 activated"));
		verify(this.acStorage).hasKey("alarm8");
		verify(this.acStorage).getAlarm(8);
		alarm2.setActive(false);
		verify(this.acStorage).storeAlarm(alarm8);
		reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.activateAlarm(10));
		verify(this.acStorage, only()).hasKey("alarm10");
	}

	/**
	 * Tests the activateTimer method
	 */
	@Test
	protected void testActivateTimer() {
		Timer timer2 = new Timer(2, 12, 0, 0, true);
		Timer timer8 = new Timer(8, 22, 57, 44, false);

		// active
		when(this.acStorage.hasKey("timer2")).thenReturn(true);
		when(this.acStorage.getTimer(2)).thenReturn(timer2);

		assertThat(this.acl.activateTimer(2), is("Timer 2 is already active"));
		verify(this.acStorage).hasKey("timer2");
		verify(this.acStorage).getTimer(2);
		verify(this.acStorage, never()).storeTimer(ArgumentMatchers.any(Timer.class));
		reset(this.acStorage);

		// inactive
		when(this.acStorage.hasKey("timer8")).thenReturn(true);
		when(this.acStorage.getTimer(8)).thenReturn(timer8);

		assertThat(this.acl.activateTimer(8), is("Timer 8 activated"));
		verify(this.acStorage).hasKey("timer8");
		verify(this.acStorage).getTimer(8);
		timer2.setActive(false);
		verify(this.acStorage).storeTimer(timer8);
		reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateTimer(10));
		verify(this.acStorage, only()).hasKey("timer10");
	}

	/**
	 * Tests the getAlarm method
	 */
	@Test
	protected void testGetAlarm() {
		Alarm alarm1 = new Alarm(1, 20, 15, true);

		// 1
		when(this.acStorage.hasKey("alarm1")).thenReturn(true);
		when(this.acStorage.getAlarm(1)).thenReturn(alarm1);
		assertThat(this.acl.getAlarm(1), is(alarm1));
		verify(this.acStorage).hasKey("alarm1");
		verify(this.acStorage).getAlarm(1);
		reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.getAlarm(3));
		verify(this.acStorage, only()).hasKey("alarm3");
	}

	/**
	 * Tests the getTimer method
	 */
	@Test
	protected void testGetTimer() {
		Timer timer1 = new Timer(1, 15, 20, 15, true);

		// 1
		when(this.acStorage.hasKey("timer1")).thenReturn(true);
		when(this.acStorage.getTimer(1)).thenReturn(timer1);
		assertThat(this.acl.getTimer(1), is(timer1));
		verify(this.acStorage).hasKey("timer1");
		verify(this.acStorage).getTimer(1);
		reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.getTimer(3));
		verify(this.acStorage, only()).hasKey("timer3");
	}

	/**
	 * Tests the getAllAlarms method
	 */
	@Test
	protected void testGetAllAlarms() {
		// no alarms
		when(this.acStorage.getAlarmCounter()).thenReturn(10);
		assertThat(this.acl.getAllAlarms(), is(new Alarm[] {}));
		reset(this.acStorage);

		// some alarms
		Alarm alarm1 = new Alarm(1, 20, 15, true);
		Alarm alarm2 = new Alarm(2, 6, 30, false);
		Alarm alarm5 = new Alarm(5, 10, 0, true);
		Alarm[] alarms = new Alarm[] { alarm1, alarm2, alarm5 };
		when(this.acStorage.getAlarmCounter()).thenReturn(10);
		when(this.acStorage.hasKey("alarm1")).thenReturn(true);
		when(this.acStorage.getAlarm(1)).thenReturn(alarm1);
		when(this.acStorage.hasKey("alarm2")).thenReturn(true);
		when(this.acStorage.getAlarm(2)).thenReturn(alarm2);
		when(this.acStorage.hasKey("alarm5")).thenReturn(true);
		when(this.acStorage.getAlarm(5)).thenReturn(alarm5);

		assertThat(this.acl.getAllAlarms(), is(alarms));
		verify(this.acStorage, times(10)).hasKey(ArgumentMatchers.anyString());
	}

	/**
	 * Tests the getAllTimers method
	 */
	@Test
	protected void testGetAllTimers() {
		// no timers
		when(this.acStorage.getTimerCounter()).thenReturn(10);
		assertThat(this.acl.getAllTimers(), is(new Timer[] {}));
		reset(this.acStorage);

		// some timers
		Timer timer1 = new Timer(1, 20, 5, 15, true);
		Timer timer2 = new Timer(2, 0, 6, 30, false);
		Timer timer5 = new Timer(5, 30, 10, 0, true);
		Timer[] alarms = new Timer[] { timer1, timer2, timer5 };
		when(this.acStorage.getTimerCounter()).thenReturn(10);
		when(this.acStorage.hasKey("timer1")).thenReturn(true);
		when(this.acStorage.getTimer(1)).thenReturn(timer1);
		when(this.acStorage.hasKey("timer2")).thenReturn(true);
		when(this.acStorage.getTimer(2)).thenReturn(timer2);
		when(this.acStorage.hasKey("timer5")).thenReturn(true);
		when(this.acStorage.getTimer(5)).thenReturn(timer5);

		assertThat(this.acl.getAllTimers(), is(alarms));
		verify(this.acStorage, times(10)).hasKey(ArgumentMatchers.anyString());
	}

	/**
	 * Tests the editAlarm method
	 */
	@Test
	protected void testEditAlarm() {
		// alarm not found
		assertThrows(NoSuchElementException.class, () -> this.acl.editAlarm(1, new int[] { 15, 20 }));
		reset(this.acStorage);

		// normal case
		Alarm alarm1 = new Alarm(1, 15, 50, true);
		when(this.acStorage.hasKey("alarm1")).thenReturn(true);
		when(this.acStorage.getAlarm(1)).thenReturn(alarm1);

		alarm1.setTime(new int[] { 4, 20 });
		assertThat(this.acl.editAlarm(1, new int[] { 4, 20 }), is(alarm1));
		verify(this.acStorage).hasKey("alarm1");
		verify(this.acStorage).getAlarm(1);
		verify(this.acStorage).storeAlarm(alarm1);
	}
}
