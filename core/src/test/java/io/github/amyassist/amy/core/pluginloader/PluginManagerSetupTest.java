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

package io.github.amyassist.amy.core.pluginloader;

import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.DependencyInjection;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.persistence.Persistence;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Tests the post constructor of the plugin manager.
 * 
 * @author Tim Neumann
 */
@ExtendWith(FrameworkExtension.class)
class PluginManagerSetupTest {

	@Reference
	private TestFramework testFramework;

	private Properties properties;

	/**
	 * Setup
	 * 
	 * @throws Exception
	 *             if anything goes wrong.
	 */
	@BeforeEach
	void setup() throws Exception {
		this.testFramework.mockService(DependencyInjection.class);
		this.testFramework.mockService(PluginLoader.class);

		Path tempDir = Files.createTempDirectory(PluginManagerService.class.getName());
		tempDir.toFile().deleteOnExit();
		Environment environment = this.testFramework.mockService(Environment.class);
		when(environment.getWorkingDirectory()).thenReturn(tempDir);

		ConfigurationManager configurationManager = this.testFramework.mockService(ConfigurationManager.class);
		this.properties = new Properties();
		when(configurationManager.getConfigurationWithDefaults("plugin.config")).thenReturn(this.properties);

		//this.testFramework.mockService(NLProcessingManager.class);
		this.testFramework.mockService(Persistence.class);

	}

	/**
	 * Tests the an error occurs when the mode is dev the properties contain no plugins
	 */
	@Test
	void testInvalidPropertiesNoPlugins() {
		this.properties.setProperty("pluginDir", "test");
		Assertions.assertThrows(IllegalStateException.class,
				() -> this.testFramework.setServiceUnderTest(PluginManagerService.class),
				"The property plugins is not set.");
	}

	/**
	 * Tests the an error occurs when the mode is dev the properties contain no pluginDir
	 */
	@Test
	void testInvalidPropertiesNoPluginDir() {
		this.properties.setProperty("plugins", "test");
		Assertions.assertThrows(IllegalStateException.class,
				() -> this.testFramework.setServiceUnderTest(PluginManagerService.class),
				"The property pluginDir is not set.");
	}
}
