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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

	/**
	 * Setup before each test
	 * 
	 * @throws IOException
	 *             when a file system error occurs
	 */
	@BeforeEach
	void setup() throws IOException {
		CommandLineArgumentHandler cmaHandler = this.testFramework.mockService(CommandLineArgumentHandler.class);
		Environment environment = this.testFramework.mockService(Environment.class);
		this.tempDir = Files.createTempDirectory(ConfigurationLoaderTest.class.getName());
		this.tempDir.toFile().deleteOnExit();
		Files.createDirectory(this.tempDir.resolve("config"));
		Files.createDirectory(this.tempDir.resolve("config1"));
		Files.createDirectory(this.tempDir.resolve("config2"));
		Files.createDirectory(this.tempDir.resolve("config3"));

		writeToFile(this.tempDir.resolve("config").resolve("test1.properties"), "Test1:conf0");
		writeToFile(this.tempDir.resolve("config").resolve("test2.properties"), "Test2:conf0");
		writeToFile(this.tempDir.resolve("config").resolve("test3.properties"), "Test3:conf0");
		writeToFile(this.tempDir.resolve("config").resolve("test4.properties"), "Test4:conf0");

		writeToFile(this.tempDir.resolve("config1").resolve("test1.properties"), "Test1:conf1");
		writeToFile(this.tempDir.resolve("config1").resolve("test2.properties"), "Test2:conf1");
		writeToFile(this.tempDir.resolve("config1").resolve("test3.properties"), "Test3:conf1");

		writeToFile(this.tempDir.resolve("config2").resolve("test1.properties"), "Test1:conf2");
		writeToFile(this.tempDir.resolve("config2").resolve("test2.properties"), "Test2:conf2");

		writeToFile(this.tempDir.resolve("config3").resolve("test1.properties"), "Test1:conf3");

		Mockito.when(environment.getWorkingDirectory()).thenReturn(this.tempDir);
		Mockito.when(cmaHandler.getConfigPaths()).thenReturn(Arrays.asList("config1", "config2", "config3"));

		this.configurationLoader = this.testFramework.setServiceUnderTest(ConfigurationLoaderImpl.class);
	}

	private void writeToFile(Path p, String s) throws IOException {
		try (BufferedWriter bw = Files.newBufferedWriter(p, StandardOpenOption.CREATE_NEW)) {
			bw.write(s);
		}
	}

	private String readFile(Path p) throws IOException {
		String buf, ret = "";
		try (BufferedReader br = Files.newBufferedReader(p)) {
			while ((buf = br.readLine()) != null) {
				ret += buf;
			}
		}
		return ret;
	}

	/**
	 * Test the load method for the hierarchy level 3/3.
	 */
	@Test
	void testLoadFromConf3() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(ConfigurationLoaderImpl.class);

		Properties prop1 = this.configurationLoader.load("test1");
		Assertions.assertEquals("conf3", prop1.getProperty("Test1"),
				"test1.properties should be loaded from config3, but the value of Test1 is wrong");
		Assertions.assertEquals(1, prop1.size(), "test1.properties should only contain one element");

		assertThat(testLogger.getLoggingEvents(), is(empty()));
	}

	/**
	 * Test the load method for the hierarchy level 2/3.
	 */
	@Test
	void testLoadFromConf2() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(ConfigurationLoaderImpl.class);

		Properties prop2 = this.configurationLoader.load("test2");
		Assertions.assertEquals("conf2", prop2.getProperty("Test2"),
				"test2.properties should be loaded from config2, but the value of Test2 is wrong");
		Assertions.assertEquals(1, prop2.size(), "test2.properties should only contain one element");

		assertThat(testLogger.getLoggingEvents(), is(empty()));
	}

	/**
	 * Test the load method for the hierarchy level 1/3.
	 */
	@Test
	void testLoadFromConf1() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(ConfigurationLoaderImpl.class);

		Properties prop3 = this.configurationLoader.load("test3");
		Assertions.assertEquals("conf1", prop3.getProperty("Test3"),
				"test3.properties should be loaded from config1, but the value of Test3 is wrong");
		Assertions.assertEquals(1, prop3.size(), "test3.properties should only contain one element");

		assertThat(testLogger.getLoggingEvents(), is(empty()));
	}

	/**
	 * Test the load method for the hierarchy level 0/3.
	 */
	@Test
	void testLoadFromConf0() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(ConfigurationLoaderImpl.class);

		Properties prop4 = this.configurationLoader.load("test4");
		Assertions.assertEquals("conf0", prop4.getProperty("Test4"),
				"test4.properties should be loaded from config, but the value of Test4 is wrong");
		Assertions.assertEquals(1, prop4.size(), "test4.properties should only contain one element");

		assertThat(testLogger.getLoggingEvents(), is(empty()));
	}

	/**
	 * Test the store method when overwriting existing config files.
	 * 
	 * @throws IOException
	 *             when a file system error occurs
	 */
	@Test
	void testStoreExistingFile() throws IOException {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(ConfigurationLoaderImpl.class);

		Properties prop1 = this.configurationLoader.load("test1");
		Properties prop2 = this.configurationLoader.load("test2");
		Properties prop3 = this.configurationLoader.load("test3");
		Properties prop4 = this.configurationLoader.load("test4");

		prop1.setProperty("store", "StoreTest");
		prop2.setProperty("store", "StoreTest");
		prop3.setProperty("store", "StoreTest");
		prop4.setProperty("store", "StoreTest");

		this.configurationLoader.store("test1", prop1);
		this.configurationLoader.store("test2", prop1);
		this.configurationLoader.store("test3", prop1);
		this.configurationLoader.store("test4", prop1);

		String searchString = "store=StoreTest";

		assertThat("config/test1.properties should not have been written to.",
				readFile(this.tempDir.resolve("config").resolve("test1.properties")),
				not(containsString(searchString)));
		assertThat("config/test2.properties should not have been written to.",
				readFile(this.tempDir.resolve("config").resolve("test2.properties")),
				not(containsString(searchString)));
		assertThat("config/test3.properties should not have been written to.",
				readFile(this.tempDir.resolve("config").resolve("test3.properties")),
				not(containsString(searchString)));
		assertThat("config/test4.properties should have been written to.",
				readFile(this.tempDir.resolve("config").resolve("test4.properties")), containsString(searchString));

		assertThat("config1/test1.properties should not have been written to.",
				readFile(this.tempDir.resolve("config1").resolve("test1.properties")),
				not(containsString(searchString)));
		assertThat("config1/test2.properties should not have been written to.",
				readFile(this.tempDir.resolve("config1").resolve("test2.properties")),
				not(containsString(searchString)));
		assertThat("config1/test3.properties should have been written to.",
				readFile(this.tempDir.resolve("config1").resolve("test3.properties")), containsString(searchString));

		assertThat("config2/test1.properties should not have been written to.",
				readFile(this.tempDir.resolve("config2").resolve("test1.properties")),
				not(containsString(searchString)));
		assertThat("config2/test2.properties should have been written to.",
				readFile(this.tempDir.resolve("config2").resolve("test2.properties")), containsString(searchString));

		assertThat("config3/test1.properties should have been written to.",
				readFile(this.tempDir.resolve("config3").resolve("test1.properties")), containsString(searchString));

		assertThat(testLogger.getLoggingEvents(), is(empty()));
	}

	/**
	 * Test the store method when no such file exists yet.
	 */
	@Test
	void testStoreNewFile() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(ConfigurationLoaderImpl.class);
		Properties properties = new Properties();
		properties.setProperty("simpleKey", "test value!!");

		this.configurationLoader.store("test", properties);

		assertThat(Files.exists(this.tempDir.resolve("config3").resolve("test.properties")), is(true));
		assertThat(testLogger.getLoggingEvents(), is(empty()));
	}

	/**
	 * Clean up after each test
	 * 
	 * @throws IOException
	 *             when a file system error occurs
	 */
	@AfterEach
	void cleanUp() throws IOException {
		Files.walk(this.tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}

}
