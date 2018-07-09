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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * test for the rest resource of the system time
 * 
 * @author Muhammed Kaya
 *
 */
@ExtendWith(FrameworkExtension.class)
class SystemTimeRestTest {

	@Reference
	private TestFramework testFramework;

	private SystemTimeLogic logic;

	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.testFramework.setRESTResource(SystemTimeResource.class);
		this.logic = this.testFramework.mockService(SystemTimeLogic.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.systemtime.SystemTimeResource#getTime()}.
	 */
	@Test
	void testGetTime() {
		Mockito.when(this.logic.getTime()).thenReturn("12 34 56");

		Response response = this.target.path("systemtime").path("time").request().get();
		String responseMsg = this.target.path("systemtime").path("time").request().get(String.class);

		assertEquals("12 34 56", responseMsg);
		assertEquals(200, response.getStatus());
		assertNotNull(response.getEntity());
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.systemtime.SystemTimeResource#getDate()}.
	 */
	@Test
	void testGetDate() {
		Mockito.when(this.logic.getDate()).thenReturn("31 12 18");

		Response response = this.target.path("systemtime").path("date").request().get();
		String responseMsg = this.target.path("systemtime").path("date").request().get(String.class);

		assertEquals("31 12 18", responseMsg);
		assertEquals(200, response.getStatus());
		assertNotNull(response.getEntity());
	}

}
