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

package io.github.amyassist.amy.core.io;

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

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.information.ProgramInformation;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;
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

	private CommandLineArgumentHandlerService cmaService;

	/**
	 * Setup before each test
	 */
	@BeforeEach
	void setup() {
		this.pi = this.testFramework.mockService(ProgramInformation.class);
		Mockito.when(this.pi.getLicenseNotice()).thenReturn("notice");
		Mockito.when(this.pi.getVersion()).thenReturn("version");
		this.cmaService = this.testFramework.setServiceUnderTest(CommandLineArgumentHandlerService.class);
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.core.io.CommandLineArgumentHandlerService#shouldProgramContinue()}.
	 * 
	 * @param args
	 *            The cma args to test with
	 */
	@ParameterizedTest
	@MethodSource("stopFlags")
	void testShouldProgramContinue(String[] args) {
		this.cmaService.load(args, this.logger::info);
		Assertions.assertFalse(this.cmaService.shouldProgramContinue(), "Program should not continue");
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
	 * {@link io.github.amyassist.amy.core.io.CommandLineArgumentHandlerService#shouldProgramContinue()}.
	 * 
	 * @param args
	 *            The cma args to test with
	 */
	@ParameterizedTest
	@MethodSource("invalidFlags")
	void testShouldProgramContinueWithInvalidFlags(String[] args) {
		this.cmaService.load(args, this.logger::info);
		Assertions.assertFalse(this.cmaService.shouldProgramContinue(), "Program should not continue");
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
	 * Test method for {@link io.github.amyassist.amy.core.io.CommandLineArgumentHandlerService#getInfo()}.
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

		this.cmaService.load(args, this.logger::info);
		Assertions.assertTrue(this.cmaService.shouldProgramContinue());
		List<String> correct = Arrays.asList(configs);
		Assertions.assertEquals(correct, this.cmaService.getInfo().getConfigPaths());
	}

	/**
	 * Tests that a duplicate flag get handeled correctly.
	 */
	@Test
	void testDuplicateFlag() {
		String[] args = new String[2];
		args[0] = "-h";
		args[1] = "-h";
		this.cmaService.load(args, this.logger::info);
		Assertions.assertFalse(this.cmaService.shouldProgramContinue());
	}

	/**
	 * Tests that duplicate load is not allowed.
	 */
	@Test
	void testDuplicateLoad() {
		String args[] = new String[0];
		this.cmaService.load(args, this.logger::info);
		Assertions.assertThrows(IllegalStateException.class, () -> this.cmaService.load(args, this.logger::info));
	}

	/**
	 * Tests that using getInfo() before load is not allowed.
	 */
	@Test
	void testGetInfoBeforeLoad() {
		Assertions.assertThrows(IllegalStateException.class, () -> this.cmaService.getInfo());
	}

	/**
	 * Tests that using shouldProgramContinue() before load is not allowed.
	 */
	@Test
	void testShouldProgramContinueBeforeLoad() {
		Assertions.assertThrows(IllegalStateException.class, () -> this.cmaService.shouldProgramContinue());
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

}
