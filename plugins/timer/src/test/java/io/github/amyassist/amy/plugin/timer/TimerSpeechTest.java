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

package io.github.amyassist.amy.plugin.timer;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.natlang.EntityData;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test class for the timer speech class
 * 
 * @author Patrick Gebhardt
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class TimerSpeechTest {

	@Reference
	private TestFramework framework;

	private TimerSpeech tSpeech;

	private TimerLogic tLogic;

	@Mock
	private EntityData hour;

	@Mock
	private EntityData minute;

	@Mock
	private EntityData second;

	@Mock
	private EntityData number;

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void init() {
		this.tLogic = this.framework.mockService(TimerLogic.class);
		this.framework.mockService(TimerRegistry.class);
		this.tSpeech = this.framework.setServiceUnderTest(TimerSpeech.class);
	}

	/**
	 * Tests the setTimer method with valid arguments
	 */
	@Test
	public void setTimerTest() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.hour.getNumber()).thenReturn(1);
		when(this.minute.getNumber()).thenReturn(5);
		when(this.second.getNumber()).thenReturn(30);
		map.put("hour", this.hour);
		map.put("minute", this.minute);
		map.put("second", this.second);
		LocalDateTime timerDate = LocalDateTime.now().plusHours(this.hour.getNumber())
				.plusMinutes(this.minute.getNumber()).plusSeconds(this.second.getNumber())
				.truncatedTo(ChronoUnit.SECONDS);
		Timer t = new Timer(1, timerDate, null, true);
		when(this.tLogic.setTimer(timerDate)).thenReturn(t);
		assertThat(this.tSpeech.setTimer(map), is("Timer 1 set"));
	}

	/**
	 * Tests the setTimer method with invalid arguments
	 */
	@Test
	public void setTimerInvalidTest() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.hour.getNumber()).thenReturn(0);
		when(this.minute.getNumber()).thenReturn(0);
		when(this.second.getNumber()).thenReturn(0);
		map.put("hour", this.hour);
		map.put("minute", this.minute);
		map.put("second", this.second);
		assertThat(this.tSpeech.setTimer(map), is("No value is set"));
	}

	/**
	 * Tests the pauseTimer method
	 */
	@Test
	public void pauseTimerTest() {
		Map<String, EntityData> map = new HashMap<>();
		map.put("number", this.number);
		when(map.get("number").getNumber()).thenReturn(1);
		LocalDateTime timerDate = LocalDateTime.now().plusHours(this.hour.getNumber())
				.plusMinutes(this.minute.getNumber()).plusSeconds(this.second.getNumber())
				.truncatedTo(ChronoUnit.SECONDS);
		Timer t = new Timer(1, timerDate, null, true);
		when(this.tLogic.getTimer(1)).thenReturn(t);
		assertThat(this.tSpeech.pauseTimer(map), is("Timer 1 paused"));
	}

	/**
	 * Tests the reactivateTimer method
	 */
	@Test
	public void reactivateTimerTest() {
		Map<String, EntityData> map = new HashMap<>();
		map.put("number", this.number);
		when(map.get("number").getNumber()).thenReturn(1);
		LocalDateTime timerDate = LocalDateTime.now().plusHours(this.hour.getNumber())
				.plusMinutes(this.minute.getNumber()).plusSeconds(this.second.getNumber())
				.truncatedTo(ChronoUnit.SECONDS);
		Timer t = new Timer(1, timerDate, null, false);
		when(this.tLogic.getTimer(1)).thenReturn(t);
		assertThat(this.tSpeech.reactivateTimer(map), is("Timer 1 resumed"));
	}

	/**
	 * Tests the resetTimerObjects method
	 */
	@Test
	public void resetTimerObjectsTest() {
		Map<String, EntityData> map = new HashMap<>();
		assertThat(this.tSpeech.resetTimerObjects(map), is(this.tLogic.deleteAllTimers()));
	}

	/**
	 * Tests the deleteTimerObject method
	 */
	@Test
	public void deleteTimerObjectTest() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(1);
		map.put("number", this.number);
		assertThat(this.tSpeech.deleteTimerObject(map), is(this.tLogic.deleteTimer(1)));
	}

	/**
	 * Tests the outputTimer method and the getTimerObjectMethod
	 */
	@Test
	public void outputTimerNowTest() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(1);
		map.put("number", this.number);
		LocalDateTime timerDate = LocalDateTime.of(2018, 9, 6, 12, 11, 10);
		Timer t = new Timer(1, timerDate, Duration.ZERO, false);
		when(this.tLogic.getTimer(1)).thenReturn(t);
		assertThat(this.tSpeech.getTimerObject(map), is("Timer 1 is ringing right now."));
	}

	/**
	 * Tests the outputTimer method and the getTimerObjectMethod
	 */
	@Test
	public void outputTimerSecondTest() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(1);
		map.put("number", this.number);
		LocalDateTime timerDate = LocalDateTime.of(2018, 9, 6, 12, 11, 10);
		Timer t = new Timer(1, timerDate, Duration.ofSeconds(10), false);
		when(this.tLogic.getTimer(1)).thenReturn(t);
		assertThat(this.tSpeech.getTimerObject(map), is("Timer 1 will ring in 10 seconds but is paused"));
	}

	/**
	 * Tests the outputTimer method and the getTimerObjectMethod
	 */
	@Test
	public void outputTimerMinuteTest() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(1);
		map.put("number", this.number);
		LocalDateTime timerDate = LocalDateTime.of(2018, 9, 6, 12, 11, 10);
		Timer t = new Timer(1, timerDate, Duration.ofMinutes(10), false);
		when(this.tLogic.getTimer(1)).thenReturn(t);
		assertThat(this.tSpeech.getTimerObject(map), is("Timer 1 will ring in 10 minutes and 0 seconds but is paused"));
	}

	/**
	 * Tests the outputTimer method and the getTimerObjectMethod
	 */
	@Test
	public void outputTimerHourTest() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.number.getNumber()).thenReturn(1);
		map.put("number", this.number);
		LocalDateTime timerDate = LocalDateTime.of(2018, 9, 6, 12, 11, 10);
		Timer t = new Timer(1, timerDate, Duration.ofHours(10), false);
		when(this.tLogic.getTimer(1)).thenReturn(t);
		assertThat(this.tSpeech.getTimerObject(map),
				is("Timer 1 will ring in 10 hours and 0 minutes and 0 seconds but is paused"));
	}

}
