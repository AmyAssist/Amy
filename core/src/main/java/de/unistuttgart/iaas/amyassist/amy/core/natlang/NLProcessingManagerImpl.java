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
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNodeType;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.EntityNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.AIMIntent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.ChooseLanguage;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.INLParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.NLParserException;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.WordToken;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.userInteraction.Entity;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.userInteraction.UserIntent;
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
	
	/**
	 * different possible answers 
	 */
	private final String[] failedToUnderstandAnswer = {
			"I did not understand that", "Sorry, could you repeat that?", 
			"I don't know what you mean", "No idea what you are talking about", "My plugin developers did not teach me this yet"
	};
	
	private final String quitIntentUserInput = "(never mind|quit|forget that)";
	
	private final String[] quitIntentAnswer = {
		"ok", "sure", "what else can i do for you?"	
	};

	@Reference
	private Logger logger;
	
	@Reference
	private Environment environment;
		
	private final Map<AGFNode, Pair<Method, AIMIntent>> nodeToMethodAIMPair = new HashMap<>();
 
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
		
		AGFLexer lex = new AGFLexer(this.quitIntentUserInput);
		AGFParser parser = new AGFParser(lex);
		this.quitIntentUserInputGram = parser.parseWholeExpression();
	}
	
	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager#register(java.lang.reflect.Method, de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.AIMIntent)
	 */
	@Override
	public void register(Method method, AIMIntent intent) {
		if (!method
				.isAnnotationPresent(de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Intent.class)) {
			throw new IllegalArgumentException(
					"annotation is not present in " + method.getName());
		}

		UserIntent  userIntent = new UserIntent(method, intent);
		this.nodeToMethodAIMPair.put(userIntent.getGrammar(), Pair.of(method, intent));
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
	public void processIntent(DialogImpl dialog, String naturalLanguageText) {
		List<AGFNode> promptGrams = new ArrayList<>();
		for(Entity entity : dialog.getIntent().getEntityList().values()) {
			if(entity.getPrompt() != null) {
			promptGrams.add(entity.getPrompt().getGrammar());
			}
		}
		
		promptGrams.add(this.quitIntentUserInputGram);
		
		NLLexer nlLexer = new NLLexer(this.language.getNumberConversion());
		List<WordToken> tokens = nlLexer.tokenize(naturalLanguageText);
		INLParser nlParser = new NLParser(promptGrams, this.language.getStemmer());
		try {
			int matchingNodeIndex = nlParser.matchingNodeIndex(tokens);
			
			if(matchingNodeIndex == promptGrams.indexOf(this.quitIntentUserInputGram)) {
				dialog.output(generateRandomAnswer(this.quitIntentAnswer));
				dialog.setIntent(null);
				return;
			}
			
			Map<String, String> entityIdToUserContent = getEntityContent(promptGrams.get(matchingNodeIndex));
			
			for(Entry<String, String> entry : entityIdToUserContent.entrySet()) {
				EntityData data = new EntityData(entry.getValue());
				dialog.getIntent().getEntityList().get(entry.getKey()).insertEntityData(data);
			}
			
			if(!dialog.getIntent().isFinished()) {
				dialog.output(dialog.getIntent().generateQuestion());
			}
		} catch(NLParserException e) {
			this.logger.debug("no matching grammar found " + e.getMessage());
			dialog.output(generateRandomAnswer(this.failedToUnderstandAnswer));
		}
		
	}
	
	/**
	 * helper method to extract entity contents
	 * @param node to extract entity content from
	 * @return Map which is mapping the entity id to the user provided content
	 */
	private Map<String, String> getEntityContent(AGFNode node){
		Map<String, String> result = new HashMap<>();
		
		for(AGFNode child : node.getChilds()) {
			if(child.getType() == AGFNodeType.ENTITY) {
				EntityNode entity = (EntityNode) child;
				if(entity.getUserProvidedContent() != null) {
					result.put(entity.getContent(),entity.getUserProvidedContent());
				}
			}else {
				result.putAll(getEntityContent(child));
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager#decideIntent(de.unistuttgart.iaas.amyassist.amy.core.natlang.DialogImpl, java.lang.String)
	 */
	@Override
	public void decideIntent(DialogImpl dialog, String naturalLanguageText) {
		NLLexer nlLexer = new NLLexer(this.language.getNumberConversion());
		List<WordToken> tokens = nlLexer.tokenize(naturalLanguageText);
		INLParser nlParser = new NLParser(new ArrayList<>(this.nodeToMethodAIMPair.keySet()), this.language.getStemmer());
		try {
			AGFNode node = nlParser.matchingNode(tokens);
			Method left = this.nodeToMethodAIMPair.get(node).getLeft();
			AIMIntent right = this.nodeToMethodAIMPair.get(node).getRight();
			UserIntent userIntent = new UserIntent(left, right);
			dialog.setIntent(userIntent);
			
			Map<String, String> entityIdToUserContent = getEntityContent(node);
			
			for(Entry<String, String> entry : entityIdToUserContent.entrySet()) {
				dialog.getIntent().getEntityList().get(entry.getValue());
				EntityData data = new EntityData(entry.getValue());
				dialog.getIntent().getEntityList().get(entry.getKey()).insertEntityData(data);
			}
			
			if(!dialog.getIntent().isFinished()) {
				dialog.output(dialog.getIntent().generateQuestion());
			}
		} catch(NLParserException e) {
			this.logger.debug("no matching grammar found " + e.getMessage());
			dialog.output(generateRandomAnswer(this.failedToUnderstandAnswer));
		}

	}
	
	private String generateRandomAnswer(String[] strings) {
		Random rand = new Random();
		int rndm = rand.nextInt(strings.length);
		return strings[rndm];
	}
}








