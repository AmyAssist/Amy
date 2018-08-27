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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNodeType;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.EntityNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLAIMIntent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.ChooseLanguage;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.INLParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLParserException;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.WordToken;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.userinteraction.EntityDataImpl;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.userinteraction.UserIntent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.userinteraction.UserIntentTemplate;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Constants;

/**
 * The implementation of the NLProcessingManager. This implementation uses the Parsers in the
 * {@link de.unistuttgart.iaas.amyassist.amy.core.natlang.nl} and the
 * {@link de.unistuttgart.iaas.amyassist.amy.core.natlang.agf} package.
 *
 * @author Leon Kiefer, Felix Burk
 */
@Service()
public class NLProcessingManagerImpl implements NLProcessingManager {

	/**
	 * different possible answers
	 */
	private static final String[] FAILED_TO_UNDERSTAND_ANSWER = { "I did not understand that",
			"Sorry, could you repeat that?",
			"I don't know what you mean", "No idea what you are talking about",
			"My plugin developers did not teach me this yet" };

	private static final String QUIT_INTENT_USER_INPUT = "(never mind|quit|forget that)";

	private static final String[] QUIT_INTENT_ANSWER = { "ok", "sure", "what else can i do for you?" };

	@Reference
	private Logger logger;

	@Reference
	private Environment environment;

	@Reference
	private ServiceLocator serviceLocator;

	private Map<AGFNode, UserIntentTemplate> nodeToMethodAIMPair = new HashMap<>();

	@Reference
	private ConfigurationManager configurationLoader;

	private static final String CONFIG_NAME = "core.config";
	private static final String PROPERTY_ENABLE_STEMMER = "enableStemmer";
	private static final String PROBERTY_LANGUAGE = "chooseLanguage";

	private ChooseLanguage language;

	private AGFNode quitIntentUserInputGram;

	@PostConstruct
	private void setup() {
		boolean stemmerEnabled = Boolean.parseBoolean(this.configurationLoader.getConfigurationWithDefaults(CONFIG_NAME)
				.getProperty(PROPERTY_ENABLE_STEMMER, "true"));
		String languageString = this.configurationLoader.getConfigurationWithDefaults(CONFIG_NAME)
				.getProperty(PROBERTY_LANGUAGE, "EN");
		this.language = new ChooseLanguage(languageString, stemmerEnabled);

		AGFLexer lex = new AGFLexer(QUIT_INTENT_USER_INPUT);
		AGFParser parser = new AGFParser(lex);
		this.quitIntentUserInputGram = parser.parseWholeExpression();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager#register(java.lang.reflect.Method,
	 *      de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLAIMIntent)
	 */
	@Override
	public void register(Method method, XMLAIMIntent intent) {
		if (!method.isAnnotationPresent(de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent.class)) {
			throw new IllegalArgumentException("annotation is not present in " + method.getName());
		}

		UserIntentTemplate template = new UserIntentTemplate(method, intent);
		
		//unfortunately we have to use UserIntent here because enity data has to be present 
		UserIntent userIntent = new UserIntent(method, intent);
		this.nodeToMethodAIMPair.put(userIntent.getGrammar(), template);
	}

	/**
	 *
	 * currently not working!
	 *
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager#getGrammarFileString(java.lang.String)
	 */
	@Override
	public String getGrammarFileString(String grammarName) {
		JSGFGenerator generator = new JSGFGenerator(grammarName, Constants.MULTI_CALL_START,
				Constants.SINGLE_CALL_START, Constants.MULTI_CALL_STOP, Constants.SHUT_UP);

		return generator.generateGrammarFileString();
	}

