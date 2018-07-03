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

package de.unistuttgart.iaas.amyassist.amy.core.grammar;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * TODO: Description
 * 
 * @author Kai Menzel
 */
@Service(GrammarObjectCreator.class)
public class GrammarObjectCreator implements GrammarObjectCreatorInterface {

	@Reference
	private Environment environment;

	private Path grammarFile;

	private Grammar mainGrammar;
	private Map<String, Grammar> switchables;

	@PostConstruct
	private void init() {
		this.switchables = new HashMap<>();
		this.grammarFile = this.environment.getWorkingDirectory().resolve("resources")
				.resolve("sphinx-grammars/grammar.gram");
		this.mainGrammar = new Grammar("grammar", this.grammarFile.toFile());

	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.grammar.GrammarObjectCreatorInterface#getMainGrammar()
	 */
	@Override
	public Grammar getMainGrammar() {
		return this.mainGrammar;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.grammar.GrammarObjectCreatorInterface#getSwitchableGrammars()
	 */
	@Override
	public Map<String, Grammar> getSwitchableGrammars() {
		return this.switchables;
	}

}
