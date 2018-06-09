/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.util.Date;

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
 * Test class for the AlarmClock plug-in
 * 
 * @author Patrick Singer
 */
@ExtendWith(FrameworkExtention.class)
public class AlarmClockTest {
	@Reference
	private TestFramework framework;

	private AlarmClockLogic acl;

	private TaskSchedulerAPI mockService;

	/**
	 * 
	 */
	@BeforeEach
	public void setup() {
		this.mockService = this.framework.mockService(TaskSchedulerAPI.class);
		this.acl = this.framework.setServiceUnderTest(AlarmClockLogic.class);
	}

	/**
	 * Tests the setAlarm method
	 */
	@Test
	public void testSetAlarm() {
		this.acl.setAlarm(new String[] { "15", "20" });

		Mockito.verify(this.mockService).schedule(ArgumentMatchers.any(Runnable.class),
				ArgumentMatchers.any(Date.class));
	}

	/**
	 * Tests the setTimer method
	 */
	@Test
	public void testSetTimer() {
		this.acl.setAlarm(2);
		Mockito.verify(this.mockService).schedule(ArgumentMatchers.any(Runnable.class),
				ArgumentMatchers.any(Date.class));
	}

	/**
	 * Tests the resetAlarms method
	 */
	@Test
	public void testResetAlarms() {
		this.acl.setAlarm(new String[] { "12", "25" });
		this.acl.setAlarm(new String[] { "5", "45" });
		this.acl.setAlarm(new String[] { "20", "00" });

		this.acl.resetTimers();

	}

	/**
	 * Tests the resetTimers method
	 */
	@Test
	protected void testResetTimers() {

	}

	/**
	 * Tests the deleteAlarm method
	 */
	@Test
	protected void testDeleteAlarm() {

	}

	/**
	 * Tests the deactivateAlarm method
	 */
	@Test
	protected void testDeactivateAlarm() {

	}

	/**
	 * Tests the getAlarm method
	 */
	@Test
	protected void testGetAlarm() {

	}

	/**
	 * Tests the getTimer method
	 */
	@Test
	protected void testGetTimer() {

	}

	/**
	 * Tests the getAllAlarms method
	 */
	@Test
	protected void testGetAllAlarms() {

	}

	/**
	 * Tests the editAlarm method
	 */
	@Test
	protected void testEditAlarm() {

	}
}
