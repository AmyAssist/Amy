/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest.Timestamp;
import de.unistuttgart.iaas.amyassist.amy.rest.Server;

/**
 * test for the rest resource of the alarmclock
 * 
 * @author Christian Bräuner
 */
@ExtendWith(FrameworkExtention.class)
class AlarmClockRestTest {

	@Reference
	private TestFramework testFramework;

	private AlarmClockLogic logic;

	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.logic = this.testFramework.mockService(AlarmClockLogic.class);
		this.testFramework.setRESTResource(AlarmClockResource.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#getAlarm()}.
	 */
	@Test
	void testGetAlarm() {
//		when(this.logic.getAlarm()).thenReturn("16:05");
//
//		String responseMsg = this.target.path("alarmclock").request().get(String.class);
//		assertThat(responseMsg, equalTo("16:05"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#setAlarm(java.lang.String)}.
	 */
	@Test
	void testSetAlarm() {
//		Timestamp ts = new Timestamp();
//		ts.hour = 15;
//		ts.minute = 20;
//		Entity<Timestamp> entity = Entity.entity(ts, MediaType.APPLICATION_JSON);
//		this.target.path("alarmclock").request().post(entity);
//
//		verify(this.logic, atLeastOnce()).setAlarm("15:20");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#deleteAlarm(java.lang.String)}.
	 */
	@Test
	void testDeleteAlarm() {

	}

}
