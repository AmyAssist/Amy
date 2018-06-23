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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for the AlarmClock plug-in
 * 
 * @author Patrick Singer
 */
@ExtendWith(FrameworkExtension.class)
public class AlarmClockTest {
	@Reference
	private TestFramework framework;

	private AlarmClockLogic acl;

	private TaskSchedulerAPI mockService;

	private IStorage storage;

	/**
	 * 
	 */
	@BeforeEach
	public void setup() {
		this.mockService = this.framework.mockService(TaskSchedulerAPI.class);
		this.acl = this.framework.setServiceUnderTest(AlarmClockLogic.class);
		this.storage = this.framework.storage();
	}

	/**
	 * Tests the setAlarm method
	 */
	@Test
	public void testSetAlarm() {
		// 1
		assertEquals("Alarm 1 set for 00:00", this.acl.setAlarm(new String[] { "00", "00" }));
		Mockito.verify(this.storage).put(AlarmClockLogic.ALARMCOUNTER, "1");
		Mockito.verify(this.storage).put("alarm1", "00;00;true");
		Mockito.verify(this.mockService).schedule(ArgumentMatchers.any(Runnable.class),
				ArgumentMatchers.any(Date.class));
		Mockito.reset(this.storage);
		Mockito.reset(this.mockService);

		// 2
		assertEquals("Alarm 2 set for 23:59", this.acl.setAlarm(new String[] { "23", "59" }));
		Mockito.verify(this.storage).put(AlarmClockLogic.ALARMCOUNTER, "2");
		Mockito.verify(this.storage).put("alarm2", "23;59;true");
		Mockito.verify(this.mockService).schedule(ArgumentMatchers.any(Runnable.class),
				ArgumentMatchers.any(Date.class));

		// storage values
		assertEquals("2", this.storage.get(AlarmClockLogic.ALARMCOUNTER));
		assertEquals("00;00;true", this.storage.get("alarm1"));
		assertEquals("23;59;true", this.storage.get("alarm2"));
	}

	/**
	 * Tests the setTimer method
	 */
	@Test
	public void testSetTimer() {
		// 1
		assertEquals("Timer 1 set on 2 minutes", this.acl.setAlarm(2));
		Mockito.verify(this.storage).put(AlarmClockLogic.TIMERCOUNTER, "1");
		Mockito.verify(this.storage).put("timer1", "2;true");
		Mockito.verify(this.mockService).schedule(ArgumentMatchers.any(Runnable.class),
				ArgumentMatchers.any(Date.class));
		Mockito.reset(this.storage);
		Mockito.reset(this.mockService);

		// 2
		assertEquals("No valid delay", this.acl.setAlarm(0));
		Mockito.verify(this.storage, Mockito.never()).put(AlarmClockLogic.TIMERCOUNTER, "2");
		Mockito.verify(this.storage, Mockito.never()).put("timer2", "0;true");
		Mockito.verify(this.mockService, Mockito.never()).schedule(ArgumentMatchers.any(Runnable.class),
				ArgumentMatchers.any(Date.class));

		// storage values
		assertEquals("1", this.storage.get(AlarmClockLogic.TIMERCOUNTER));
		assertEquals("2;true", this.storage.get("timer1"));
	}

	/**
	 * Tests the resetAlarms method
	 */
	@Test
	public void testResetAlarms() {

		// 1
		this.storage.put(AlarmClockLogic.ALARMCOUNTER, "0");
		Mockito.reset(this.storage);
		assertEquals("No alarms found", this.acl.resetAlarms());
		Mockito.verify(this.storage, Mockito.never()).put(AlarmClockLogic.ALARMCOUNTER, "0");
		Mockito.verify(this.storage, Mockito.never()).delete(ArgumentMatchers.anyString());
		assertEquals("0", this.storage.get(AlarmClockLogic.ALARMCOUNTER));

		// 2
		this.storage.put(AlarmClockLogic.ALARMCOUNTER, "10");
		this.storage.put("alarm4", "15;20;true");
		this.storage.put("alarm8", "14;00;false");
		Mockito.reset(this.storage);

		assertEquals("2 alarms deleted", this.acl.resetAlarms());
		Mockito.verify(this.storage).put(AlarmClockLogic.ALARMCOUNTER, "0");
		Mockito.verify(this.storage).delete("alarm4");
		Mockito.verify(this.storage).delete("alarm8");
		assertEquals("0", this.storage.get(AlarmClockLogic.ALARMCOUNTER));
		Mockito.reset(this.storage);

	}

