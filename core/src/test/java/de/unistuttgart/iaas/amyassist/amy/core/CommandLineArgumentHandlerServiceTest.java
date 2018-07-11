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

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Streams;

/**
 * Tests the {@link CommandLineArgumentHandlerService}
 * 
 * @author Tim Neumann
 */
class CommandLineArgumentHandlerServiceTest {

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandlerService#shouldProgramContinue()}.
	 * 
	 * @param args
	 *            The cma args to test with
	 */
	@ParameterizedTest
	@MethodSource("stopFlags")
	void testShouldProgramContinue(String[] args) {
		CommandLineArgumentHandlerService cmaService = new CommandLineArgumentHandlerService();
		cmaService.init(args);
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
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandlerService#shouldProgramContinue()}.
	 * 
	 * @param args
	 *            The cma args to test with
	 */
	@ParameterizedTest
	@MethodSource("invalidFlags")
	void testShouldProgramContinueWithInvalidFlags(String[] args) {
		CommandLineArgumentHandlerService cmaService = new CommandLineArgumentHandlerService();
		cmaService.init(args);
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
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandlerService#getConfigPaths()}.
	 * 
	 * @param configs
	 *            The config names to test with
	 */
	@ParameterizedTest
	@MethodSource("names")
	void testGetConfigPaths(String[] configs) {
		CommandLineArgumentHandlerService cmaService = new CommandLineArgumentHandlerService();

		String[] args = new String[configs.length * 2];

		for (int i = 0; i < configs.length; i++) {
			args[i * 2] = "-c";
			args[i * 2 + 1] = configs[i];
		}

		cmaService.init(args);
		List<String> correct = Arrays.asList(configs);
		Assertions.assertEquals(correct, cmaService.getConfigPaths());
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
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandlerService#getConfigPaths()}.
	 * 
	 * @param plugins
	 *            The plugin names to test with
	 */
	@ParameterizedTest
	@MethodSource("names")
	void testGetPluginsPaths(String[] plugins) {
		CommandLineArgumentHandlerService cmaService = new CommandLineArgumentHandlerService();

		String[] args = new String[plugins.length * 2];

		for (int i = 0; i < plugins.length; i++) {
			args[i * 2] = "-p";
			args[i * 2 + 1] = plugins[i];
		}

		cmaService.init(args);
		List<String> correct = Arrays.asList(plugins);
		Assertions.assertEquals(correct, cmaService.getPluginPaths());
	}

}
