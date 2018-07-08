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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * test method for NL Parser, uses predefined grammars and nl
 * @author Felix Burk
 */
public class NLParserTest {
	
	/**
	 * hash map for pre defined gramars
	 */
	Map<String, AGFNode> nlToGram;
	
	/**
	 * sets up nlToGram HashMap
	 */
	@BeforeEach
	public void setup() {
		this.nlToGram = new HashMap<>();
		
		AGFNode gram = new AGFParser(new AGFLexer("test [# (minutes|minute)]")).parseWholeExpression();
		this.nlToGram.put("test 10 minutes", gram);
		this.nlToGram.put("test 10 minute", gram);
		
		AGFNode weather = new AGFParser(new AGFLexer("weather [today]")).parseWholeExpression();
		this.nlToGram.put("weather", weather);
		this.nlToGram.put("weather today", weather);
		
		this.nlToGram.put("wrong grammar", null);
		this.nlToGram.put("delete alarm 10", 
				new AGFParser(new AGFLexer("delete (alarm|timer) #")).parseWholeExpression());
		this.nlToGram.put("when does timer 10 ring", 
				new AGFParser(new AGFLexer("when (does|is) timer # (ringing|ring)")).parseWholeExpression());
		this.nlToGram.put("spotify play", 
				new AGFParser(new AGFLexer("spotify play")).parseWholeExpression());
		
		AGFNode badGram = new AGFParser(
				new AGFLexer("alarm clock (set|create) timer (for|on) "
						+ "[# (hour|hours)] [# (minute|minutes)] [# (second|seconds)]")).parseWholeExpression();
		this.nlToGram.put("alarm clock create timer on 10 hours 2 minute 10 seconds", badGram);
		this.nlToGram.put("alarm clock create timer on 10 hours 11 seconds", badGram);

	}

	 /**
	  * tests hard coded nls and grammars
	  */
	 @Test
	 public void testParser() {
		 List<AGFNode> addedNodes = new ArrayList<>();
		 
		 for(String s : this.nlToGram.keySet()) {
			 if(!addedNodes.contains(this.nlToGram.get(s))) {
				 addedNodes.add(this.nlToGram.get(s));
			 }
		 }
		 
		 NLParser nlParser = new NLParser(addedNodes);
		 
		 for(String s : this.nlToGram.keySet()) {
			 NLLexer lex = new NLLexer(s);
			 if(s.equals("wrong grammar")) {
				 assertThrows(NLParserException.class, ()->nlParser.matchingNodeIndex(lex));
			 }else {
				 //matching grammars
				 assertThat(nlParser.matchingNode(lex), equalTo(this.nlToGram.get(s)));
			 }
		 }
	 }

}







