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

import static de.unistuttgart.iaas.amyassist.amy.test.matcher.logger.LoggerMatchers.hasLogged;
import static de.unistuttgart.iaas.amyassist.amy.test.matcher.logger.LoggerMatchers.warn;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandler;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.Persistence;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Test the PluginManager
 * 
 * @author Leon Kiefer, Tim Neumann
 */
@ExtendWith(FrameworkExtension.class)
class PluginManagerTest {
	@Reference
	private TestFramework testFramework;

	private PluginManager serviceUnderTest;
	private Properties properties;

	private Path tempDir;

	@BeforeEach
	void setup() throws IOException {
		this.testFramework.mockService(DependencyInjection.class);
		this.testFramework.mockService(PluginLoader.class);

		this.tempDir = Files.createTempDirectory(PluginManagerService.class.getName());
		this.tempDir.toFile().deleteOnExit();
		Environment environment = this.testFramework.mockService(Environment.class);
		when(environment.getWorkingDirectory()).thenReturn(this.tempDir);

		Files.createDirectory(this.tempDir.resolve("plugins"));
		
		Files.createFile(this.tempDir
				.resolve("plugins").resolve("testxml.xml"));

		CommandLineArgumentHandler cmaHandler = this.testFramework.mockService(CommandLineArgumentHandler.class);
		when(cmaHandler.getPluginPaths()).thenReturn(null);

		ConfigurationManager configurationManager = this.testFramework.mockService(ConfigurationManager.class);
		this.properties = new Properties();
		this.properties.setProperty("pluginDir", "plugins");
		this.properties.setProperty("plugins", "");
		this.properties.setProperty("mode", "dev");
		when(configurationManager.getConfigurationWithDefaults("plugin.config")).thenReturn(this.properties);

		this.testFramework.mockService(Persistence.class);

		this.serviceUnderTest = this.testFramework.setServiceUnderTest(PluginManagerService.class);
	}

	@Test
	void testCantLoadTwice() {

		this.serviceUnderTest.loadPlugins();
		assertThrows(IllegalStateException.class, () -> this.serviceUnderTest.loadPlugins());
	}
	
	@Test
	void testGetXML() {

	}

	@Test
	void testPluginNotFound() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(PluginManagerService.class);
		this.properties.setProperty("plugins", "testPlugin");

		this.serviceUnderTest.loadPlugins();
		assertThat(testLogger, hasLogged(warn("The plugin {} does not exist in the plugin directory.",
				this.tempDir.resolve("plugins").resolve("testPlugin"))));
	}

}
