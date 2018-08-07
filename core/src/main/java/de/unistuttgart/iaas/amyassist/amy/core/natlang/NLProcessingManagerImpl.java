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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
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
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Intent;
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
 * @author Leon Kiefer
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
	private ConfigurationLoader configurationLoader;
	private static final String CONFIG_NAME = "core.config";
	private static final String PROPERTY_ENABLE_STEMMER = "enableStemmer";
	private static final String PROBERTY_LANGUAGE = "chooseLanguage";
	private boolean stemmerEnabled;
	private String languageString;

	@PostConstruct
	private void setup() {
		this.stemmerEnabled = Boolean
				.parseBoolean(this.configurationLoader.load(CONFIG_NAME).getProperty(PROPERTY_ENABLE_STEMMER, "true"));
		this.languageString = this.configurationLoader.load(CONFIG_NAME).getProperty(PROBERTY_LANGUAGE, "EN");
	}

	@Override
	public void register(Class<?> naturalLanguageInterpreter, List<String> aimContents) {
		if (!naturalLanguageInterpreter
				.isAnnotationPresent(de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand.class)) {
			throw new IllegalArgumentException(
					"annotation is not present in " + naturalLanguageInterpreter.getSimpleName());
		}
		Set<Method> grammars = NLIAnnotationReader.getValidNLIMethods(naturalLanguageInterpreter);
		
		Set<Method> intentMethods = NLIAnnotationReader.getValidIntentMethods(naturalLanguageInterpreter);
		
		HashMap<String, Node> aimIntents = new HashMap<>();
		for(String s : aimContents) {
			NodeList tmpList = extractIntents(s);
			for(int i=0; i < tmpList.getLength(); i++) {
				aimIntents.put(tmpList.item(i).getAttributes().getNamedItem("ref").getTextContent(), tmpList.item(i));
			}
		}		
		
		HashMap<AIMIntent, Method> aimToMethod = new HashMap<>();
		Iterator<Method> i = intentMethods.iterator();
		while(i.hasNext()) {
			Method method = i.next();
			String s = naturalLanguageInterpreter.getName() + "." + method.getAnnotation(Intent.class).value();
			if(aimIntents.containsKey(s)) {
				AIMIntent intent = unmarshal(aimIntents.get(s));
				if(intent != null) {
					aimToMethod.put(unmarshal(aimIntents.get(s)), method);
				}
			}else {
				this.logger.error("no matching intent found in xml for {}", s);
			}
		}

		for (Method e : grammars) {
			PartialNLI partialNLI = this.generatePartialNLI(naturalLanguageInterpreter, e);
			this.register.add(partialNLI);
			this.registeredNodeList.add(partialNLI.getGrammar());
		}
	}

	/**
	 * @param node
	 * @return
	 */
	private AIMIntent unmarshal(Node node) {
		try {
			JAXBContext jc = JAXBContext.newInstance(AIMIntent.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			AIMIntent intentS = (AIMIntent) unmarshaller.unmarshal(node);
			return intentS;
		} catch (JAXBException e1) {
			e1.printStackTrace();
			this.logger.error("JAXBException Intent with ref {} could not be parsed", node.getAttributes().getNamedItem("ref").getTextContent());
		}
		return null;
	}

	private PartialNLI generatePartialNLI(Class<?> natuaralLanguageInterpreter, Method method) {
		String grammar = method.getAnnotation(Grammar.class).value();
		
		//TODO quick workaround for legacy # REMOVE THIS
		grammar = grammar.replaceAll("#", "{test}");
		
		AGFParser agfParser = new AGFParser(new AGFLexer(grammar));
		AGFNode parseWholeExpression = agfParser.parseWholeExpression();
		return new PartialNLI(method, parseWholeExpression, natuaralLanguageInterpreter);
	}

	@Override
	public String getGrammarFileString(String grammarName) {
		JSGFGenerator generator = new JSGFGenerator(grammarName, Constants.MULTI_CALL_START,
				Constants.SINGLE_CALL_START, Constants.MULTI_CALL_STOP, Constants.SHUT_UP);
		for (PartialNLI partialNLI : this.register) {
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
	
	/**
	 * extracts <Intent> tags from amy interaction model xmls
	 * 
	 * @param s amy interaction model content
	 * @return list of nodes with <Intent> tag
	 */
	private NodeList extractIntents(String s) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			ByteArrayInputStream input = new ByteArrayInputStream(s.getBytes("UTF-8"));
			Document doc = builder.parse(input);
			Element root = doc.getDocumentElement();
			return root.getElementsByTagName("Intent");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			this.logger.error("error parsing xml " + e.getMessage());
		}
		return null;
	}
}
