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

package de.unistuttgart.iaas.amyassist.amy.core.io;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.slf4j.Logger;

import com.google.common.collect.Streams;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.information.ProgramInformation;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Tests the {@link CommandLineArgumentHandlerService}
 * 
 * @author Tim Neumann
 */
@ExtendWith(FrameworkExtension.class)
class CommandLineArgumentHandlerServiceTest {
	@Reference
	private TestFramework testFramework;

	private Logger logger = TestLoggerFactory.getTestLogger(CommandLineArgumentHandlerServiceTest.class);

	private ProgramInformation pi;

	/**
	 * Setup before each test
	 */
	@BeforeEach
	void setup() {
		this.pi = this.testFramework.mockService(ProgramInformation.class);
		Mockito.when(this.pi.getLicenseNotice()).thenReturn("notice");
		Mockito.when(this.pi.getVersion()).thenReturn("version");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.io.CommandLineArgumentHandlerService#shouldProgramContinue()}.
	 * 
	 * @param args
	 *            The cma args to test with
	 */
	@ParameterizedTest
	@MethodSource("stopFlags")
	void testShouldProgramContinue(String[] args) {
		CommandLineArgumentHandlerService cmaService = new CommandLineArgumentHandlerService(this.pi, this.logger::info,
				args);
		Assertions.assertFalse(cmaService.shouldProgramContinue(), "Program should not continue");
	}

	/**
	 * Returns valid flags that should stop execution
	 * 
	 * @return a stream of valid args that should stop execution
	 */
	static Stream<Arguments> stopFlags() {
		Stream<String[]> oneFlag = Stream.of("-h", "--help", "-v", "--version", "--notice")
				.map(s -> new String[] { s });
		Stream<String[]> multipleFlags = Stream.of(new String[] { "-v", "-c", "bla" },
				new String[] { "-c", "bla", "-v", "-h" });
		return Streams.concat(oneFlag, multipleFlags).map(arr -> Arguments.of((Object) arr));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.io.CommandLineArgumentHandlerService#shouldProgramContinue()}.
	 * 
	 * @param args
	 *            The cma args to test with
	 */
	@ParameterizedTest
	@MethodSource("invalidFlags")
	void testShouldProgramContinueWithInvalidFlags(String[] args) {
		CommandLineArgumentHandlerService cmaService = new CommandLineArgumentHandlerService(this.pi, this.logger::info,
				args);
		Assertions.assertFalse(cmaService.shouldProgramContinue(), "Program should not continue");
	}

	/**
	 * Returns invalid args
	 * 
	 * @return a stream of invalid args
	 */
	static Stream<Arguments> invalidFlags() {
		Stream<String[]> oneFlag = Stream.of("-o", "--meh", "asd", "-c", "-p").map(s -> new String[] { s });
		Stream<String[]> multipleFlags = Stream.of(new String[] { "-p", "a", "-c" },
				new String[] { "bla", "-p", "bla" });
		return Streams.concat(oneFlag, multipleFlags).map(arr -> Arguments.of((Object) arr));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.io.CommandLineArgumentHandlerService#getInfo()}.
	 * 
	 * @param configs
	 *            The config names to test with
	 */
	@ParameterizedTest
	@MethodSource("names")
	void testGetConfigPaths(String[] configs) {
		String[] args = new String[configs.length * 2];

		for (int i = 0; i < configs.length; i++) {
			args[i * 2] = "-c";
			args[i * 2 + 1] = configs[i];
		}

		CommandLineArgumentHandlerService cmaService = new CommandLineArgumentHandlerService(this.pi, this.logger::info,
				args);
		Assertions.assertTrue(cmaService.shouldProgramContinue());
		List<String> correct = Arrays.asList(configs);
		Assertions.assertEquals(correct, cmaService.getInfo().getConfigPaths());
	}

	/**
	 * Tests that a duplicate flag get handeled correctly.
	 */
	@Test
	void testDuplicateFlag() {
		String[] args = new String[2];
		args[0] = "-h";
		args[1] = "-h";
		CommandLineArgumentHandlerService cmaService = new CommandLineArgumentHandlerService(this.pi, this.logger::info,
				args);
		Assertions.assertFalse(cmaService.shouldProgramContinue());
	}

	/**
	 * Returns names. Could be config or plugin names/paths.
	 * 
	 * @return a stream of names
	 */
	static Stream<Arguments> names() {
		Stream<String[]> oneName = Stream
				.of("test", "test2", "test3", "-c", "-p", "-h", "--config", "--plugin", "--help")
				.map(s -> new String[] { s });
		Stream<String[]> multipleNames = Stream.of(new String[] { "test1", "test2", "test3" },
				new String[] { "test1", "test2", "test3", "test4", "test5", "test6" },
				new String[] { "-c", "-p", "--config", "--help", "--notice" });
		return Streams.concat(oneName, multipleNames).map(arr -> Arguments.of((Object) arr));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.io.CommandLineArgumentHandlerService#getInfo()}.
	 * 
	 * @param plugins
	 *            The plugin names to test with
	 */
	@ParameterizedTest
	@MethodSource("names")
	void testGetPluginsPaths(String[] plugins) {

		String[] args = new String[plugins.length * 2];

		for (int i = 0; i < plugins.length; i++) {
			args[i * 2] = "-p";
			args[i * 2 + 1] = plugins[i];
		}
		CommandLineArgumentHandlerService cmaService = new CommandLineArgumentHandlerService(this.pi, this.logger::info,
				args);
		Assertions.assertTrue(cmaService.shouldProgramContinue());
		List<String> correct = Arrays.asList(plugins);
		Assertions.assertEquals(correct, cmaService.getInfo().getPluginPaths());
	}

}
