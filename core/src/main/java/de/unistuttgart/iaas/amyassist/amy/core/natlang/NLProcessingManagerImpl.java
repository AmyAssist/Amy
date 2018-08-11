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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.AIMIntent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.ChooseLanguage;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.INLParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLParserException;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.WordToken;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Constants;

/**
 * The implementation of the NLProcessingManager. This implementation uses the Parsers in the
 * {@link de.unistuttgart.iaas.amyassist.amy.core.natlang.nl} and the
 * {@link de.unistuttgart.iaas.amyassist.amy.core.natlang.agf} package.
 * 
 * @author Leon Kiefer, Felix Burk
 */
@Service
public class NLProcessingManagerImpl implements NLProcessingManager {

	@Reference
	private Logger logger;
	@Reference
	private Environment environment;

	@Reference
	private ServiceLocator serviceLocator;

	private final List<PartialNLI> register = new ArrayList<>();

	private final List<AGFNode> registeredNodeList = new ArrayList<>();

	@Reference
	private ConfigurationManager configurationLoader;
	private static final String CONFIG_NAME = "core.config";
	private static final String PROPERTY_ENABLE_STEMMER = "enableStemmer";
	private static final String PROBERTY_LANGUAGE = "chooseLanguage";
	private boolean stemmerEnabled;
	private String languageString;

	@PostConstruct
	private void setup() {
		this.stemmerEnabled = Boolean.parseBoolean(this.configurationLoader.getConfigurationWithDefaults(CONFIG_NAME)
				.getProperty(PROPERTY_ENABLE_STEMMER, "true"));
		this.languageString = this.configurationLoader.getConfigurationWithDefaults(CONFIG_NAME)
				.getProperty(PROBERTY_LANGUAGE, "EN");
	}

	@Override
	@Deprecated
	public void register(Class<?> naturalLanguageInterpreter) {
		if (!naturalLanguageInterpreter
				.isAnnotationPresent(de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand.class)) {
			throw new IllegalArgumentException(
					"annotation is not present in " + naturalLanguageInterpreter.getSimpleName());
		}
		Set<Method> grammars = NLIAnnotationReader.getValidNLIMethods(naturalLanguageInterpreter);

		for (Method e : grammars) {
			PartialNLI partialNLI = this.generatePartialNLI(naturalLanguageInterpreter, e);
			this.register.add(partialNLI);
			this.registeredNodeList.add(partialNLI.getGrammar());
		}
	}
	
	@Override
	public void register(Method method, AIMIntent intent) {
		if (!method
				.isAnnotationPresent(de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Intent.class)) {
			throw new IllegalArgumentException(
					"annotation is not present in " + method.getName());
		}

		PartialNLI partialNLI = this.generatePartialNLI(method, intent);
		this.register.add(partialNLI);
		this.registeredNodeList.add(partialNLI.getGrammar());
	}

	@Deprecated
	private PartialNLI generatePartialNLI(Class<?> natuaralLanguageInterpreter, Method method) {
		String grammar = method.getAnnotation(Grammar.class).value();
		
		//TODO quick workaround for legacy # REMOVE THIS
		grammar = grammar.replaceAll("#", "{test}");
		
		AGFParser agfParser = new AGFParser(new AGFLexer(grammar));
		AGFNode parseWholeExpression = agfParser.parseWholeExpression();
		return new PartialNLI(method, parseWholeExpression, natuaralLanguageInterpreter);
	}
	
	private PartialNLI generatePartialNLI(Method method, AIMIntent intent) {
		String grammar = intent.getGram();
		
		AGFParser agfParser = new AGFParser(new AGFLexer(grammar));
		AGFNode parseWholeExpression = agfParser.parseWholeExpression();
		
		return new PartialNLI(method, parseWholeExpression, method.getClass());
	}

	@Override
	public String getGrammarFileString(String grammarName) {
		JSGFGenerator generator = new JSGFGenerator(grammarName, Constants.MULTI_CALL_START,
				Constants.SINGLE_CALL_START, Constants.MULTI_CALL_STOP, Constants.SHUT_UP);
		
		for (PartialNLI partialNLI : this.register) {
			this.logger.error("registered grammar {}", partialNLI.getGrammar());
			generator.addRule(partialNLI.getGrammar(), UUID.randomUUID().toString());
		}

		return generator.generateGrammarFileString();
	}

	@Override
	public String process(String naturalLanguageText) {
		ChooseLanguage language = new ChooseLanguage(this.languageString);
		this.logger.debug("input {}", naturalLanguageText);
		NLLexer nlLexer = new NLLexer(language.getNumberConversion());
		List<WordToken> tokens = nlLexer.tokenize(naturalLanguageText);
		INLParser nlParser = new NLParser(this.registeredNodeList, language.getStemmer(), this.stemmerEnabled);
		try {
			int matchingNodeIndex = nlParser.matchingNodeIndex(tokens);
			PartialNLI partialNLI = this.register.get(matchingNodeIndex);
			String[] arguments = Lists.transform(tokens, WordToken::getContent).toArray(new String[tokens.size()]);
			Object object = this.serviceLocator.createAndInitialize(partialNLI.getPartialNLIClass());
			return partialNLI.call(object, arguments);
		}catch(NLParserException e) {
			return "I did not understand that";
		}
		
	}
}
