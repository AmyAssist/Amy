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

package de.unistuttgart.iaas.amyassist.amy.core.console;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.Core;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * TODO: Description
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
class ConsoleTest {

	@Reference
	private TestFramework testFramework;
	private Properties properties;

	@BeforeEach
	void init() {
		this.testFramework.mockService(Core.class);
		ConfigurationManager configurationManager = this.testFramework.mockService(ConfigurationManager.class);
		this.properties = new Properties();
		Mockito.when(configurationManager.getConfigurationWithDefaults("core.config")).thenReturn(this.properties);
	}

	@Test
	void test() {
		final String[] testInput = { "Hello", "world", "say", "hello" };
		final String expected = "hello1";

		SpeechInputHandler handler = this.testFramework.mockService(SpeechInputHandler.class);

		CompletableFuture<String> completableFuture = new CompletableFuture<>();
		completableFuture.complete(expected);
		Mockito.when(handler.handle("Hello world say hello")).thenReturn(completableFuture);

		Console console = this.testFramework.setServiceUnderTest(Console.class);

		assertThat(console.say(testInput), equalTo(expected));
	}
}
