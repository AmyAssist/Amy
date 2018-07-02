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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.parselets;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParseException;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFToken;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFTokenType;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.Parser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.MorphemeNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.RuleNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.WordNode;

/**
 * parses the smallest meaningful unit in the AGF Syntax
 * 
 * <Morphene> := (<Word> | <Rule>)+;
 *  
 * @author Felix Burk
 */
public class MorphemeParselet implements IAGFParselet {
	
	/**
	 * parses a morphene parcelet
	 * @param parser the parser
	 * @param token the token found
	 * @return a MorpheneNode
	 */
	@Override
	public AGFNode parse(Parser parser, AGFToken token) {
		MorphemeNode morph = new MorphemeNode("");
		
		parseMorph(morph,token, parser);
		
		//first one was already consumed by the parser
		//the following ones have to be consumed "by hand"
		while(parser.match(AGFTokenType.RULE) || parser.match(AGFTokenType.WORD)) {
			AGFToken t = parser.consume();
			parseMorph(morph, t, parser);
		}

		
		return morph;
	}

	/**
	 * @param morph 
	 * @param token
	 * @param parser 
	 */
	private void parseMorph(MorphemeNode morph, AGFToken token, Parser parser) {
		if(token.type == AGFTokenType.WORD) {
			morph.addChild(new WordNode(token.content));
		}else if(token.type == AGFTokenType.RULE) {
			//is the next token a rule too? 
			//this is not allowed!
			if(!parser.match(AGFTokenType.RULE)) {
				morph.addChild(new RuleNode(token.content));
			}else {
				throw new AGFParseException("numbers cannot be followed by another number");
			}
		}
		
	}
	
	

}
