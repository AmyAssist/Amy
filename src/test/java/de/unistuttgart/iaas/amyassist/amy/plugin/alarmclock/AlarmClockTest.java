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

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;

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

	@BeforeEach
	public void setup() {
		this.mockService = this.framework.mockService(TaskSchedulerAPI.class);
		this.acl = this.framework.setServiceUnderTest(AlarmClockLogic.class);
		this.acl.init(null);
	}

	/**
	 * Tests if the keywords are correct
	 */
	@Test
	public void testKeywords() {

	}

	/**
	 * Tests the setAlarms method
	 */
	@Test
	public void testSet() {
		this.acl.setAlarm("15:20");

		Mockito.verify(this.mockService).schedule(
				ArgumentMatchers.any(Runnable.class),
				ArgumentMatchers.any(Date.class));
	}

	@Test
	public void testGetAll() {

	}

}
