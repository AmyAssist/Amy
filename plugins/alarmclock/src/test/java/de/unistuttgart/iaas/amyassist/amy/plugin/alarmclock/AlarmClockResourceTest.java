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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;
import de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest.Timestamp;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * test for the rest resource of the alarmclock
 * 
 * @author Christian Bräuner
 */
@ExtendWith(FrameworkExtension.class)
class AlarmClockResourceTest {

	@Reference
	private TestFramework testFramework;

	private AlarmClockLogic logic;

	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.logic = this.testFramework.mockService(AlarmClockLogic.class);
		this.testFramework.setRESTResource(AlarmClockResource.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI.toString() + "/" + AlarmClockResource.PATH);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#getAllAlarm()}.
	 */
	@Test
	void testGetAllAlarms() {
		Alarm[] alarms = createAlarms(15);
		when(this.logic.getAllAlarms()).thenReturn(alarms);

		Response r = this.target.path("alarms").request().get();
		assertEquals(200, r.getStatus());
		Timestamp[] ts = r.readEntity(Timestamp[].class);
		assertEquals(15, ts.length);
		for (int i = 0; i < ts.length; i++) {
			Timestamp expected = new Timestamp(alarms[i]);
			assertEquals(expected, ts[i]);
			assertEquals(Server.BASE_URI.toString() + "/" + AlarmClockResource.PATH + "/alarms/" + i, ts[i].getLink());
		}

	}

	private Alarm[] createAlarms(int arraySize) {
		Random random = new Random();
		Alarm[] alarms = new Alarm[arraySize];
		for (int cnt = 0; cnt < arraySize; cnt++) {
			alarms[cnt] = new Alarm(cnt, random.nextInt(24), random.nextInt(60), true);
		}
		return alarms;
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#getAlarm()}.
	 * 
	 */
	@Test
	void testGetAlarm() {
		Alarm[] alarms = createAlarms(5);
		when(this.logic.getAlarm(0)).thenReturn(alarms[0]);
		when(this.logic.getAlarm(1)).thenReturn(alarms[1]);
		when(this.logic.getAlarm(2)).thenReturn(alarms[2]);
		when(this.logic.getAlarm(3)).thenReturn(alarms[3]);
		when(this.logic.getAlarm(4)).thenReturn(alarms[4]);
		when(this.logic.getAlarm(5)).thenThrow(new NoSuchElementException());

		Response r = this.target.path("alarms/0").request().get();
		assertEquals(200, r.getStatus());
		Timestamp ts = r.readEntity(Timestamp.class);
		assertEquals(new Timestamp(alarms[0]), ts);
		r = this.target.path("alarms/1").request().get();
		assertEquals(200, r.getStatus());
		ts = r.readEntity(Timestamp.class);
		assertEquals(new Timestamp(alarms[1]), ts);
		r = this.target.path("alarms/2").request().get();
		assertEquals(200, r.getStatus());
		ts = r.readEntity(Timestamp.class);
		assertEquals(new Timestamp(alarms[2]), ts);
		r = this.target.path("alarms/3").request().get();
		assertEquals(200, r.getStatus());
		ts = r.readEntity(Timestamp.class);
		assertEquals(new Timestamp(alarms[3]), ts);
		r = this.target.path("alarms/4").request().get();
		assertEquals(200, r.getStatus());
		ts = r.readEntity(Timestamp.class);
		assertEquals(new Timestamp(alarms[4]), ts);

		r = this.target.path("alarms/5").request().get();
		assertEquals(404, r.getStatus());
		assertTrue(r.readEntity(String.class).startsWith("there is no alarm5"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#resetAlarms()}.
	 */
	@Test
	void testResetAlarms() {
		Response r = this.target.path("alarms/reset").request().post(null);
		assertEquals(204, r.getStatus());
		verify(this.logic).resetAlarms();
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#newAlarm()}.
	 */
	@Test
	void testNewAlarm() {
		Alarm newAlarm = new Alarm(17, 20, 1, true);
		when(this.logic.setAlarm(new int[] { 20, 1 })).thenReturn(newAlarm);
		Timestamp ts = new Timestamp(newAlarm);
		Entity<Timestamp> entity = Entity.entity(ts, MediaType.APPLICATION_JSON);

		Response r = this.target.path("alarms/new").request().post(entity);
		assertEquals(200, r.getStatus());
		Timestamp tsr = r.readEntity(Timestamp.class);
		assertEquals(new Timestamp(newAlarm), tsr);
		assertEquals(Server.BASE_URI.toString() + "/" + AlarmClockResource.PATH + "/alarms/17", tsr.getLink());

		entity = Entity.entity(new Timestamp(27, 20), MediaType.APPLICATION_JSON);
		r = this.target.path("alarms/new").request().post(entity);
		assertEquals(400, r.getStatus());
		assertEquals("The given time wasn't a valid time", r.readEntity(String.class));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#editAlarm()}.
	 */
	@Test
	void testEditAlarm() {
		Alarm[] alarms = createAlarms(5);

		Response r = this.target.path("alarms/0").request().post(null);
		assertEquals(400, r.getStatus());
		assertEquals("The given time wasn't a valid time", r.readEntity(String.class));

		Timestamp ts = new Timestamp(alarms[1]);
		when(this.logic.editAlarm(1, new int[] { 10, 20 })).thenReturn(alarms[1]);
		Entity<Timestamp> entity = Entity.entity(new Timestamp(10, 20), MediaType.APPLICATION_JSON);

		r = this.target.path("alarms/1").request().post(entity);
		assertEquals(200, r.getStatus());
		assertEquals(new Timestamp(alarms[1]), r.readEntity(Timestamp.class));

		when(this.logic.editAlarm(20, new int[] { 10, 20 })).thenThrow(new NoSuchElementException());
		r = this.target.path("alarms/20").request().post(entity);
		assertEquals(404, r.getStatus());
		assertTrue(r.readEntity(String.class).startsWith("there is no alarm20"));

		when(this.logic.editAlarm(2, new int[] { 10, 20 })).thenReturn(alarms[2]);
		r = this.target.path("alarms/2").queryParam("mode", "edit").request().post(entity);
		assertEquals(200, r.getStatus());
		assertEquals(new Timestamp(alarms[2]), r.readEntity(Timestamp.class));

		r = this.target.path("alarms/3").queryParam("mode", "deactivate").request().post(null);
		assertEquals(204, r.getStatus());
		verify(this.logic).deactivateAlarm(3);

		r = this.target.path("alarms/4").queryParam("mode", "activate").request().post(null);
		assertEquals(204, r.getStatus());
		verify(this.logic).activateAlarm(4);

		r = this.target.path("alarms/5").queryParam("mode", "delete").request().post(null);
		assertEquals(204, r.getStatus());
		verify(this.logic).deleteAlarm(5);
	}

}
