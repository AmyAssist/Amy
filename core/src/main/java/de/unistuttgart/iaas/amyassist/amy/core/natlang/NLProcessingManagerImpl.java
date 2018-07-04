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

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFToken;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLLexer;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.Constants;

/**
 * The implementation of the NLProcessingManager. This implementation uses the Parsers in the
 * {@link de.unistuttgart.iaas.amyassist.amy.core.natlang.nl} and the
 * {@link de.unistuttgart.iaas.amyassist.amy.core.natlang.agf} package.
 * 
 * @author Leon Kiefer
 */
@Service
public class NLProcessingManagerImpl implements NLProcessingManager {

	@Reference
	private Logger logger;
	@Reference
	private Environment environment;

	private final List<PartialNLI> register = new ArrayList<>();

	@Override
	public void register(Class<?> natuaralLanguageInterpreter) {
		if (!natuaralLanguageInterpreter
				.isAnnotationPresent(de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand.class)) {
			throw new IllegalArgumentException();
		}
		String[] speechKeyword = NLIAnnotationReader.getSpeechKeywords(natuaralLanguageInterpreter);
		Set<Method> grammars = NLIAnnotationReader.getValidNLIMethods(natuaralLanguageInterpreter);

		for (Method e : grammars) {
			PartialNLI partialNLI = this.generatePartialNLI(natuaralLanguageInterpreter, e);
			this.register.add(partialNLI);
		}
	}

	private PartialNLI generatePartialNLI(Class<?> natuaralLanguageInterpreter, Method method) {
		String grammar = method.getAnnotation(Grammar.class).value();
		AGFParser agfParser = new AGFParser(new AGFLexer(grammar));
		AGFNode parseWholeExpression = agfParser.parseWholeExpression();
		return new PartialNLI(method, parseWholeExpression, natuaralLanguageInterpreter);
	}

	public String getGrammarFileString() {
		JSGFGenerator generator = new JSGFGenerator("grammar", Constants.WAKE_UP, Constants.GO_SLEEP,
				Constants.SHUT_UP);
		for (PartialNLI partialNLI : this.register) {
			generator.addRule(partialNLI.getGrammar(), UUID.randomUUID().toString());
		}

		return generator.generateGrammarFileString();
	}

	/**
	 * Call this after all register and before process
	 */
	public void completeSetup() {
		Path resolve = this.environment.getWorkingDirectory().resolve("resources")
				.resolve("sphinx-grammars/grammar.gram");

		try {
			Files.createDirectories(resolve.getParent());
		} catch (IOException e) {
			throw new IllegalStateException("Can't create parent directories of the grammar file", e);
		}
		try (BufferedWriter bw = Files.newBufferedWriter(resolve, StandardOpenOption.CREATE)) {
			bw.write(this.getGrammarFileString());
		} catch (IOException e) {
			throw new IllegalStateException("Can't write grammar file", e);
		}
	}

	@Override
	public String process(String naturalLanguageText) {
		this.logger.debug("input {}", naturalLanguageText);
		NLLexer nlLexer = new NLLexer(naturalLanguageText);
		// TODO parser

		return "";
	}

}
