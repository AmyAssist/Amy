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

package de.unistuttgart.iaas.amyassist.amy.core.speech.grammar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test for the GrammarCreator
 * 
 * @author Kai Menzel
 */
@ExtendWith(FrameworkExtension.class)
public class GrammarCreatorTest {

	@Reference
	private TestFramework framework;

	private GrammarObjectsCreator objectsCreator;

	private String grammarName;
	private Path grammarFile;

	private Path tempDir;

	@BeforeEach
	public void setup() throws IOException {
		/*this.grammarName = "grammar";

		Environment environment = this.framework.mockService(Environment.class);
		NLProcessingManager nlProcessingManager = this.framework.mockService(NLProcessingManager.class);
		Mockito.when(nlProcessingManager.getGrammarFileString(this.grammarName)).thenReturn("");
		this.tempDir = Files.createTempDirectory(GrammarCreatorTest.class.getName());
		this.tempDir.toFile().deleteOnExit();
		Mockito.when(environment.getWorkingDirectory()).thenReturn(this.tempDir);

		this.grammarFile = this.tempDir.resolve("resources").resolve("sphinx-grammars/grammar.gram");

		this.objectsCreator = this.framework.setServiceUnderTest(GrammarObjectsCreator.class);*/
	}

	@Test
	public void test() {
		/*this.objectsCreator.deploy();
		assertThat(this.objectsCreator.getMainGrammar().getName(), equalTo(this.grammarName));
		assertThat(this.objectsCreator.getMainGrammar().getFile(), equalTo(this.grammarFile.toFile()));
		assertThat(this.objectsCreator.getSwitchableGrammars().isEmpty(), equalTo(true));*/
	}

	@AfterEach
	void cleanUp() throws IOException {
		//Files.walk(this.tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}
}
