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
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * @author Patrick Gebhardt
 */
@ExtendWith(FrameworkExtension.class)
public class TimerBeepServiceTest {

	@Reference
	private TestFramework framework;

	private TimerBeepService tbs;

	private Set<Integer> timers = new HashSet<>();

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.tbs = this.framework.mockService(TimerBeepService.class);
	}

	/**
	 * Tests beep(timer)
	 */
	@Test
	public void beepTimerTest() {
		LocalDateTime timerTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Timer timer = new Timer(1, timerTime, null, true);
		when(this.tbs.beep(timer)).thenReturn(this.timers);
		this.timers.add(timer.getId());
		this.tbs.beep(timer);
		verify(this.tbs).beep(timer);
		assertThat(this.tbs.beep(timer).size(), is(1));
	}

	/**
	 * Tests stopBeep(timer)
	 */
	@Test
	public void stopBeepAlarmTest() {
		LocalDateTime timerTime = LocalDateTime.of(2018, 8, 15, 11, 11);
		Timer timer = new Timer(1, timerTime, null, true);
		when(this.tbs.stopBeep(timer)).thenReturn(this.timers);
		this.tbs.stopBeep(timer);
		verify(this.tbs).stopBeep(timer);
		assertThat(this.tbs.stopBeep(timer).size(), is(0));
	}

}
