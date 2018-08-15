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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * 
 * @author Leon Kiefer
 */
class ServerTest {

	@Reference
	private TestFramework testFramework;

	private DependencyInjection dependencyInjection;

	@BeforeEach
	void setup() {
		Properties serverConfig = new Properties();
		serverConfig.setProperty(Server.PROPERTY_PORT, "50080");
		serverConfig.setProperty(Server.PROPERTY_CONTEXT_PATH, "test");
		serverConfig.setProperty(Server.PROPERTY_LOCALHOST, "true");

		RemoteSR mockService = this.testFramework.mockService(RemoteSR.class);

		ConfigurationManager configManager = Mockito.mock(ConfigurationManager.class);
		Mockito.when(configManager.getConfigurationWithDefaults(Server.CONFIG_NAME)).thenReturn(serverConfig);

		this.dependencyInjection = new DependencyInjection();
		this.dependencyInjection.addExternalService(ConfigurationManager.class, configManager);
		this.dependencyInjection.addExternalService(Logger.class, LoggerFactory.getLogger(Server.class));
	}

	@Test
	void test() {
		assertTimeout(ofSeconds(2), () -> {
			Server server = this.dependencyInjection.createAndInitialize(Server.class);
			server.startWithResources(TestRestResource.class);
			server.stop();
		}, "The Server start and shotdown takes longer then 2 Seconds");

	}

	@Test
	void testCantStartServerTwice() {
		Server server = this.dependencyInjection.createAndInitialize(Server.class);
		String message = assertThrows(IllegalStateException.class, () -> {
			server.startWithResources(TestRestResource.class);
			server.startWithResources(TestRestResource.class);
		}, "The Server dont throw an IllegalStateException if its started twice").getMessage();
		server.stop();
		assertThat(message, equalTo("The Server is already started"));
	}

	@Test
	void testRegister() {
		Server server = this.dependencyInjection.createAndInitialize(Server.class);
		server.register(TestRestResource.class);
		server.start();
		server.stop();
	}

	@Test
	void testRegisterNonResourceClass() {
		Server server = this.dependencyInjection.createAndInitialize(Server.class);
		assertThrows(IllegalArgumentException.class, () -> {
			server.register(ServerTest.class);
		}, "The Server dont throw an IllegalArgumentException if a registered class is not a Rest Resource");
	}
}
