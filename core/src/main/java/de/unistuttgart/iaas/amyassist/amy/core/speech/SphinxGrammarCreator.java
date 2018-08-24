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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService;

/**
 * Class that Creates all Grammar Objects sets the MainGrammar and the List of all other Grammars
 * 
 * @author Kai Menzel
 */

@Service(SphinxGrammarCreator.class)
public class SphinxGrammarCreator implements DeploymentContainerService {

	@Reference
	private Logger logger;
	@Reference
	private Environment environment;
	@Reference
	private NLProcessingManager nlProcessingManager;
	@Reference
	private LocalSpeechInterpreter localSpeechInterpreter;
	@Reference
	private SphinxSpeechRecognizer sphinxSTT;

	private String mainGrammarName = "mainGrammar";

	/**
	 * Call this after all register and before process
	 */
	@Override
	public void deploy() {

		if (this.localSpeechInterpreter.isRecognitionEnabled()) {
			Path grammarFolder = this.environment.getWorkingDirectory().resolve("resources").resolve("sphinx-grammars");
			Path mainGrammarFile = grammarFolder.resolve(this.mainGrammarName + ".gram");

			// Create mainGrammar.gram
			try {
				Files.createDirectories(grammarFolder);
			} catch (IOException e) {
				throw new IllegalStateException("Can't create parent directories of the grammar file", e);
			}

			try (BufferedWriter bw = Files.newBufferedWriter(mainGrammarFile, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING)) {
				bw.write(this.nlProcessingManager.getGrammarFileString(this.mainGrammarName));
			} catch (IOException e) {
				throw new IllegalStateException("Can't write grammar file", e);
			}

			this.sphinxSTT.setGrammar(mainGrammarFile.toFile());
		}
	}

}
