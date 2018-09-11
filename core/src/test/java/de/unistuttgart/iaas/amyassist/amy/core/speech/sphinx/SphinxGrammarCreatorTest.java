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

package de.unistuttgart.iaas.amyassist.amy.core.speech.sphinx;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Tests the {@link SphinxGrammarCreator}
 * 
 * @author Tim Neumann
 */
@ExtendWith(FrameworkExtension.class)

class SphinxGrammarCreatorTest {
	@Reference
	private TestFramework testFramework;
	private Path tempDir;
	private SphinxGrammarCreator grammarCreator;

	/**
	 * @throws java.lang.Exception
	 *             When an error occurs
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.tempDir = Files.createTempDirectory(SphinxGrammarCreatorTest.class.getName());
		this.tempDir.toFile().deleteOnExit();
		Environment environment = this.testFramework.mockService(Environment.class);
		Mockito.when(environment.getWorkingDirectory()).thenReturn(this.tempDir);
		this.grammarCreator = this.testFramework.setServiceUnderTest(SphinxGrammarCreator.class);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.speech.sphinx.SphinxGrammarCreator#createGrammar(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * 
	 * @throws IOException
	 *             When an IO error occurs
	 */
	@Test
	void testCreateGrammar() throws IOException {
		this.grammarCreator.createGrammar("testName", "keyword1", "keyword2", "keyword3", "keyword4");
		Path grammarDir = this.grammarCreator.getGrammarDirectory();
		Path grammarFile = grammarDir.resolve("testName.gram");
		Assertions.assertTrue(Files.exists(grammarFile), "Grammar file should have been created,");
		String fileContent = new String(Files.readAllBytes(grammarFile), StandardCharsets.UTF_8);
		assertThat("Grammer file should start with the specified string.", fileContent, startsWith("#JSGF V1.0;"));
		assertThat("Grammer file should contain line with grammar name.", fileContent,
				containsString("grammar testName;\n"));
		assertThat("Grammer file should contain line with keyword 1.", fileContent,
				containsString("> = ( keyword1 );\n"));
		assertThat("Grammer file should contain line with keyword 2.", fileContent,
				containsString("> = ( keyword2 );\n"));
		assertThat("Grammer file should contain line with keyword 3.", fileContent,
				containsString("> = ( keyword3 );\n"));
		assertThat("Grammer file should contain line with keyword 4.", fileContent,
				containsString("> = ( keyword4 );\n"));
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
