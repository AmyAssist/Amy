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

package io.github.amyassist.amy.natlang;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.ServiceLocator;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.natlang.Response;
import io.github.amyassist.amy.natlang.agf.AGFLexer;
import io.github.amyassist.amy.natlang.agf.AGFParser;
import io.github.amyassist.amy.natlang.agf.nodes.AGFNode;
import io.github.amyassist.amy.natlang.agf.nodes.AGFNodeType;
import io.github.amyassist.amy.natlang.agf.nodes.EntityNode;
import io.github.amyassist.amy.natlang.aim.XMLAIMIntent;
import io.github.amyassist.amy.natlang.languagespecifics.ChooseLanguage;
import io.github.amyassist.amy.natlang.nl.*;
import io.github.amyassist.amy.natlang.userinteraction.EntityDataImpl;
import io.github.amyassist.amy.natlang.userinteraction.UserIntent;
import io.github.amyassist.amy.natlang.userinteraction.UserIntentTemplate;

/**
 * The implementation of the NLProcessingManager. This implementation uses the Parsers in the
 * {@link io.github.amyassist.amy.natlang.nl} and the {@link io.github.amyassist.amy.natlang.agf} package.
 *
 * @author Leon Kiefer, Felix Burk
 */
@Service
public class NLProcessingManagerImpl implements NLProcessingManager {

	/**
	 * different possible answers
	 */
	private static final String[] FAILED_TO_UNDERSTAND_ANSWER = { "I did not understand that",
			"Sorry, could you repeat that?", "I don't know what you mean", "No idea what you are talking about",
			"My plugin developers did not teach me this yet" };

	private static final String QUIT_INTENT_USER_INPUT = "(never mind|quit|forget that|nevermind)";

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

	private static final String CONFIG_NAME = "natlang.config";
	private static final String PROPERTY_ENABLE_STEMMER = "enableStemmer";
	private static final String PROBERTY_LANGUAGE = "chooseLanguage";

	private ChooseLanguage language;

	private AGFNode quitIntentUserInputGram;

