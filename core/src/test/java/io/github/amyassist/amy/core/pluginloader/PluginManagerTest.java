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

import static io.github.amyassist.amy.test.matcher.logger.LoggerMatchers.*;
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
import org.mockito.Mockito;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.DependencyInjection;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;
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

	private PluginLoader pluginLoader;

	private Path tempDir;

	/**
	 * Setup
	 * 
	 * @throws IOException
	 *             When a error occurs
	 */
	@BeforeEach
	void setup() throws IOException {
		this.testFramework.mockService(DependencyInjection.class);
		this.pluginLoader = this.testFramework.mockService(PluginLoader.class);

		this.tempDir = Files.createTempDirectory(PluginManagerService.class.getName());
		this.tempDir.toFile().deleteOnExit();
		Environment environment = this.testFramework.mockService(Environment.class);
		when(environment.getWorkingDirectory()).thenReturn(this.tempDir);

		Files.createDirectory(this.tempDir.resolve("plugins"));

		ConfigurationManager configurationManager = this.testFramework.mockService(ConfigurationManager.class);
		this.properties = new Properties();
		when(configurationManager.getConfigurationWithDefaults("plugin.config")).thenReturn(this.properties);

		this.properties.setProperty("pluginDir", "plugins");
		this.properties.setProperty("plugins", "");

		this.serviceUnderTest = this.testFramework.setServiceUnderTest(PluginManagerService.class);
	}

	/**
	 * Test that the plugin manager can't load twice.
	 * 
	 * @throws IOException
	 *             When a IO error occurs
	 */
	@Test
	void testCantLoadTwice() throws IOException {
		this.serviceUnderTest.loadPlugins();
		assertThrows(IllegalStateException.class, () -> this.serviceUnderTest.loadPlugins());
	}

	/**
	 * Test that it is logged, when a plugin can't be found
	 * 
	 * @throws IOException
	 *             When a IO error occurs
	 */
	@Test
	void testPluginNotFound() throws IOException {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(PluginManagerService.class);
		this.properties.setProperty("plugins", "testPlugin");

		this.serviceUnderTest.loadPlugins();
		assertThat(testLogger, hasLogged(warn("The Plugin {} is missing its jar file {} and is therefore not loaded.",
				"testPlugin", this.tempDir.resolve("plugins").resolve("testPlugin.jar"))));
	}

	/**
	 * Tests {@link PluginManager#loadPlugins()}
	 * 
	 * @throws IOException
	 *             When a IO error occurs
	 */
	@Test
	void testLoadPlugins() throws IOException {
		this.properties.setProperty("pluginDir", "plugins");
		this.properties.setProperty("plugins", "pluginA");
		Path pluginDir = this.tempDir.resolve("plugins");
		Path plugin1 = pluginDir.resolve("pluginA.jar");
		Path plugin2 = pluginDir.resolve("pluginB.jar");
		Files.createDirectories(pluginDir);
		Files.createFile(plugin1);
		Files.createFile(plugin2);
		this.serviceUnderTest.loadPlugins();
		Mockito.verify(this.pluginLoader).loadPlugin(plugin1);
		Mockito.verify(this.pluginLoader, Mockito.never()).loadPlugin(plugin2);
	}

	/**
	 * Tests {@link PluginManager#loadPlugins()} with plugins=all
	 * 
	 * @throws IOException
	 *             When a IO error occurs
	 */
	@Test
	void testLoadPluginsAll() throws IOException {
		this.properties.setProperty("pluginDir", "plugins");
		this.properties.setProperty("plugins", "all");
		Path pluginDir = this.tempDir.resolve("plugins");
		Path plugin1 = pluginDir.resolve("pluginA.jar");
		Path plugin2 = pluginDir.resolve("pluginB.jar");
		Files.createDirectories(pluginDir);
		Files.createFile(plugin1);
		Files.createFile(plugin2);
		this.serviceUnderTest.loadPlugins();
		Mockito.verify(this.pluginLoader).loadPlugin(plugin1);
		Mockito.verify(this.pluginLoader).loadPlugin(plugin2);
	}

}
