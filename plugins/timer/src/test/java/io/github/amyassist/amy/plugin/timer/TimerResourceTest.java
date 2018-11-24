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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * test for the rest resource of the timer
 * 
 * @author Patrick Gebhardt, Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
class TimerResourceTest {

	@Reference
	private TestFramework framework;

	private TimerLogic tLogic;

	private WebTarget target;

	private TimerRegistry timerStorage;

	private List<Timer> timers = new ArrayList<>();

	private Response r;

	/**
	 * setup server and client for requests and responses
	 */
	@BeforeEach
	public void setUp() {
		this.tLogic = this.framework.mockService(TimerLogic.class);
		this.target = this.framework.setRESTResource(TimerResource.class);
		this.timerStorage = this.framework.mockService(TimerRegistry.class);

		when(this.timerStorage.getAll()).thenReturn(this.timers);
	}

	/**
	 * Test method to get all timers
	 */
	@Test
	void testGetAllTimers() {
		List<Timer> returnedTimers = createTimers(3, true);
		when(this.tLogic.getAllTimers()).thenReturn(returnedTimers);
		this.r = this.target.path("timers").request().get();
		assertThat(returnedTimers.size(), is(3));
		assertEquals(200, this.r.getStatus());

	}

	private List<Timer> createTimers(int amount, boolean active) {
		for (int i = 1; i <= amount; i++) {
			Timer mockTimer = new Timer(i, LocalDateTime.of(2018, 9, 21, 21, 21), null, active);
			this.timers.add(mockTimer);
		}
		return this.timers;
	}

	/**
	 * Test method to get one timer
	 * 
	 */
	@Test
	void testGetTimer() {
		List<Timer> returnedTimers = createTimers(3, true);
		when(this.tLogic.getTimer(2)).thenReturn(returnedTimers.get(1));
		when(this.tLogic.getTimer(5)).thenThrow(new NoSuchElementException());

		this.r = this.target.path("timers/0").request().get();
		assertEquals(500, this.r.getStatus());

		this.r = this.target.path("timers/2").request().get();
		assertEquals(200, this.r.getStatus());
		assertEquals(this.tLogic.getTimer(2).getId(), 2);

		this.r = this.target.path("timers/5").request().get();
		assertEquals(404, this.r.getStatus());
		assertTrue(this.r.readEntity(String.class).startsWith("there is no timer5"));
	}

	/**
	 * Test method to delete all Timers
	 */
	@Test
	void testDeleteAllTimers() {
		this.r = this.target.path("timers/deleteAll").request().post(null);
		assertEquals(204, this.r.getStatus());
		verify(this.tLogic).deleteAllTimers();
	}

	/**
	 * Test method for new Timers
	 */
	@Test
	void testNewTimer() {
		LocalDateTime newTimerTime = LocalDateTime.of(2018, 9, 21, 21, 21);
		Timer newTimer = new Timer(1, newTimerTime, null, true);
		when(this.tLogic.setTimer(newTimerTime)).thenReturn(newTimer);
		Entity<Timer> entity = Entity.entity(newTimer, MediaType.APPLICATION_JSON);

		this.r = this.target.path("timers/new").request().post(entity);

		Timer timerRead = this.r.readEntity(Timer.class);
		assertEquals(200, this.r.getStatus());
		assertEquals(newTimer.getId(), timerRead.getId());
		assertEquals(newTimer.getTimerTime(), timerRead.getTimerTime());
		// time.now() is not testable
		assertEquals(newTimer.isActive(), timerRead.isActive());
	}

	/**
	 * Test method to reactivate a paused timer
	 */
	@Test
	void testReactivateTimer() {
		List<Timer> returnedTimers = createTimers(3, false);
		when(this.tLogic.getTimer(1)).thenReturn(returnedTimers.get(0));
		Entity<Timer> entity = Entity.entity(returnedTimers.get(0), MediaType.APPLICATION_JSON);

		this.r = this.target.path("timers/de.activate/1").request().post(entity);
		assertEquals(204, this.r.getStatus());
		verify(this.tLogic).reactivateTimer(any());
	}

	/**
	 * Test method to pause a running timer
	 */
	@Test
	void testPauseTimer() {
		List<Timer> returnedTimers = createTimers(3, true);
		when(this.tLogic.getTimer(1)).thenReturn(returnedTimers.get(0));
		Entity<Timer> entity = Entity.entity(returnedTimers.get(0), MediaType.APPLICATION_JSON);

		this.r = this.target.path("timers/de.activate/1").request().post(entity);
		assertEquals(204, this.r.getStatus());
		verify(this.tLogic).pauseTimer(any());
	}

	/**
	 * Test method to delete a timer
	 */
	@Test
	void testDeleteTimer() {
		List<Timer> returnedTimers = createTimers(3, true);
		when(this.tLogic.getTimer(1)).thenReturn(returnedTimers.get(0));
		Entity<Timer> entity = Entity.entity(returnedTimers.get(0), MediaType.APPLICATION_JSON);

		this.r = this.target.path("timers/delete/1").request().post(entity);
		assertEquals(204, this.r.getStatus());
		verify(this.tLogic).deleteTimer(1);
	}

}
