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

package de.unistuttgart.iaas.amyassist.amy.plugin.calendar;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import javax.annotation.meta.When;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for calendar speech
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
class CalendarSpeechTest {

	@Reference
	private TestFramework testFramework;
	
	private CalendarLogic calendar;
	private CalendarSpeech speech;
	
	@Mock
	private EntityData number;
	@Mock
	private EntityData day;
	
	@BeforeEach
	void init() {
		this.calendar = this.testFramework.mockService(CalendarLogic.class);
		this.speech = this.testFramework.setServiceUnderTest(CalendarSpeech.class);
	}
	
	@Test
	void testGetEvents() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(1);
		map.put("number", number);
		this.speech.getEvents(map);
		verify(this.calendar).getEvents(1);
	}
	
	@Test
	void testGetEventsToday() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.day.getString()).thenReturn("today");
		map.put("day", this.day);
		this.speech.getEventsToday(map);
		verify(this.calendar).getEventsToday();	
	}
	
	@Test
	void testGetEventsTomorrow() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.day.getString()).thenReturn("tomorrow");
		map.put("day", this.day);
		this.speech.getEventsToday(map);
		verify(this.calendar).getEventsTomorrow();
	}

}