	/**
	 * Tests the resetTimers method
	 */
	@Test
	protected void testResetTimers() {
		// 1
		this.storage.put(AlarmClockLogic.TIMERCOUNTER, "0");
		Mockito.reset(this.storage);
		assertEquals("No timers found", this.acl.resetTimers());
		Mockito.verify(this.storage, Mockito.never()).put(AlarmClockLogic.TIMERCOUNTER, "0");
		Mockito.verify(this.storage, Mockito.never()).delete(ArgumentMatchers.anyString());
		assertEquals("0", this.storage.get(AlarmClockLogic.TIMERCOUNTER));
		Mockito.reset(this.storage);

		// 2
		this.storage.put(AlarmClockLogic.TIMERCOUNTER, "10");
		this.storage.put("timer4", "10;true");
		this.storage.put("timer8", "5;false");
		Mockito.reset(this.storage);

		assertEquals("2 timers deleted", this.acl.resetTimers());
		Mockito.verify(this.storage).put(AlarmClockLogic.TIMERCOUNTER, "0");
		Mockito.verify(this.storage).delete("timer4");
		Mockito.verify(this.storage).delete("timer8");
		assertEquals("0", this.storage.get(AlarmClockLogic.TIMERCOUNTER));
	}

	/**
	 * Tests the deleteAlarm method
	 */
	@Test
	protected void testDeleteAlarm() {
		this.storage.put("alarm2", "2;5;true");
		this.storage.put("alarm5", "3;3;false");
		Mockito.reset(this.storage);
		// 1
		assertEquals("Alarm 2 deleted", this.acl.deleteAlarm(2));
		Mockito.verify(this.storage).delete("alarm2");

		// 2
		assertEquals("Alarm 4 not found", this.acl.deleteAlarm(4));
		Mockito.verify(this.storage, Mockito.never()).delete("alarm4");
	}

	/**
	 * Tests the deleteTimer method
	 */
	@Test
	protected void testDeleteTimer() {
		this.storage.put("timer2", "5;true");
		this.storage.put("timer5", "3;false");
		Mockito.reset(this.storage);

		// 1
		assertEquals("Timer 2 deleted", this.acl.deleteTimer(2));
		Mockito.verify(this.storage).delete("timer2");

		// 2
		assertEquals("Timer 4 not found", this.acl.deleteTimer(4));
		Mockito.verify(this.storage, Mockito.never()).delete("alarm4");
	}

	/**
	 * Tests the deactivateAlarm method
	 */
	@Test
	protected void testDeactivateAlarm() {
		this.storage.put("alarm2", "12;25;true");
		this.storage.put("alarm8", "22;57;false");
		this.storage.put("alarm9", "1;5");
		Mockito.reset(this.storage);

		// active
		assertEquals("Alarm 2 deactivated", this.acl.deactivateAlarm(2));
		Mockito.verify(this.storage).put("alarm2", "12;25;false");
		assertEquals("12;25;false", this.storage.get("alarm2"));
		Mockito.reset(this.storage);

		// inactive
		assertEquals("Alarm 8 is already inactive", this.acl.deactivateAlarm(8));
		Mockito.verify(this.storage).has("alarm8");
		Mockito.verify(this.storage).get("alarm8");
		assertEquals("22;57;false", this.storage.get("alarm8"));
		Mockito.reset(this.storage);

		// not found
		assertEquals("Alarm 10 not found", this.acl.deactivateAlarm(10));
		Mockito.verify(this.storage, Mockito.only()).has("alarm10");
		Mockito.reset(this.storage);

		// array size problem
		assertEquals("Something went wrong", this.acl.deactivateAlarm(9));
		Mockito.verify(this.storage).has("alarm9");
		Mockito.verify(this.storage).get("alarm9");
	}

