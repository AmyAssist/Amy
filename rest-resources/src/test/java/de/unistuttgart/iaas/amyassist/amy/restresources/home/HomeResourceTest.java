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

package de.unistuttgart.iaas.amyassist.amy.restresources.home;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;
import de.unistuttgart.iaas.amyassist.amy.restresources.home.HomeResource;
import de.unistuttgart.iaas.amyassist.amy.restresources.home.SimplePluginEntity;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test Class for {@link de.unistuttgart.iaas.amyassist.amy.restresources.home.HomeResource}
 * 
 * @author Christian Bräuner
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
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.restresources.home.HomeResource#useAmy(java.lang.String)}.
	 */
	@Test
	void testUseAmy() {
		String consoleInput = "Amy do something";
		String result = "I did something";
		Mockito.when(this.speechInputHandler.handle(consoleInput)).thenReturn(CompletableFuture.completedFuture(result));
		Entity<String> entity = Entity.entity(consoleInput, MediaType.TEXT_PLAIN);
		Response r = this.target.path("console").request().post(entity);
		assertEquals(200, r.getStatus());
		assertEquals(result, r.readEntity(String.class));
		Mockito.verify(this.speechInputHandler).handle(consoleInput);
		
		consoleInput = "wrong input";
		Mockito.when(this.speechInputHandler.handle(consoleInput)).thenThrow(new RuntimeException("some exception"));
		entity = Entity.entity(consoleInput, MediaType.TEXT_PLAIN);
		r = this.target.path("console").request().post(entity);
		assertEquals(500, r.getStatus());
		assertTrue(r.readEntity(String.class).startsWith("can't handle input: " + consoleInput));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.restresources.home.HomeResource#getPlugins()}.
	 */
	@Test
	@Disabled
	void testGetPlugins() {
		IPlugin[] plugins = setupPlugins();
		Response r = this.target.request().get();
		assertEquals(200, r.getStatus());
		SimplePluginEntity[] spes = r.readEntity(SimplePluginEntity[].class);
		assertEquals(plugins.length+1, spes.length);
		for(int i = 0; i < plugins.length; i++) {
			assertEquals(plugins[i].getDisplayName(), spes[i].getName());
			assertEquals(plugins[i].getDescription(), spes[i].getDescription());
			assertEquals(expectedLink(plugins[i]), spes[i].getLink());
		}
		
	}

	private String expectedLink(IPlugin iPlugin) {
		// it would be nice if the plugin could provide this
		//TODO create Link somehow
		return null;
	}

	private IPlugin[] setupPlugins() {
		IPlugin[] plugins = null;
		//TODO create some Plugins
		return plugins;
	}

}
