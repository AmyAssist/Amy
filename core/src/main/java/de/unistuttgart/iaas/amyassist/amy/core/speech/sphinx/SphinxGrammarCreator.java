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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Constants;

/**
 * Class that Creates all Grammar Objects sets the MainGrammar and the List of all other Grammars
 * 
 * @author Kai Menzel, Tim Neumann
 */

@Service(SphinxGrammarCreator.class)
public class SphinxGrammarCreator {

	@Reference
	private Environment environment;

	private Path grammarDir;

	@PostConstruct
	private void init() {
		this.grammarDir = this.environment.getWorkingDirectory().resolve("resources").resolve("sphinx-grammars");

		// Create directories if necessary
		try {
			Files.createDirectories(this.grammarDir);
		} catch (IOException e) {
			throw new IllegalStateException("Can't create parent directories of the grammar file", e);
		}
	}

	/**
	 * Create a grammar file with the given name and the given string as content
	 * 
	 * @param name
	 *            The name of the file
	 */
	public void createGrammar(String name) {
		Path grammarFile = this.grammarDir.resolve(name + ".gram");

		try (BufferedWriter bw = Files.newBufferedWriter(grammarFile, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			bw.write(generateGrammarString(name, Constants.MULTI_CALL_START, Constants.SINGLE_CALL_START,
					Constants.MULTI_CALL_STOP, Constants.SHUT_UP));
		} catch (IOException e) {
			throw new IllegalStateException("Can't write grammar file", e);
		}
	}

	private String generateGrammarString(String name, String multiCallStart, String singleCallStart,
			String multiCallStop, String voiceOutputStopCommand) {
		StringBuilder grammar = new StringBuilder();

		grammar.append("#JSGF V1.0;\n" + "\n" + "/**\n" + " * JSGF Grammar \n" + " */\n" + "\n");

		grammar.append("grammar " + name + ";\n");
		publicRule(grammar, "wakeup", multiCallStart);
		publicRule(grammar, "singlewakeup", singleCallStart);
		publicRule(grammar, "sleep", multiCallStop);
		publicRule(grammar, "shutdown", voiceOutputStopCommand);

		return grammar.toString();
	}

	private void publicRule(StringBuilder builder, String ruleName, String rule) {
		builder.append("public <").append(ruleName).append("> = ( ").append(rule).append(" );\n");
	}

	/**
	 * Get's directory for grammars.
	 * 
	 * @return grammar directory
	 */
	public Path getGrammarDirectory() {
		return this.grammarDir;
	}
}
