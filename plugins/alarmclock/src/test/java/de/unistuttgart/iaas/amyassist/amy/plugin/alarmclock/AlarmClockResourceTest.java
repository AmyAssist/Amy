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

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
 * test for the rest resource of the alarmclock
 * 
 * @author Christian Bräuner, Patrick Gebhardt
 */
@ExtendWith(FrameworkExtension.class)
class AlarmClockResourceTest {

	@Reference
	private TestFramework framework;

	private AlarmClockLogic acl;

	private WebTarget target;

	private AlarmRegistry alarmStorage;

	private List<Alarm> alarms = new ArrayList<>();

	private Response r;

	/**
	 * setup server and client for requests and responses
	 */
	@BeforeEach
	public void setUp() {
		this.acl = this.framework.mockService(AlarmClockLogic.class);
		this.target = this.framework.setRESTResource(AlarmClockResource.class);
		this.alarmStorage = this.framework.mockService(AlarmRegistry.class);

		when(this.alarmStorage.getAll()).thenReturn(this.alarms);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#getAllAlarms()}.
	 */
	@Test
	void testGetAllAlarms() {
		List<Alarm> returnedAlarms = createAlarms(3, true);
		when(this.acl.getAllAlarms()).thenReturn(returnedAlarms);
		this.r = this.target.path("alarms").request().get();
		assertThat(returnedAlarms.size(), is(3));
		assertEquals(200, this.r.getStatus());

	}

	private List<Alarm> createAlarms(int amount, boolean active) {
		for (int i = 1; i <= amount; i++) {
			Alarm mockAlarm = new Alarm(i, LocalDateTime.of(2018, 8, 21, 21, 21), active);
			this.alarms.add(mockAlarm);
		}
		return this.alarms;
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#getAlarm(int)}.
	 * 
	 */
	@Test
	void testGetAlarm() {
		List<Alarm> returnedAlarms = createAlarms(3, true);
		when(this.acl.getAlarm(2)).thenReturn(returnedAlarms.get(1));
		when(this.acl.getAlarm(5)).thenThrow(new NoSuchElementException());

		this.r = this.target.path("alarms/0").request().get();
		assertEquals(204, this.r.getStatus());

		this.r = this.target.path("alarms/2").request().get();
		assertEquals(200, this.r.getStatus());
		assertEquals(this.acl.getAlarm(2).getId(), 2);

		this.r = this.target.path("alarms/5").request().get();
		assertEquals(404, this.r.getStatus());
		assertTrue(this.r.readEntity(String.class).startsWith("there is no alarm5"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#resetAlarms()}.
	 */
	@Test
	void testResetAlarms() {
		this.r = this.target.path("alarms/reset").request().post(null);
		assertEquals(204, this.r.getStatus());
		verify(this.acl).resetAlarms();
	}

	/**
	 * Test method for new Alarms
	 */
	@Test
	void testNewAlarm() {
		Alarm newAlarm = new Alarm(1, LocalDateTime.of(2018, 8, 21, 21, 21), true);
		when(this.acl.setAlarm(1, 21, 21)).thenReturn(newAlarm);
		Entity<Alarm> entity = Entity.entity(newAlarm, MediaType.APPLICATION_JSON);

		this.r = this.target.path("alarms/new").request().post(entity);
		assertEquals(200, this.r.getStatus());
		Alarm alarmread = this.r.readEntity(Alarm.class);
		assertEquals(newAlarm, alarmread);
	}

	/**
	 * Test method for editing the alarm
	 */
	@Test
	void testEditAlarm() {
		List<Alarm> returnedAlarms = createAlarms(3, true);

		when(this.acl.editAlarm(1, 1, 10, 20)).thenReturn(returnedAlarms.get(1));
		Entity<Alarm> entity = Entity.entity(new Alarm(1, LocalDateTime.of(2018, 8, 23, 12, 12), true),
				MediaType.APPLICATION_JSON);

		this.r = this.target.path("alarms/delete/1").request().post(entity);
		assertEquals(204, this.r.getStatus());
		verify(this.acl).deleteAlarm(1);
	}

	/**
	 * 
	 */
	@Test
	void testActivateAlarm() {
		List<Alarm> returnedAlarms = createAlarms(3, false);
		when(this.acl.getAlarm(1)).thenReturn(returnedAlarms.get(0));
		Entity<Alarm> entity = Entity.entity(returnedAlarms.get(0), MediaType.APPLICATION_JSON);

		this.r = this.target.path("alarms/de.activate/1").request().post(entity);
		assertEquals(204, this.r.getStatus());
		verify(this.acl).activateAlarm(1);
	}

	/**
	 * 
	 */
	@Test
	void testDeactivateAlarm() {
		List<Alarm> returnedAlarms = createAlarms(3, true);
		when(this.acl.getAlarm(1)).thenReturn(returnedAlarms.get(0));
		Entity<Alarm> entity = Entity.entity(returnedAlarms.get(0), MediaType.APPLICATION_JSON);

		this.r = this.target.path("alarms/de.activate/1").request().post(entity);
		assertEquals(204, this.r.getStatus());
		verify(this.acl).deactivateAlarm(1);
	}

	/**
	 * 
	 */
	@Test
	void testDeleteAlarm() {
		List<Alarm> returnedAlarms = createAlarms(3, true);
		when(this.acl.getAlarm(1)).thenReturn(returnedAlarms.get(0));
		Entity<Alarm> entity = Entity.entity(returnedAlarms.get(0), MediaType.APPLICATION_JSON);

		this.r = this.target.path("alarms/delete/1").request().post(entity);
		assertEquals(204, this.r.getStatus());
		verify(this.acl).deleteAlarm(1);
	}

}
