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

package io.github.amyassist.amy.core.natlang.nl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.amyassist.amy.natlang.agf.nodes.AGFNode;
import io.github.amyassist.amy.natlang.agf.nodes.AGFNodeType;
import io.github.amyassist.amy.natlang.agf.nodes.EntityNode;
import io.github.amyassist.amy.natlang.aim.XMLAmyInteractionModel;
import io.github.amyassist.amy.natlang.languagespecifics.ChooseLanguage;
import io.github.amyassist.amy.natlang.nl.EndToken;
import io.github.amyassist.amy.natlang.nl.NLLexer;
import io.github.amyassist.amy.natlang.nl.NLParser;
import io.github.amyassist.amy.natlang.userinteraction.UserIntent;

/**
 * TODO: Description
 * 
 * @author Felix Burk, Lars Buttgereit
 */
public class NLParserTest {

	private List<UserIntent> intents;

	@BeforeEach
	public void setup() throws JAXBException, IOException {
		InputStream inputResource = this.getClass()
				.getResourceAsStream("../userinteraction/testXMLUserInteraction.aim.xml");
		try (InputStreamReader inputStreamReader = new InputStreamReader(inputResource, StandardCharsets.UTF_8);
				BufferedReader reader = new BufferedReader(inputStreamReader)) {
			String xml = reader.lines().collect(Collectors.joining());
			JAXBContext jc = JAXBContext.newInstance(XMLAmyInteractionModel.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			StringReader stringReader = new StringReader(xml);
			XMLAmyInteractionModel aimmodel = (XMLAmyInteractionModel) unmarshaller.unmarshal(stringReader);

			UserIntent int0 = new UserIntent(this.getClass().getMethods()[0], aimmodel.getIntents().get(0));
			UserIntent int1 = new UserIntent(this.getClass().getMethods()[0], aimmodel.getIntents().get(1));
			UserIntent int2 = new UserIntent(this.getClass().getMethods()[0], aimmodel.getIntents().get(2));
			UserIntent int3 = new UserIntent(this.getClass().getMethods()[0], aimmodel.getIntents().get(3));

			this.intents = new ArrayList<>();
			this.intents.add(int0);
			this.intents.add(int1);
			this.intents.add(int2);
			this.intents.add(int3);
		}
	}

	@Test
	public void test() {
		NLLexer lex = new NLLexer(new ChooseLanguage("en", false));
		List<EndToken> tokens = lex.tokenize("greet me");
		List<AGFNode> nodes = new ArrayList<>();
		nodes.add(this.intents.get(0).getGrammar());
		NLParser parser = new NLParser(nodes, null);
		assertThat(parser.matchingNodeIndex(tokens), is(0));
		assertThat(this.intents.get(0).isFinished(), is(false));
	}

	@Test
	public void finished() {
		NLLexer lex = new NLLexer(new ChooseLanguage("en", false));
		List<EndToken> tokens = lex.tokenize("greet me with good morning test ten oh twenty");
		List<AGFNode> nodes = new ArrayList<>();
		nodes.add(this.intents.get(0).getGrammar());
		NLParser parser = new NLParser(nodes, null);

		assertThat(parser.matchingNodeIndex(tokens), is(0));
		assertThat(getEntityNodeContent(parser.matchingNode(tokens)), is("good morning10 oh 20"));
	}

	/**
	 * tests stop conditions in + wildcards
	 */
	@Test
	public void entityStopperGrammar() {
		NLLexer lex = new NLLexer(new ChooseLanguage("en", false));
		List<EndToken> tokens = lex.tokenize("best transport from stuttgart test to berlin x test");
		List<AGFNode> nodes = new ArrayList<>();
		nodes.add(this.intents.get(3).getGrammar());
		NLParser parser = new NLParser(nodes, null);

		Deque<AGFNode> queue = parser.generateStopperDeque(nodes.get(0));
		AGFNode queueLast = queue.pollLast();
		assertThat(queueLast.getType(), is(AGFNodeType.AGF));
		AGFNode orGroup = queueLast.getChilds().get(0);
		assertThat(orGroup.getType(), is(AGFNodeType.ORG));
		assertThat(orGroup.getChilds(), hasSize(1));

		AGFNode queueSecond = queue.pollLast();
		assertThat(queueSecond.getType(), is(AGFNodeType.AGF));
		AGFNode orGroupSecond = queueSecond.getChilds().get(0);
		assertThat(orGroupSecond.getType(), is(AGFNodeType.ORG));
		assertThat(orGroupSecond.getChilds(), hasSize(2));
	}

	@Test
	public void shortWildcardTest() {
		NLLexer lex = new NLLexer(new ChooseLanguage("en", false));
		List<EndToken> tokens = lex.tokenize("test the four sign wildcard long here");
		List<AGFNode> nodes = new ArrayList<>();
		nodes.add(this.intents.get(1).getGrammar());
		NLParser parser = new NLParser(nodes, null);
		assertThat(parser.matchingNodeIndex(tokens), is(0));
	}

	@Test
	public void longWildcardTest() {
		StringBuilder b = new StringBuilder();
		b.append("test the wildcard");
		for (int i = 0; i < 1000; i++) {
			b.append(" really");
		}
		b.append(" long");
		NLLexer lex = new NLLexer(new ChooseLanguage("en", false));
		List<EndToken> tokens = lex.tokenize(b.toString());
		List<AGFNode> nodes = new ArrayList<>();
		nodes.add(this.intents.get(2).getGrammar());
		NLParser parser = new NLParser(nodes, null);
		System.out.println(parser.matchingNode(tokens).printSelf());
		assertThat(parser.matchingNodeIndex(tokens), is(0));
	}

	StringBuilder b = new StringBuilder();

	public String getEntityNodeContent(AGFNode node) {
		for (AGFNode child : node.getChilds()) {
			if (child.getType() == AGFNodeType.ENTITY) {
				EntityNode entity = (EntityNode) child;
				this.b.append(entity.getUserProvidedContent());
			} else {
				getEntityNodeContent(child);
			}
		}

		return this.b.toString();
	}

}
