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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.JSGFGenerator;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * test method for NL Parser, uses predefined grammars and nl
 * @author Felix Burk
 */
public class NLParserTest {
	
	/**
	 * pre defined grammars
	 */
	String[] grammars = {
		"weather [today]",
		"alarm (set|create) alarm (at|for) # oh #",
		"test [# (minutes|minute)]",
		"alarm clock (set|create) timer (for|on) [# (hour|hours)] [# (minute|minutes)] [# (second|seconds)]",
		"delete (alarm|timer) #",
		"spotify play",
		"edit alarm # to # oh #",
		"when (does|is) timer # (ringing|ring)",
		
	};

	
	String [] nlInput = {
		"test 10 minutes",
		"test 10 minute",
		"weather",
		"weather today",
		"wrong grammar",
		"delete alarm 10",
		"when does timer 10 ring",
		"spotify play",
		"edit alarm 10 to 20 oh 9",
		"alarm clock create timer on 10 hours 2 minute 10 seconds"
	};

	 /**
	  * tests hard coded nls and grammars
	  */
	 @Test
	 public void testParser() {
		 List<AGFParser> parsers = new ArrayList<>();
		 List<AGFNode> nodes = new ArrayList<>();
		 
		 NLParser nlParser = new NLParser(new ArrayList<AGFNode>());
		 
		 for(String s : this.grammars) {
			 System.out.println("grammar " + s);
			 AGFParser p = new AGFParser(new AGFLexer(s));
			 parsers.add(p);
			 AGFNode agfGram = p.parseWholeExpression();
			 nodes.add(agfGram);
			 nlParser.addAGFNode(agfGram);
		 }
		 
		 for(String s : nlInput) {
			 NLLexer lex = new NLLexer(s);
			 if(s.equals("wrong grammar")) {
				 assertThrows(NLParserException.class, ()->nlParser.parseNL(lex));
			 }else {
				 JSGFGenerator gen = new JSGFGenerator("test", "test", "test", "test");
				 //matching grammars
				 System.out.println(gen.addRule(nlParser.parseNL(lex), s));
			 }
		 }
	 }

}