	/**
	 * Tests the deactivateTimer method
	 */
	@Test
	protected void testDeactivateTimer() {
		this.storage.put("timer2", "12;true");
		this.storage.put("timer8", "22;false");
		this.storage.put("timer9", "1");
		Mockito.reset(this.storage);

		// active
		assertEquals("Timer 2 deactivated", this.acl.deactivateTimer(2));
		Mockito.verify(this.storage).put("timer2", "12;false");
		assertEquals("12;false", this.storage.get("timer2"));
		Mockito.reset(this.storage);

		// inactive
		assertEquals("Timer 8 is already inactive", this.acl.deactivateTimer(8));
		Mockito.verify(this.storage).has("timer8");
		Mockito.verify(this.storage).get("timer8");
		assertEquals("22;false", this.storage.get("timer8"));
		Mockito.reset(this.storage);

		// not found
		assertEquals("Timer 10 not found", this.acl.deactivateTimer(10));
		Mockito.verify(this.storage, Mockito.only()).has("timer10");
		Mockito.reset(this.storage);

		// array size problem
		assertEquals("Something went wrong", this.acl.deactivateTimer(9));
		Mockito.verify(this.storage).has("timer9");
		Mockito.verify(this.storage).get("timer9");
	}

	/**
	 * Tests the deactivateAlarm method
	 */
	@Test
	protected void testActivateAlarm() {
		this.storage.put("alarm2", "12;25;true");
		this.storage.put("alarm8", "22;57;false");
		this.storage.put("alarm9", "1;5");
		Mockito.reset(this.storage);

		// active
		assertEquals("Alarm 2 is already active", this.acl.activateAlarm(2));
		Mockito.verify(this.storage).has("alarm2");
		Mockito.verify(this.storage).get("alarm2");
		assertEquals("12;25;true", this.storage.get("alarm2"));
		Mockito.reset(this.storage);

		// inactive
		assertEquals("Alarm 8 activated", this.acl.activateAlarm(8));
		Mockito.verify(this.storage).put("alarm8", "22;57;true");
		assertEquals("22;57;true", this.storage.get("alarm8"));
		Mockito.reset(this.storage);

		// not found
		assertEquals("Alarm 10 not found", this.acl.activateAlarm(10));
		Mockito.verify(this.storage, Mockito.only()).has("alarm10");
		Mockito.reset(this.storage);

		// array size problem
		assertEquals("Something went wrong", this.acl.activateAlarm(9));
		Mockito.verify(this.storage).has("alarm9");
		Mockito.verify(this.storage).get("alarm9");
	}

	/**
	 * Tests the deactivateTimer method
	 */
	@Test
	protected void testActivateTimer() {
		this.storage.put("timer2", "12;true");
		this.storage.put("timer8", "22;false");
		this.storage.put("timer9", "1");
		Mockito.reset(this.storage);

		// active
		assertEquals("Timer 2 is already active", this.acl.activateTimer(2));
		Mockito.verify(this.storage).has("timer2");
		Mockito.verify(this.storage).get("timer2");
		assertEquals("12;true", this.storage.get("timer2"));
		Mockito.reset(this.storage);

		// inactive
		assertEquals("Timer 8 activated", this.acl.activateTimer(8));
		Mockito.verify(this.storage).put("timer8", "22;true");
		assertEquals("22;true", this.storage.get("timer8"));
		Mockito.reset(this.storage);

		// not found
		assertEquals("Timer 10 not found", this.acl.activateTimer(10));
		Mockito.verify(this.storage, Mockito.only()).has("timer10");
		Mockito.reset(this.storage);

		// array size problem
		assertEquals("Something went wrong", this.acl.activateTimer(9));
		Mockito.verify(this.storage).has("timer9");
		Mockito.verify(this.storage).get("timer9");
	}

