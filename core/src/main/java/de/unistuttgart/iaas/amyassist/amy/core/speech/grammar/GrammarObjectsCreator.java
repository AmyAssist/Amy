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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager;

/**
 * Class that Creates all Grammar Objects sets the MainGrammar and the List of all other Grammars
 * 
 * @author Kai Menzel
 */
@Service
public class GrammarObjectsCreator {

	@Reference
	private Environment environment;
	@Reference
	private NLProcessingManager nlProcessingManager;

	private Grammar mainGrammar;
	private List<Grammar> switchables = new ArrayList<>();

	/**
	 * Call this after all register and before process
	 */
	public void completeSetup() {
		Path grammarFile = this.environment.getWorkingDirectory().resolve("resources")
				.resolve("sphinx-grammars/grammar.gram");

		try {
			Files.createDirectories(grammarFile.getParent());
		} catch (IOException e) {
			throw new IllegalStateException("Can't create parent directories of the grammar file", e);
		}
		try (BufferedWriter bw = Files.newBufferedWriter(grammarFile, StandardOpenOption.CREATE)) {
			bw.write(this.nlProcessingManager.getGrammarFileString("grammar"));
		} catch (IOException e) {
			throw new IllegalStateException("Can't write grammar file", e);
		}
		this.mainGrammar = new Grammar("grammar", grammarFile);
	}

	/**
	 * @return the MainGrammar of the Recognition System
	 */
	public Grammar getMainGrammar() {
		return this.mainGrammar;
	}

	/**
	 * @return List of the Switchable Grammars
	 */
	public List<Grammar> getSwitchableGrammars() {
		return this.switchables;
	}

}
