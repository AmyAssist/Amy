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

import static de.unistuttgart.iaas.amyassist.amy.test.matcher.logger.LoggerMatchers.*;
import static de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers.tossedExactly;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.Persistence;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

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

		this.testFramework.mockService(NLProcessingManager.class);
		this.testFramework.mockService(Persistence.class);

	}

	/**
	 * Tests the an error occurs when the properties contain no mode
	 */
	@Test
	void testInvalidPropertiesNoMode() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(PluginManagerService.class);
		this.testFramework.setServiceUnderTest(PluginManagerService.class);
		assertThat(testLogger, hasLogged(error("Error loading config.",
				tossedExactly(IllegalStateException.class, "The property mode is not set."))));
	}

	/**
	 * Tests the an error occurs when the properties contain an invalid mode
	 * 
	 * @param mode
	 *            A invalid mode
	 */
	@ParameterizedTest
	@MethodSource("unknownModes")
	void testInvalidPropertiesUnkownMode(String mode) {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(PluginManagerService.class);
		this.properties.setProperty("mode", mode);
		this.testFramework.setServiceUnderTest(PluginManagerService.class);
		assertThat(testLogger, hasLogged(
				error("Error loading config.", tossedExactly(IllegalStateException.class, "Unknown mode: " + mode))));
	}

	/**
	 * Tests the an error occurs when the mode is dev the properties contain no plugins
	 */
	@Test
	void testInvalidPropertiesModeDevNoPlugins() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(PluginManagerService.class);
		this.properties.setProperty("mode", "dev");
		this.properties.setProperty("pluginDir", "test");
		this.testFramework.setServiceUnderTest(PluginManagerService.class);
		assertThat(testLogger, hasLogged(error("Error loading config.",
				tossedExactly(IllegalStateException.class, "The property plugins is not set."))));
	}

	/**
	 * Tests the an error occurs when the mode is dev the properties contain no pluginDir
	 */
	@Test
	void testInvalidPropertiesModeDevNoPluginDir() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(PluginManagerService.class);
		this.properties.setProperty("mode", "dev");
		this.properties.setProperty("plugins", "test");
		this.testFramework.setServiceUnderTest(PluginManagerService.class);
		assertThat(testLogger, hasLogged(error("Error loading config.",
				tossedExactly(IllegalStateException.class, "The property pluginDir is not set."))));
	}

	/**
	 * Tests the an error occurs when the mode is docker the properties contain no pluginDir
	 */
	@Test
	void testInvalidPropertiesModeDockerNoPluginDir() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(PluginManagerService.class);
		this.properties.setProperty("mode", "docker");
		this.testFramework.setServiceUnderTest(PluginManagerService.class);
		assertThat(testLogger, hasLogged(error("Error loading config.",
				tossedExactly(IllegalStateException.class, "The property pluginDir is not set."))));
	}

	/**
	 * Tests the an error occurs when the mode is manula the properties contain no plugins
	 */
	@Test
	void testInvalidPropertiesModeManualNoPlugins() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(PluginManagerService.class);
		this.properties.setProperty("mode", "manual");
		this.properties.setProperty("pluginDir", "test");
		this.testFramework.setServiceUnderTest(PluginManagerService.class);
		assertThat(testLogger, hasLogged(error("Error loading config.",
				tossedExactly(IllegalStateException.class, "The property plugins is not set."))));
	}

	/**
	 * @return invalid modes
	 */
	static Stream<String> unknownModes() {
		return Stream.of("", "mode", "de", "dock", "default", "devmanual");
	}
}