	@Override
	public void processIntent(Dialog dialog, String naturalLanguageText) {
		List<AGFNode> promptGrams = new ArrayList<>();
			if (dialog.getNextPrompt() != null) {
				promptGrams.add(dialog.getNextPrompt().getGrammar());
			}

		promptGrams.add(this.quitIntentUserInputGram);

		NLLexer nlLexer = new NLLexer(this.language);
		List<WordToken> tokens = nlLexer.tokenize(naturalLanguageText);
		INLParser nlParser = new NLParser(promptGrams, this.language.getStemmer());
		try {
			int matchingNodeIndex = nlParser.matchingNodeIndex(tokens);

			if (matchingNodeIndex == promptGrams.indexOf(this.quitIntentUserInputGram)) {
				dialog.output(generateRandomAnswer(QUIT_INTENT_ANSWER));
				dialog.setIntent(null);
				return;
			}

			Map<String, String> entityIdToUserContent = getEntityContent(promptGrams.get(matchingNodeIndex));

			for (Entry<String, String> entry : entityIdToUserContent.entrySet()) {
				EntityDataImpl data = new EntityDataImpl(entry.getValue(), this.language.getTimeUtility());
				dialog.getIntent().getEntityList().get(entry.getKey()).setEntityData(data);
			}

			if (!dialog.getIntent().isFinished()) {
				dialog.setNextPrompt(dialog.getIntent().getNextPrompt());
				dialog.output(dialog.getNextPrompt().getOutputText());
			}
		} catch (NLParserException e) {
			this.logger.debug("no matching grammar found ", e);
			dialog.output(generateRandomAnswer(FAILED_TO_UNDERSTAND_ANSWER));
		}

	}

	/**
	 * helper method to extract entity contents
	 * 
	 * @param node
	 *                 to extract entity content from
	 * @return Map which is mapping the entity id to the user provided content
	 */
	private Map<String, String> getEntityContent(AGFNode node) {
		Map<String, String> result = new HashMap<>();

		for (AGFNode child : node.getChilds()) {
			if (child.getType() == AGFNodeType.ENTITY) {
				EntityNode entity = (EntityNode) child;
				if (entity.getUserProvidedContent() != null) {
					result.put(entity.getContent(), entity.getUserProvidedContent());
				}
			} else {
				result.putAll(getEntityContent(child));
			}
		}

		return result;
	}

	/**
	 *
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager#decideIntent(de.unistuttgart.iaas.amyassist.amy.core.natlang.Dialog,
	 *      java.lang.String)
	 */
	@Override
	public void decideIntent(Dialog dialog, String naturalLanguageText) {
		NLLexer nlLexer = new NLLexer(this.language);
		List<WordToken> tokens = nlLexer.tokenize(naturalLanguageText);
		INLParser nlParser = new NLParser(new ArrayList<>(this.nodeToMethodAIMPair.keySet()),
				this.language.getStemmer());
		try {
			AGFNode node = nlParser.matchingNode(tokens);
			Method left = this.nodeToMethodAIMPair.get(node).getMethod();
			XMLAIMIntent right = this.nodeToMethodAIMPair.get(node).getXml();
			UserIntent userIntent = new UserIntent(left, right);
			dialog.setIntent(userIntent);

			Map<String, String> entityIdToUserContent = getEntityContent(node);
			for (Entry<String, String> entry : entityIdToUserContent.entrySet()) {
				EntityDataImpl data = new EntityDataImpl(entry.getValue(), this.language.getTimeUtility());
				dialog.getIntent().getEntityList().get(entry.getKey()).setEntityData(data);
			}

			if (!dialog.getIntent().isFinished()) {
				dialog.setNextPrompt(dialog.getIntent().getNextPrompt());
				dialog.output(dialog.getNextPrompt().getOutputText());
			}
		} catch (NLParserException e) {
			this.logger.debug("no matching grammar found ", e);
			dialog.output(generateRandomAnswer(FAILED_TO_UNDERSTAND_ANSWER));
		}

	}

	private String generateRandomAnswer(String[] strings) {
		Random rand = new Random();
		int rndm = rand.nextInt(strings.length);
		return strings[rndm];
	}
}
