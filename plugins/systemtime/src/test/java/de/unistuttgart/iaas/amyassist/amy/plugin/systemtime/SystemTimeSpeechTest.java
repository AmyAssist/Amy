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

package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * test class for system Time speech
 *
 * @author Lars Buttgereit, Florian Bauer
 */
@ExtendWith(FrameworkExtension.class)
public class SystemTimeSpeechTest {

	@Reference
	private TestFramework testFramework;

	private SystemTimeLogic logic;
	private SystemTimeSpeech speech;

	/**
	 * initialize the test
	 */
	@BeforeEach
	void init() {
		this.logic = this.testFramework.mockService(SystemTimeLogic.class);
		this.speech = this.testFramework.setServiceUnderTest(SystemTimeSpeech.class);
	}

	/**
	 * Test time(Map<String, EntityData>)
	 */
	@Test
	void testTime() {
		when(this.logic.getTimeStamp()).thenReturn(LocalDateTime.of(2018, 8, 20, 20, 15, 28));
		assertThat(this.speech.time(new HashMap<>()), equalToIgnoringWhiteSpace("It is 20:15."));
	}

	/**
	 * Test date(Map<String, EntityData>)
	 */
	@Test
	void testDate() {
		when(this.logic.getTimeStamp()).thenReturn(LocalDateTime.of(2018, 8, 20, 20, 15, 28));
		assertThat(this.speech.date(new HashMap<>()), equalToIgnoringWhiteSpace("It is the 20th of august."));
	}

	/**
	 * Test year(Map<String, EntityData>)
	 */
	@Test
	void testYear() {
		when(this.logic.getTimeStamp()).thenReturn(LocalDateTime.of(2018, 8, 20, 20, 15, 28));
		assertThat(this.speech.year(new HashMap<>()), equalToIgnoringWhiteSpace("It is 2018."));
	}

	/**
	 * Test ordinal(int)
	 */
	@Test
	void testOrdinal() {
		assertThat(SystemTimeSpeech.ordinal(1), equalToIgnoringWhiteSpace("1st"));
		assertThat(SystemTimeSpeech.ordinal(11), equalToIgnoringWhiteSpace("11th"));
		assertThat(SystemTimeSpeech.ordinal(21), equalToIgnoringWhiteSpace("21st"));
	}
}
