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

package de.unistuttgart.iaas.amyassist.amy.core.configuration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandler;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Test IO of ConfigurationLoader
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
class ConfigurationLoaderTest {
	@Reference
	private TestFramework testFramework;
	private ConfigurationLoader configurationLoader;
	private Path tempDir;

	@BeforeEach
	void setup() throws IOException {
		this.testFramework.mockService(CommandLineArgumentHandler.class);
		Environment environment = this.testFramework.mockService(Environment.class);
		this.tempDir = Files.createTempDirectory(ConfigurationLoaderTest.class.getName());
		this.tempDir.toFile().deleteOnExit();
		Files.createDirectory(this.tempDir.resolve("config"));
		Mockito.when(environment.getWorkingDirectory()).thenReturn(this.tempDir);

		this.configurationLoader = this.testFramework.setServiceUnderTest(ConfigurationLoader.class);
	}

	@Test
	void testStore() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(ConfigurationLoader.class);
		Properties properties = new Properties();
		properties.setProperty("simpleKey", "test value!!");

		this.configurationLoader.store("test", properties);

		assertThat(Files.exists(this.tempDir.resolve("config/test.properties")), is(true));
		assertThat(testLogger.getLoggingEvents(), is(empty()));
	}

	@AfterEach
	void cleanUp() throws IOException {
		Files.walk(this.tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}

}
