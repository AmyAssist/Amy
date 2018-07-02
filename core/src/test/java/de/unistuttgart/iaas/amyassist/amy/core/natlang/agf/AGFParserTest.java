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

import java.util.List;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.JSGFRuleGenerator;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNodeType;


/**
 * TODO: Description
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
	}
	
	/**
	 * tests groups surrounded by characters
	 * @param brO open bracket character
	 * @param brC close bracket character
	 * @param type the node type
	 */
	public void bracketGroup(char brO, char brC, AGFNodeType type) {
		AGFLexer lex = new AGFLexer();
		//testinvalid group with missing ) bracket
		List<AGFToken> list = lex.tokenize("set timer on " + brO + "x");
		AGFParser parser = new AGFParser(list.iterator());
		
		assertThrows(AGFParseException.class, () -> parser.parseWholeExpression());
		
		//test invalid or group with missing ) bracket
		list = lex.tokenize("set timer on " + brO +  "wa|d");
		AGFParser parser2 = new AGFParser(list.iterator());
		
		assertThrows(AGFParseException.class, () -> parser2.parseWholeExpression());
		
		//test normal group without suffix 
		list = lex.tokenize(brO + "wa|d" + brC + " bla");
		AGFParser parser4 = new AGFParser(list.iterator());
		AGFNode node = parser4.parseWholeExpression();
		AGFNode orgroup = node.getChilds().get(0);
		assertEquals(orgroup.getType(), type); 
		assertEquals(orgroup.getChilds().get(0).getType(), AGFNodeType.R);
		assertEquals(orgroup.getChilds().get(1).getType(), AGFNodeType.R);
		
		//test normal group with suffix
		list = lex.tokenize(brO + "wa|d" + brC + " bla");
		AGFParser parser5 = new AGFParser(list.iterator());
		AGFNode node2 = parser5.parseWholeExpression();
		AGFNode orgroup2 = node2.getChilds().get(0);
		assertEquals(orgroup2.getType(), type); 
		assertEquals(orgroup2.getChilds().get(0).getType(), AGFNodeType.R);
		assertEquals(orgroup2.getChilds().get(1).getType(), AGFNodeType.R);
		
		//test nested groups
		list = lex.tokenize(brO + "wa"+brO+"a|b" + brC + "|d" + brC);
		AGFParser parser6 = new AGFParser(list.iterator());
		AGFNode nestedNode = parser6.parseWholeExpression();
		AGFNode secndOr = nestedNode.getChilds().get(0).getChilds().get(0).getChilds().get(1);
		assertEquals(secndOr.getType(), type);
		
	}

}
