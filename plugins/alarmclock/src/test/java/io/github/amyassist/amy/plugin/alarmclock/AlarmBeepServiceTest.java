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
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * @author Patrick Gebhardt
 */
@ExtendWith(FrameworkExtension.class)
public class AlarmBeepServiceTest {

	@Reference
	private TestFramework framework;

	private AlarmBeepService abs;

	private Set<Integer> alarms = new HashSet<>();

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.abs = this.framework.mockService(AlarmBeepService.class);
	}

	/**
	 * Tests beep(Alarm)
	 */
	@Test
	public void beepAlarmTest() {
		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Alarm alarm = new Alarm(1, alarmTime, true);
		when(this.abs.beep(alarm)).thenReturn(this.alarms);
		this.alarms.add(alarm.getId());
		this.abs.beep(alarm);
		verify(this.abs).beep(alarm);
		assertThat(this.abs.beep(alarm).size(), is(1));
	}

	/**
	 * Tests stopBeep(alarm)
	 */
	@Test
	public void stopBeepAlarmTest() {
		LocalDateTime alarmTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Alarm alarm = new Alarm(1, alarmTime, true);
		when(this.abs.stopBeep(alarm)).thenReturn(this.alarms);
		this.abs.stopBeep(alarm);
		verify(this.abs).stopBeep(alarm);
		assertThat(this.abs.stopBeep(alarm).size(), is(0));
	}

}
