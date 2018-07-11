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

package de.unistuttgart.iaas.amyassist.amy.httpserver;

import static java.time.Duration.ofSeconds;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;

/**
 * 
 * @author Leon Kiefer
 */
class ServerTest {

	private DependencyInjection dependencyInjection;

	@BeforeEach
	void setup() {
		Properties serverConfig = new Properties();
		serverConfig.setProperty(Server.PROPERTY_PORT, "50080");
		serverConfig.setProperty(Server.PROPERTY_ROOT_PATH, "/test/");
		serverConfig.setProperty(Server.PROPERTY_LOCALHOST, "true");

		ConfigurationLoader configLoader = Mockito.mock(ConfigurationLoader.class);
		Mockito.when(configLoader.load(Server.CONFIG_NAME)).thenReturn(serverConfig);

		this.dependencyInjection = new DependencyInjection();
		this.dependencyInjection.addExternalService(ConfigurationLoader.class, configLoader);
		this.dependencyInjection.addExternalService(Logger.class, LoggerFactory.getLogger(Server.class));
	}

	@Test
	void test() {
		assertTimeout(ofSeconds(2), () -> {
			Server server = this.dependencyInjection.createAndInitialize(Server.class);
			server.start(TestRestResource.class);
			server.shutdown();
		}, "The Server start and shotdown takes longer then 2 Seconds");

	}

	@Test
	void testCantStartServerTwice() {
		Server server = this.dependencyInjection.createAndInitialize(Server.class);
		String message = assertThrows(IllegalStateException.class, () -> {
			server.start(TestRestResource.class);
			server.start(TestRestResource.class);
		}, "The Server dont throw an IllegalStateException if its started twice").getMessage();
		server.shutdown();
		assertThat(message, equalTo("The Server is already started"));
	}

	@Test
	void testRegister() {
		Server server = this.dependencyInjection.createAndInitialize(Server.class);
		server.register(TestRestResource.class);
		server.start();
		server.shutdown();
	}

	@Test
	void testRegisterNonResourceClass() {
		Server server = this.dependencyInjection.createAndInitialize(Server.class);
		assertThrows(IllegalArgumentException.class, () -> {
			server.register(ServerTest.class);
		}, "The Server dont throw an IllegalArgumentException if a registered class is not a Rest Resource");
	}
}
