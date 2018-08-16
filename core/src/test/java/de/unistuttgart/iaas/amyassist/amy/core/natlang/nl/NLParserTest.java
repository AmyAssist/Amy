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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.nl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNodeType;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.EntityNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLAmyInteractionModel;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.en.EnglishNumberConversion;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.userInteraction.UserIntent;

/**
 * TODO: Description
 * @author Felix Burk, Lars Buttgereit
 */
public class NLParserTest {
	
	private UserIntent intent;
	
	@BeforeEach
	public void setup() throws JAXBException, FileNotFoundException {
		FileInputStream stream = new FileInputStream(
				"src/test/resources/de/unistuttgart/iaas/amyassist/amy/core/natlang/userInteraction/testXMLUserInteraction.aim.xml");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String xml = reader.lines().collect(Collectors.joining());
		JAXBContext jc = JAXBContext.newInstance(XMLAmyInteractionModel.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StringReader stringReader = new StringReader(xml);
		XMLAmyInteractionModel aimmodel = (XMLAmyInteractionModel) unmarshaller.unmarshal(stringReader);
		
		this.intent = new UserIntent(this.getClass().getMethods()[0], aimmodel.getIntents().get(0));
		
	}
	
	
	@Test
	public void test() {
		NLLexer lex = new NLLexer(new EnglishNumberConversion());
		List<WordToken> tokens = lex.tokenize("greet me");
		List<AGFNode> nodes = new ArrayList<>();
		nodes.add(this.intent.getGrammar());
		NLParser parser = new NLParser(nodes, null);
		assertEquals(parser.matchingNodeIndex(tokens),0);
		assertEquals(intent.isFinished(), false);
	}
	
	@Test
	public void finished() {
		NLLexer lex = new NLLexer(new EnglishNumberConversion());
		List<WordToken> tokens = lex.tokenize("greet me with good morning test ten oh twenty");
		List<AGFNode> nodes = new ArrayList<>();
		nodes.add(this.intent.getGrammar());
		NLParser parser = new NLParser(nodes, null);
		
		assertEquals(parser.matchingNodeIndex(tokens),0);
		assertEquals(getEntityNodeContent(parser.matchingNode(tokens)), "good morning10 oh 20");
	}
	
	StringBuilder b = new StringBuilder();
	public String getEntityNodeContent(AGFNode node) {
		for(AGFNode child : node.getChilds()) {
			if(child.getType() == AGFNodeType.ENTITY) {
				EntityNode entity = (EntityNode) child;
				this.b.append(entity.getUserProvidedContent());
			}else {
				getEntityNodeContent(child);
			}
		}
	
		return this.b.toString();
	}

}








