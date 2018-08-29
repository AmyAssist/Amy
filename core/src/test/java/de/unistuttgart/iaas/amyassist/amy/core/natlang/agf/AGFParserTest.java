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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.agf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNodeType;


/**
 * Test for AGFParser
 * @author Felix Burk
 */
public class AGFParserTest {
	
	/**
	 * tests or groups eg (x|y)
	 * and optional groups eg [x|y]
	 */
	@Test
	public void testBracketGroups() {
		bracketGroup('(', ')', AGFNodeType.ORG);
		bracketGroup('[', ']', AGFNodeType.OPG);
		testEntities();
	}
	
	/**
	 * 
	 */
	private void testEntities() {
		Map<String, AGFNode> map = new HashMap<>();
		map.put("x", new AGFNode("test"));
		map.put("timer", new AGFNode("test"));
		AGFParser parser = new AGFParser(new AGFLexer("set timer on {x"), map);
		assertThrows(AGFParseException.class, () -> parser.parseWholeExpression());

		AGFLexer lex = new AGFLexer("set {timer} on x");
		while(lex.hasNext()) {
			System.out.println(lex.next().type);
		}
		AGFParser parser2 = new AGFParser(new AGFLexer("set {timer} on x"), map);
		AGFNode node = parser2.parseWholeExpression();
		System.out.println(node.printSelf());
		AGFNode morph = node.getChilds().get(0);
		assertEquals(morph.getType(), AGFNodeType.MORPH);
		assertEquals(morph.getChilds().get(1).getType(), AGFNodeType.ENTITY);
	}

	/**
	 * tests groups surrounded by characters
	 * @param brO open bracket character
	 * @param brC close bracket character
	 * @param type the node type
	 */
	public void bracketGroup(char brO, char brC, AGFNodeType type) {
		//testinvalid group with missing ) bracket
		AGFParser parser = new AGFParser(new AGFLexer("set timer on " + brO + "x"));
		
		assertThrows(AGFParseException.class, () -> parser.parseWholeExpression());
		
		//test invalid or group with missing ) bracket
		AGFParser parser2 = new AGFParser(new AGFLexer("set timer on " + brO +  "wa|d"));
		
		assertThrows(AGFParseException.class, () -> parser2.parseWholeExpression());
		
		//test normal group without suffix 
		AGFParser parser4 = new AGFParser(new AGFLexer(brO + "wa|d" + brC + " bla"));
		AGFNode node = parser4.parseWholeExpression();
		AGFNode orgroup = node.getChilds().get(0);
		assertEquals(orgroup.getType(), type); 
		assertEquals(orgroup.getChilds().get(0).getType(), AGFNodeType.AGF);
		assertEquals(orgroup.getChilds().get(1).getType(), AGFNodeType.AGF);
		
		//test normal group with suffix
		AGFParser parser5 = new AGFParser(new AGFLexer(brO + "wa|d" + brC + " bla"));
		AGFNode node2 = parser5.parseWholeExpression();
		AGFNode orgroup2 = node2.getChilds().get(0);
		assertEquals(orgroup2.getType(), type); 
		assertEquals(orgroup2.getChilds().get(0).getType(), AGFNodeType.AGF);
		assertEquals(orgroup2.getChilds().get(1).getType(), AGFNodeType.AGF);
		
		//test nested groups
		AGFParser parser6 = new AGFParser(new AGFLexer(brO + "wa"+brO+"a|b" + brC + "|d" + brC));
		AGFNode nestedNode = parser6.parseWholeExpression();
		AGFNode secndOr = nestedNode.getChilds().get(0).getChilds().get(0).getChilds().get(1);
		assertEquals(secndOr.getType(), type);
		
	}
	
	/**
	 * tests parsing number expressions eg: $(0,1000,1)
	 */
	@Test
	public void testNumberExpressions() {
		//okay i know this is really bad practice but it's 1 AM i'll fix this later... - Felix B 
		AGFParser parser = new AGFParser(new AGFLexer("test $(0,100,10)"));
		assertEquals(AGFNodeType.NUMBER, parser.parseExpression().getChilds().get(1).getType());
		AGFParser parser2 = new AGFParser(new AGFLexer("test $(0,100,10) test"));
		assertEquals(AGFNodeType.NUMBER, parser2.parseExpression().getChilds().get(1).getType());
		
		AGFParser parser3 = new AGFParser(new AGFLexer("test $(0,100,10 test"));
		assertThrows(AGFParseException.class, () -> parser3.parseWholeExpression());
		AGFParser parser4 = new AGFParser(new AGFLexer("test $(0,100,) test"));
		assertThrows(AGFParseException.class, () -> parser4.parseWholeExpression());

		AGFParser parser5 = new AGFParser(new AGFLexer("test $(0,100) test"));
		assertThrows(AGFParseException.class, () -> parser5.parseWholeExpression());

		AGFParser parser6 = new AGFParser(new AGFLexer("test $(0,10010) test"));
		assertThrows(AGFParseException.class, () -> parser6.parseWholeExpression());

		AGFParser parser7 = new AGFParser(new AGFLexer("test (0,100,10) test"));
		assertThrows(AGFParseException.class, () -> parser7.parseWholeExpression());

	}

}
