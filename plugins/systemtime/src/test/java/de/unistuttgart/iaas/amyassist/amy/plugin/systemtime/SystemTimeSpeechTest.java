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

import javax.annotation.meta.When;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.verification.NoMoreInteractions;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * test class for system Time speech
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class SystemTimeSpeechTest {

	@Reference
	private TestFramework testFramework;
	
	private SystemTimeLogic logic;
	private SystemTimeSpeech speech;
	
	@BeforeEach
	void init() {
		this.logic = this.testFramework.mockService(SystemTimeLogic.class);
		this.speech = this.testFramework.setServiceUnderTest(SystemTimeSpeech.class);
	}
	
	@Test
	void testTime() {
		when(this.logic.getTime()).thenReturn("20:20");
		this.speech.time(new HashMap<>());
		verify(this.logic, times(3)).getTime();
		when(this.logic.getTime()).thenReturn(null);
		this.speech.time(new HashMap<>());
		verify(this.logic, times(5)).getTime();
	}
	
	@Test
	void testDate() {
		this.speech.date(new HashMap<>());
		verify(this.logic).getDate();
	}
	
	@Test
	void testYear() {
		this.speech.year(new HashMap<>());
		verify(this.logic).getYear();
	}
}