	/**
	 * Tests the getAlarm method
	 */
	@Test
	protected void testGetAlarm() {
		this.storage.put("alarm1", "20;15;true");
		this.storage.put("alarm2", "20;15;false");

		// active
		assertEquals("This alarm is set for 20:15 and active.", this.acl.getAlarm(1));
		Mockito.verify(this.storage).get("alarm1");
		Mockito.reset(this.storage);

		// inactive
		assertEquals("This alarm is set for 20:15 and NOT active.", this.acl.getAlarm(2));
		Mockito.verify(this.storage).get("alarm2");
		Mockito.reset(this.storage);

		// not found
		assertEquals("Alarm not found", this.acl.getAlarm(3));
		Mockito.verify(this.storage, Mockito.only()).has("alarm3");
	}

	/**
	 * Tests the getTimer method
	 */
	@Test
	protected void testGetTimer() {
		this.storage.put("timer1", "2;true");
		this.storage.put("timer2", "5;false");

		// active
		assertEquals("This timer was set on 2 minutes and is active.", this.acl.getTimer(1));
		Mockito.verify(this.storage).get("timer1");
		Mockito.reset(this.storage);

		// inactive
		assertEquals("This timer was set on 5 minutes and is NOT active.", this.acl.getTimer(2));
		Mockito.verify(this.storage).get("timer2");
		Mockito.reset(this.storage);

		// not found
		assertEquals("Timer not found", this.acl.getTimer(3));
		Mockito.verify(this.storage, Mockito.only()).has("timer3");
	}

	/**
	 * Tests the getAllAlarms method
	 */
	@Test
	protected void testGetAllAlarms() {
		// no alarms
		this.storage.put(AlarmClockLogic.ALARMCOUNTER, "20");
		assertArrayEquals(new String[] {}, this.acl.getAllAlarms());

		// some alarms
		this.storage.put(AlarmClockLogic.ALARMCOUNTER, "10");
		this.storage.put("alarm1", "20;15;true");
		this.storage.put("alarm2", "6;30;false");
		this.storage.put("alarm5", "10;00;true");
		String[] alarms = { "1;20;15;true", "2;6;30;false", "5;10;00;true" };
		Mockito.reset(this.storage);

		assertArrayEquals(alarms, this.acl.getAllAlarms());
		Mockito.verify(this.storage, Mockito.times(10)).has(ArgumentMatchers.anyString());

	}

	/**
	 * Tests the getAllTimers method
	 */
	@Test
	protected void testGetAllTimers() {
		// no timers
		this.storage.put(AlarmClockLogic.TIMERCOUNTER, "15");
		assertArrayEquals(new String[] {}, this.acl.getAllTimers());

		// some timers
		this.storage.put(AlarmClockLogic.TIMERCOUNTER, "10");
		this.storage.put("timer1", "2;true");
		this.storage.put("timer2", "5;false");
		this.storage.put("timer3", "10;true");
		String[] timers = { "1: 2;true", "2: 5;false", "3: 10;true" };
		Mockito.reset(this.storage);

		assertArrayEquals(timers, this.acl.getAllTimers());
		Mockito.verify(this.storage, Mockito.times(10)).has(ArgumentMatchers.anyString());

	}

	/**
	 * Tests the editAlarm method
	 */
	@Test
	protected void testEditAlarm() {
		// alarm not found
		assertEquals("Alarm not found", this.acl.editAlarm(5, new String[] { "15", "13" }));

		// normal cases
		this.storage.put(AlarmClockLogic.ALARMCOUNTER, "2");
		this.storage.put("alarm1", "15;50;true");
		this.storage.put("alarm2", "2;2;false");

		assertEquals("Alarm 1 changed to 4:20", this.acl.editAlarm(1, new String[] { "4", "20" }));
		Mockito.verify(this.storage).delete("alarm1");
		assertEquals("4;20;true", this.storage.get("alarm3"));
	}
}
