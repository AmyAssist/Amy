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

package de.unistuttgart.iaas.amyassist.amy.core.speech;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Tests for the SpeechCammandHandler
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
class SpeechCommandHandlerTest {
	@Reference
	private TestFramework framework;
	private SpeechCommandHandler speechCammandHandler;
	private Path tempDir;

	@BeforeEach
	public void setup() throws IOException {
		this.speechCammandHandler = this.framework.setServiceUnderTest(SpeechCommandHandler.class);
		Environment environment = this.framework.setServiceUnderTest(Environment.class);

		this.tempDir = Files.createTempDirectory(SpeechCommandHandlerTest.class.getName());
		this.tempDir.toFile().deleteOnExit();
		Mockito.when(environment.getWorkingDirectory()).thenReturn(this.tempDir);
	}

	@Test
	void test() {
		this.speechCammandHandler.registerSpeechCommand(TestSpeechCommand.class);
		this.speechCammandHandler.completeSetup();

		String result = this.speechCammandHandler.handleSpeechInput("testkeyword simple 10");
		assertThat(result, equalTo("10"));
		// assertThat(result, equalTo("10"));
	}

	@Test
	void testUnknownKeyword() {
		this.speechCammandHandler.registerSpeechCommand(TestSpeechCommand.class);
		this.speechCammandHandler.completeSetup();

		assertThrows(IllegalArgumentException.class,
				() -> this.speechCammandHandler.handleSpeechInput("unknownKeyword simple 10"));
	}

	@AfterEach
	void cleanUp() throws IOException {
		Files.walk(this.tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}

}
