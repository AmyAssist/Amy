/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core.speech;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Tests for the SpeechCammandHandler
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtention.class)
class SpeechCommandHandlerTest {
	@Reference
	private TestFramework framework;
	private SpeechCommandHandler speechCammandHandler;

	@BeforeEach
	public void setup() {
		this.speechCammandHandler = this.framework.setServiceUnderTest(SpeechCommandHandler.class);
	}

	@Test
	void test() {
		this.speechCammandHandler.registerSpeechCommand(TestSpeechCommand.class);
		this.speechCammandHandler.completeSetup();

		String result = this.speechCammandHandler.handleSpeechInput("testkeyword simple 10");

		assertThat(result, equalTo("testkeyword simple 10"));
		// assertThat(result, equalTo("10"));
	}

	@Test
	void testUnknownKeyword() {
		this.speechCammandHandler.registerSpeechCommand(TestSpeechCommand.class);
		this.speechCammandHandler.completeSetup();

		assertThrows(IllegalArgumentException.class,
				() -> this.speechCammandHandler.handleSpeechInput("unknownKeyword simple 10"));
	}

}
