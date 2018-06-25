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

package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommandHandler;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test the PluginManager
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
class PluginManagerTest {
	@Reference
	private TestFramework testFramework;
	private PluginManager serviceUnderTest;
	private Properties properties;

	@BeforeEach
	void setup() {
		this.testFramework.mockService(DependencyInjection.class);
		this.testFramework.mockService(PluginLoader.class);
		ConfigurationLoader configurationLoader = this.testFramework.mockService(ConfigurationLoader.class);
		this.properties = new Properties();
		when(configurationLoader.load("plugin.config")).thenReturn(this.properties);

		this.testFramework.mockService(SpeechCommandHandler.class);

		this.serviceUnderTest = this.testFramework.setServiceUnderTest(PluginManager.class);
	}

	@Test
	void testCantLoadTwice() {
		this.properties.setProperty("pluginDir", "");
		this.properties.setProperty("plugins", "");

		this.serviceUnderTest.loadPlugins();
		assertThrows(IllegalStateException.class, () -> this.serviceUnderTest.loadPlugins());
	}

	@Test
	void testPluginNotFound() {
		this.properties.setProperty("pluginDir", "");
		this.properties.setProperty("plugins", "testPlugin");

		this.serviceUnderTest.loadPlugins();

	}

}