	private Random rand = new Random();

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
	 * @see io.github.amyassist.amy.natlang.NLProcessingManager#register(java.lang.reflect.Method,
	 *      io.github.amyassist.amy.natlang.aim.XMLAIMIntent)
	 */
	@Override
	public void register(Method method, XMLAIMIntent intent) {
		if (!method.isAnnotationPresent(io.github.amyassist.amy.core.natlang.Intent.class)) {
			throw new IllegalArgumentException("annotation is not present in " + method.getName());
		}

		UserIntentTemplate template = new UserIntentTemplate(method, intent);

		// unfortunately we have to use UserIntent here because entity data has to be present
		UserIntent userIntent = new UserIntent(method, intent);
		this.nodeToMethodAIMPair.put(userIntent.getGrammar(), template);
	}

	/**
	 * @see io.github.amyassist.amy.natlang.NLProcessingManager#processIntent(io.github.amyassist.amy.natlang.Dialog,
	 *      java.lang.String)
	 */
	@Override
	public void processIntent(Dialog dialog, String naturalLanguageText) {
		NLLexer nlLexer = new NLLexer(this.language);
		List<EndToken> tokens = nlLexer.tokenize(naturalLanguageText);

		if (!promptGrammarFound(dialog, tokens)) {
			// try to skip prefixes and suffixes until a grammar matches
			for (int i = 0; i <= tokens.size(); i++) {
				for (int j = tokens.size(); j > i; j--) {
					if (tokens.subList(i, j).size() > 2 && promptGrammarFound(dialog, tokens.subList(i, j))) {
						return;
					}

				}
			}
			this.logger.debug("no matching grammar found");
			dialog.output(Response.text(dialog.getNextPrompt().getOutputText()).build());
		}
	}

	private boolean promptGrammarFound(Dialog dialog, List<EndToken> tokens) {
		List<AGFNode> promptGrams = new ArrayList<>();
		if (dialog.getNextPrompt() != null) {
			promptGrams.add(dialog.getNextPrompt().getGrammar());
		}

		promptGrams.add(this.quitIntentUserInputGram);

		INLParser nlParser = new NLParser(promptGrams, this.language.getStemmer());
		try {
			int matchingNodeIndex = nlParser.matchingNodeIndex(tokens);

			if (matchingNodeIndex == promptGrams.indexOf(this.quitIntentUserInputGram)) {
				dialog.output(Response.text(generateRandomAnswer(QUIT_INTENT_ANSWER)).build());
				dialog.setIntent(null);
				return true;
			}
			setEntities(promptGrams.get(matchingNodeIndex), dialog);
		} catch (NLParserException e) {
			this.logger.debug("grammar not directly regocnized - skipping pre and suffixes {}", e.getMessage());
			return false;
		}
		return true;

	}

	private void setEntities(AGFNode gram, Dialog dialog) {
		Map<String, String> entityIdToUserContent = getEntityContent(gram);

		for (Entry<String, String> entry : entityIdToUserContent.entrySet()) {
			EntityDataImpl data = new EntityDataImpl(entry.getValue(), this.language.getTimeUtility());
			dialog.getIntent().getEntityList().get(entry.getKey()).setEntityData(data);
		}

		if (!dialog.getIntent().isFinished()) {
			dialog.setNextPrompt(dialog.getIntent().getNextPrompt());
			dialog.output(Response.text(dialog.getNextPrompt().getOutputText()).build());
		}
	}

	/**
	 * helper method to extract entity contents
	 * 
	 * @param node
	 *            to extract entity content from
	 * @return Map which is mapping the entity id to the user provided content
	 */
	private Map<String, String> getEntityContent(AGFNode node) {
		Map<String, String> result = new HashMap<>();

		for (EntityNode child : node.getChildEntityNodes()) {
			if (child.getType() == AGFNodeType.ENTITY && child.getUserProvidedContent() != null) {
				result.put(child.getContent(), child.getUserProvidedContent());
			}
		}

		return result;
	}

	/**
	 * @see io.github.amyassist.amy.natlang.NLProcessingManager#decideIntent(io.github.amyassist.amy.natlang.Dialog,
	 *      java.lang.String)
	 */
	@Override
	public Dialog decideIntent(Dialog dialog, String naturalLanguageText) {
		NLLexer nlLexer = new NLLexer(this.language);
		List<EndToken> tokens = nlLexer.tokenize(naturalLanguageText);

		if (intentFound(dialog, tokens)) {
			return dialog;
		}

		// try to skip prefixes and suffixes until a grammar matches
		for (int i = 0; i <= tokens.size(); i++) {
			for (int j = tokens.size(); j > i; j--) {
				if (tokens.subList(i, j).size() > 2 && intentFound(dialog, tokens.subList(i, j))) {
					return dialog;
				}

			}
		}

		this.logger.debug("no matching grammar found");
		dialog.output(Response.text(generateRandomAnswer(FAILED_TO_UNDERSTAND_ANSWER)).build());
		return dialog;

	}

	private boolean intentFound(Dialog dialog, List<EndToken> tokens) {
		INLParser nlParser = new NLParser(new ArrayList<>(this.nodeToMethodAIMPair.keySet()),
				this.language.getStemmer());
		AGFNode node;
		try {
			node = nlParser.matchingNode(tokens);

		} catch (NLParserException e) {
			this.logger.debug("grammar not directly regocnized - skipping pre and suffixes {}", e.getMessage());
			return false;
		}

		Method left = this.nodeToMethodAIMPair.get(node).getMethod();
		XMLAIMIntent right = this.nodeToMethodAIMPair.get(node).getXml();
		UserIntent userIntent = new UserIntent(left, right);

		Object object = this.serviceLocator.createAndInitialize(userIntent.getPartialNLIClass());
		userIntent.updateGrammars(object);
		dialog.setIntent(userIntent);

		setEntities(node, dialog);
		return true;
	}

	private String generateRandomAnswer(String[] strings) {
		int rndm = this.rand.nextInt(strings.length);
		return strings[rndm];
	}

	/**
	 * @see io.github.amyassist.amy.natlang.NLProcessingManager#getPossibleGrammars()
	 */
	@Override
	public List<AGFNode> getPossibleGrammars() {
		List<AGFNode> list = new ArrayList<>();
		list.addAll(this.nodeToMethodAIMPair.keySet());
		return list;
	}
}
