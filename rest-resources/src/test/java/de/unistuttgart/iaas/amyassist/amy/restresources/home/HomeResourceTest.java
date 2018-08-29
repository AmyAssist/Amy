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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test Class for {@link de.unistuttgart.iaas.amyassist.amy.restresources.chat.ChatResource}
 * 
 * @author Christian Bräuner
 */
@ExtendWith(FrameworkExtension.class)
class HomeResourceTest {

	@Reference
	private TestFramework testFramework;

	private WebTarget target;

	private MessageHub messageHub;
	private PluginManager manager;

	/**
	 * setup for server and client
	 */
	@BeforeEach
	void setUp() {
		this.manager = this.testFramework.mockService(PluginManager.class);
		this.messageHub = this.testFramework.mockService(MessageHub.class);
		this.target = this.testFramework.setRESTResource(HomeResource.class);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.restresources.chat.ChatResource#getPlugins()}.
	 */
	@Test
	void testGetPlugins() {
		IPlugin[] plugins = setupPlugins();
		Mockito.when(this.manager.getPlugins()).thenReturn(Arrays.asList(plugins));
		Response r = this.target.request().get();
		assertEquals(200, r.getStatus());
		SimplePluginEntity[] spes = r.readEntity(SimplePluginEntity[].class);
		assertEquals(plugins.length, spes.length);
		for (int i = 0; i < plugins.length; i++) {
			assertEquals(plugins[i].getDisplayName(), spes[i].getName());
			assertEquals(plugins[i].getDescription(), spes[i].getDescription());
			assertEquals(null, spes[i].getLink());
		}
		assertEquals("Configuration", spes[spes.length - 1].getName());
		assertEquals("Configurations for this Amy instance and installed plugins",
				spes[spes.length - 1].getDescription());
		assertEquals(this.target.getUriBuilder().replacePath("config").toString(), spes[spes.length - 1].getLink());
	}

	private IPlugin[] setupPlugins() {
		IPlugin[] plugins = new IPlugin[5];
		for (int i = 0; i < 5; i++) {
			plugins[i] = Mockito.mock(IPlugin.class);
			Mockito.when(plugins[i].getDisplayName()).thenReturn("Display" + i);
			Mockito.when(plugins[i].getDescription()).thenReturn("Description" + i);

		}
		return plugins;
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.restresources.home.HomeResource#mute()}.
	 */
	@Test
	void testMute() {
		Response r = this.target.path("mute").request().post(null);
		assertEquals(204, r.getStatus());
		Mockito.verify(messageHub).publish("home/all/mute", "true");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.restresources.home.HomeResource#unmute()}.
	 */
	@Test
	void testUnmute() {
		Response r = this.target.path("unmute").request().post(null);
		assertEquals(204, r.getStatus());
		Mockito.verify(messageHub).publish("home/all/mute", "false");
	}
}
