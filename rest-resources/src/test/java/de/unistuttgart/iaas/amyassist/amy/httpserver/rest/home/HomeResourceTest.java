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

package de.unistuttgart.iaas.amyassist.amy.httpserver.rest.home;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CompletableFuture;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * TODO: Description
 * @author
 */
@ExtendWith(FrameworkExtension.class)
class HomeResourceTest {

	@Reference
	private TestFramework testFramework;

	private WebTarget target;

	private SpeechInputHandler speechInputHandler;
	
	/**
	 * setup for server and client
	 */
	@BeforeEach
	void setUp() {
		this.testFramework.mockService(PluginManager.class);
		this.speechInputHandler = this.testFramework.mockService(SpeechInputHandler.class);
		this.testFramework.setRESTResource(HomeResource.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI.toString() + "/" + HomeResource.PATH);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.httpserver.rest.home.HomeResource#useAmy(java.lang.String)}.
	 */
	@Test
	void testUseAmy() {
		String consoleInput = ""; //TODO write some posible inputs
		String result = "test";
		Mockito.when(speechInputHandler.handle(consoleInput)).thenReturn(CompletableFuture.completedFuture(result));
		Entity<String> entity = Entity.entity(consoleInput, MediaType.TEXT_PLAIN);
		Response r = this.target.path("console").request().post(entity);
		assertEquals(200, r.getStatus());
		//Mockito.verify(backendMock).someMethod(consoleInput);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.httpserver.rest.home.HomeResource#getPlugins()}.
	 */
	@Test
	void testGetPlugins() {
		//whatever the backend uses
		//Plugin[] plugins = setupPlugins()
		Response r = this.target.request().get();
		assertEquals(200, r.getStatus());
		SimplePluginEntity[] spes = r.readEntity(SimplePluginEntity[].class);
		//for(int i = 0; i < spes.lenght; i++) {
		//	assertEquals(plugins[i], spes[i]);
		//}
		
	}

}
