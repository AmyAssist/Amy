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
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;

/**
 * Class to test the Timer class
 * 
 * @author Patrick Singer
 */
@ExtendWith(FrameworkExtension.class)
public class TimerTest {

	@Test
	public void timerTest() {
		LocalDateTime timerTime = LocalDateTime.of(2018, 10, 11, 12, 11, 10);
		Timer t1 = new Timer(1, timerTime);
		assertEquals(1, t1.getId());

		LocalDateTime testTime = LocalDateTime.of(2018, 10, 11, 12, 11, 10);
		assertThat(t1.getTimerTime(), is(equalTo(testTime)));
	}

}
