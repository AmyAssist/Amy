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

import java.util.Date;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for the AlarmClockLogic class
 * 
 * @author Patrick Singer
 */
@ExtendWith(FrameworkExtention.class)
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
		Mockito.when(this.acStorage.incrementAlarmCounter()).thenReturn(1);
		this.acl.setAlarm(new int[] { 0, 0 });
		Mockito.verify(this.acStorage).incrementAlarmCounter();
		Mockito.verify(this.acStorage).storeAlarm(ArgumentMatchers.any(Alarm.class));
		Mockito.verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
		Mockito.reset(this.acStorage);
		Mockito.reset(this.scheduler);

		// 2
		Mockito.when(this.acStorage.incrementAlarmCounter()).thenReturn(1);
		this.acl.setAlarm(new int[] { 23, 59 });
		Mockito.verify(this.acStorage).incrementAlarmCounter();
		Mockito.verify(this.acStorage).storeAlarm(ArgumentMatchers.any(Alarm.class));
		Mockito.verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
	}

	/**
	 * Tests the setTimer method
	 */
	@Test
	public void testSetTimer() {

		// 1
		Mockito.when(this.acStorage.incrementTimerCounter()).thenReturn(1);
		this.acl.setTimer(0, 2, 0);
		Mockito.verify(this.acStorage).incrementTimerCounter();
		Mockito.verify(this.acStorage).storeTimer(ArgumentMatchers.any(Timer.class));
		Mockito.verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
		Mockito.reset(this.acStorage);
		Mockito.reset(this.scheduler);

		// 2
		Mockito.when(this.acStorage.incrementTimerCounter()).thenReturn(1);
		this.acl.setTimer(12, 35, 40);
		Mockito.verify(this.acStorage).incrementTimerCounter();
		Mockito.verify(this.acStorage).storeTimer(ArgumentMatchers.any(Timer.class));
		Mockito.verify(this.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));

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
		Mockito.verify(this.acStorage, Mockito.never()).putAlarmCounter(0);
		Mockito.verify(this.acStorage, Mockito.never()).deleteKey(ArgumentMatchers.anyString());

		// 1
		Mockito.when(this.acStorage.getAlarmCounter()).thenReturn(10);
		Mockito.when(this.acStorage.hasKey("alarm4")).thenReturn(true);
		Mockito.when(this.acStorage.hasKey("alarm8")).thenReturn(true);

		assertThat(this.acl.resetAlarms(), is("2 alarms deleted"));
		Mockito.verify(this.acStorage).putAlarmCounter(0);
		Mockito.verify(this.acStorage, Mockito.times(10)).hasKey(ArgumentMatchers.anyString());
		Mockito.verify(this.acStorage).deleteKey("alarm4");
		Mockito.verify(this.acStorage).deleteKey("alarm8");
	}

	/**
	 * Tests the resetTimers method
	 */
	@Test
	protected void testResetTimers() {
		// no timers
		assertThat(this.acl.resetTimers(), is("No timers found"));
		Mockito.verify(this.acStorage, Mockito.never()).putTimerCounter(0);
		Mockito.verify(this.acStorage, Mockito.never()).deleteKey(ArgumentMatchers.anyString());

		// 1
		Mockito.when(this.acStorage.getTimerCounter()).thenReturn(10);
		Mockito.when(this.acStorage.hasKey("timer2")).thenReturn(true);
		Mockito.when(this.acStorage.hasKey("timer6")).thenReturn(true);

		assertThat(this.acl.resetTimers(), is("2 timers deleted"));
		Mockito.verify(this.acStorage).putTimerCounter(0);
		Mockito.verify(this.acStorage, Mockito.times(10)).hasKey(ArgumentMatchers.anyString());
		Mockito.verify(this.acStorage).deleteKey("timer2");
		Mockito.verify(this.acStorage).deleteKey("timer6");
	}

	//
	/**
	 * Tests the deleteAlarm method
	 */
	@Test
	protected void testDeleteAlarm() {
		// 1
		Mockito.when(this.acStorage.hasKey("alarm2")).thenReturn(true);
		assertThat(this.acl.deleteAlarm(2), is("Alarm 2 deleted"));
		Mockito.verify(this.acStorage).hasKey("alarm2");
		Mockito.verify(this.acStorage).deleteKey("alarm2");

		// 2
		assertThrows(NoSuchElementException.class, () -> this.acl.deleteAlarm(4));
		Mockito.verify(this.acStorage).hasKey("alarm4");
		Mockito.verify(this.acStorage, Mockito.never()).deleteKey("alarm4");
	}

	/**
	 * Tests the deleteTimer method
	 */
	@Test
	protected void testDeleteTimer() {
		// 1
		Mockito.when(this.acStorage.hasKey("timer2")).thenReturn(true);
		assertThat(this.acl.deleteTimer(2), is("Timer 2 deleted"));
		Mockito.verify(this.acStorage).hasKey("timer2");
		Mockito.verify(this.acStorage).deleteKey("timer2");

		// 2
		assertThrows(NoSuchElementException.class, () -> this.acl.deleteTimer(4));
		Mockito.verify(this.acStorage).hasKey("timer4");
		Mockito.verify(this.acStorage, Mockito.never()).deleteKey("timer4");
	}

	/**
	 * Tests the deactivateAlarm method
	 */
	@Test
	protected void testDeactivateAlarm() {
		Alarm alarm2 = new Alarm(2, 12, 25, true);
		Alarm alarm8 = new Alarm(8, 22, 57, false);

		// active
		Mockito.when(this.acStorage.hasKey("alarm2")).thenReturn(true);
		Mockito.when(this.acStorage.getAlarm(2)).thenReturn(alarm2);

		assertThat(this.acl.deactivateAlarm(2), is("Alarm 2 deactivated"));
		Mockito.verify(this.acStorage).hasKey("alarm2");
		Mockito.verify(this.acStorage).getAlarm(2);
		alarm2.setActive(false);
		Mockito.verify(this.acStorage).storeAlarm(alarm2);
		Mockito.reset(this.acStorage);

		// inactive
		Mockito.when(this.acStorage.hasKey("alarm8")).thenReturn(true);
		Mockito.when(this.acStorage.getAlarm(8)).thenReturn(alarm8);

		assertThat(this.acl.deactivateAlarm(8), is("Alarm 8 is already inactive"));
		Mockito.verify(this.acStorage).hasKey("alarm8");
		Mockito.verify(this.acStorage).getAlarm(8);
		Mockito.verify(this.acStorage, Mockito.never()).storeAlarm(ArgumentMatchers.any(Alarm.class));
		Mockito.reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateAlarm(10));
		Mockito.verify(this.acStorage, Mockito.only()).hasKey("alarm10");
	}

	/**
	 * Tests the deactivateTimer method
	 */
	@Test
	protected void testDeactivateTimer() {
		Timer timer2 = new Timer(2, 12, 0, 0, true);
		Timer timer8 = new Timer(8, 22, 57, 44, false);

		// active
		Mockito.when(this.acStorage.hasKey("timer2")).thenReturn(true);
		Mockito.when(this.acStorage.getTimer(2)).thenReturn(timer2);

		assertThat(this.acl.deactivateTimer(2), is("Timer 2 deactivated"));
		Mockito.verify(this.acStorage).hasKey("timer2");
		Mockito.verify(this.acStorage).getTimer(2);
		timer2.setActive(false);
		Mockito.verify(this.acStorage).storeTimer(timer2);
		Mockito.reset(this.acStorage);

		// inactive
		Mockito.when(this.acStorage.hasKey("timer8")).thenReturn(true);
		Mockito.when(this.acStorage.getTimer(8)).thenReturn(timer8);

		assertThat(this.acl.deactivateTimer(8), is("Timer 8 is already inactive"));
		Mockito.verify(this.acStorage).hasKey("timer8");
		Mockito.verify(this.acStorage).getTimer(8);
		Mockito.verify(this.acStorage, Mockito.never()).storeTimer(ArgumentMatchers.any(Timer.class));
		Mockito.reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateTimer(10));
		Mockito.verify(this.acStorage, Mockito.only()).hasKey("timer10");
	}

	/**
	 * Tests the activateAlarm method
	 */
	@Test
	protected void testActivateAlarm() {
		Alarm alarm2 = new Alarm(2, 12, 25, true);
		Alarm alarm8 = new Alarm(8, 22, 57, false);

		// active
		Mockito.when(this.acStorage.hasKey("alarm2")).thenReturn(true);
		Mockito.when(this.acStorage.getAlarm(2)).thenReturn(alarm2);

		assertThat(this.acl.activateAlarm(2), is("Alarm 2 is already active"));
		Mockito.verify(this.acStorage).hasKey("alarm2");
		Mockito.verify(this.acStorage).getAlarm(2);
		Mockito.verify(this.acStorage, Mockito.never()).storeAlarm(ArgumentMatchers.any(Alarm.class));
		Mockito.reset(this.acStorage);

		// inactive
		Mockito.when(this.acStorage.hasKey("alarm8")).thenReturn(true);
		Mockito.when(this.acStorage.getAlarm(8)).thenReturn(alarm8);

		assertThat(this.acl.activateAlarm(8), is("Alarm 8 activated"));
		Mockito.verify(this.acStorage).hasKey("alarm8");
		Mockito.verify(this.acStorage).getAlarm(8);
		alarm2.setActive(false);
		Mockito.verify(this.acStorage).storeAlarm(alarm8);
		Mockito.reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.activateAlarm(10));
		Mockito.verify(this.acStorage, Mockito.only()).hasKey("alarm10");
	}

	/**
	 * Tests the activateTimer method
	 */
	@Test
	protected void testActivateTimer() {
		Timer timer2 = new Timer(2, 12, 0, 0, true);
		Timer timer8 = new Timer(8, 22, 57, 44, false);

		// active
		Mockito.when(this.acStorage.hasKey("timer2")).thenReturn(true);
		Mockito.when(this.acStorage.getTimer(2)).thenReturn(timer2);

		assertThat(this.acl.activateTimer(2), is("Timer 2 is already active"));
		Mockito.verify(this.acStorage).hasKey("timer2");
		Mockito.verify(this.acStorage).getTimer(2);
		Mockito.verify(this.acStorage, Mockito.never()).storeTimer(ArgumentMatchers.any(Timer.class));
		Mockito.reset(this.acStorage);

		// inactive
		Mockito.when(this.acStorage.hasKey("timer8")).thenReturn(true);
		Mockito.when(this.acStorage.getTimer(8)).thenReturn(timer8);

		assertThat(this.acl.activateTimer(8), is("Timer 8 activated"));
		Mockito.verify(this.acStorage).hasKey("timer8");
		Mockito.verify(this.acStorage).getTimer(8);
		timer2.setActive(false);
		Mockito.verify(this.acStorage).storeTimer(timer8);
		Mockito.reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.deactivateTimer(10));
		Mockito.verify(this.acStorage, Mockito.only()).hasKey("timer10");
	}

	/**
	 * Tests the getAlarm method
	 */
	@Test
	protected void testGetAlarm() {
		Alarm alarm1 = new Alarm(1, 20, 15, true);

		// 1
		Mockito.when(this.acStorage.hasKey("alarm1")).thenReturn(true);
		Mockito.when(this.acStorage.getAlarm(1)).thenReturn(alarm1);
		assertThat(this.acl.getAlarm(1), is(alarm1));
		Mockito.verify(this.acStorage).hasKey("alarm1");
		Mockito.verify(this.acStorage).getAlarm(1);
		Mockito.reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.getAlarm(3));
		Mockito.verify(this.acStorage, Mockito.only()).hasKey("alarm3");
	}

	/**
	 * Tests the getTimer method
	 */
	@Test
	protected void testGetTimer() {
		Timer timer1 = new Timer(1, 15, 20, 15, true);

		// 1
		Mockito.when(this.acStorage.hasKey("timer1")).thenReturn(true);
		Mockito.when(this.acStorage.getTimer(1)).thenReturn(timer1);
		assertThat(this.acl.getTimer(1), is(timer1));
		Mockito.verify(this.acStorage).hasKey("timer1");
		Mockito.verify(this.acStorage).getTimer(1);
		Mockito.reset(this.acStorage);

		// not found
		assertThrows(NoSuchElementException.class, () -> this.acl.getTimer(3));
		Mockito.verify(this.acStorage, Mockito.only()).hasKey("timer3");
	}

	/**
	 * Tests the getAllAlarms method
	 */
	@Test
	protected void testGetAllAlarms() {
		// no alarms
		Mockito.when(this.acStorage.getAlarmCounter()).thenReturn(10);
		assertThat(this.acl.getAllAlarms(), is(new Alarm[] {}));
		Mockito.reset(this.acStorage);

		// some alarms
		Alarm alarm1 = new Alarm(1, 20, 15, true);
		Alarm alarm2 = new Alarm(2, 6, 30, false);
		Alarm alarm5 = new Alarm(5, 10, 0, true);
		Alarm[] alarms = new Alarm[] { alarm1, alarm2, alarm5 };
		Mockito.when(this.acStorage.getAlarmCounter()).thenReturn(10);
		Mockito.when(this.acStorage.hasKey("alarm1")).thenReturn(true);
		Mockito.when(this.acStorage.getAlarm(1)).thenReturn(alarm1);
		Mockito.when(this.acStorage.hasKey("alarm2")).thenReturn(true);
		Mockito.when(this.acStorage.getAlarm(2)).thenReturn(alarm2);
		Mockito.when(this.acStorage.hasKey("alarm5")).thenReturn(true);
		Mockito.when(this.acStorage.getAlarm(5)).thenReturn(alarm5);

		assertThat(this.acl.getAllAlarms(), is(alarms));
		Mockito.verify(this.acStorage, Mockito.times(10)).hasKey(ArgumentMatchers.anyString());
	}

	/**
	 * Tests the getAllTimers method
	 */
	@Test
	protected void testGetAllTimers() {
		// no timers
		Mockito.when(this.acStorage.getTimerCounter()).thenReturn(10);
		assertThat(this.acl.getAllTimers(), is(new Timer[] {}));
		Mockito.reset(this.acStorage);

		// some timers
		Timer timer1 = new Timer(1, 20, 5, 15, true);
		Timer timer2 = new Timer(2, 0, 6, 30, false);
		Timer timer5 = new Timer(5, 30, 10, 0, true);
		Timer[] alarms = new Timer[] { timer1, timer2, timer5 };
		Mockito.when(this.acStorage.getTimerCounter()).thenReturn(10);
		Mockito.when(this.acStorage.hasKey("timer1")).thenReturn(true);
		Mockito.when(this.acStorage.getTimer(1)).thenReturn(timer1);
		Mockito.when(this.acStorage.hasKey("timer2")).thenReturn(true);
		Mockito.when(this.acStorage.getTimer(2)).thenReturn(timer2);
		Mockito.when(this.acStorage.hasKey("timer5")).thenReturn(true);
		Mockito.when(this.acStorage.getTimer(5)).thenReturn(timer5);

		assertThat(this.acl.getAllTimers(), is(alarms));
		Mockito.verify(this.acStorage, Mockito.times(10)).hasKey(ArgumentMatchers.anyString());
	}

	/**
	 * Tests the editAlarm method
	 */
	@Test
	protected void testEditAlarm() {
		// alarm not found
		assertThrows(NoSuchElementException.class, () -> this.acl.editAlarm(1, new int[] { 15, 20 }));
		Mockito.reset(this.acStorage);

		// normal case
		Alarm alarm1 = new Alarm(1, 15, 50, true);
		Mockito.when(this.acStorage.hasKey("alarm1")).thenReturn(true);
		Mockito.when(this.acStorage.getAlarm(1)).thenReturn(alarm1);

		alarm1.setTime(new int[] { 4, 20 });
		assertThat(this.acl.editAlarm(1, new int[] { 4, 20 }), is(alarm1));
		Mockito.verify(this.acStorage).hasKey("alarm1");
		Mockito.verify(this.acStorage).getAlarm(1);
		Mockito.verify(this.acStorage).storeAlarm(alarm1);
	}
}
