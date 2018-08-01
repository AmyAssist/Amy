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
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.EntityNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.WordNode;

/**
 * parses the smallest meaningful unit in the AGF Syntax
 * 
 * <Morpheme> := (<Word> | <Rule>)+;
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
		while(parser.match(AGFTokenType.OPENCBR) ||
				parser.match(AGFTokenType.CLOSECBR) || parser.match(AGFTokenType.WORD)) {
			AGFToken t = parser.consume();
			parseMorph(morph, t, parser);
		}

		
		return morph;
	}

	/**
	 * parses a single morpheme
	 * 
	 * <Morpheme> := (<Word> | <Rule>);
	 * 
	 * @param morph the morpheme node
	 * @param token the corresponding token
	 * @param parser the corresponding parser
	 */
	private void parseMorph(MorphemeNode morph, AGFToken token, Parser parser) {
		if(token.type == AGFTokenType.WORD) {
			morph.addChild(new WordNode(token.content));
		}else if(token.type == AGFTokenType.OPENCBR) {
			parser.consume();
			EntityNode entity = new EntityNode("");
			if(parser.match(AGFTokenType.WORD)) {
				AGFToken word = parser.consume();
				entity.addChild(new WordNode(word.content));
				
				if(parser.consume().type != AGFTokenType.CLOSECBR) {
					throw new AGFParseException("} missing or entity name contains a whitespace");
				}
			}
			morph.addChild(entity);
		}
		
		
	}
	
	

}
