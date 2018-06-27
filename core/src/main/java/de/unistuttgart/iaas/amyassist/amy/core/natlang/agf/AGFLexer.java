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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * AGF lexer implementation
 * @author Felix Burk
 */
public class AGFLexer implements IAGFLexer {

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.IAGFLexer#tokenize(java.lang.String)
	 */
	@Override
	public List<AGFToken> tokenize(String toTokenize) {
		List<AGFToken> result = new ArrayList<>();
		
		int i = 0;
		while(i < toTokenize.length()) {
			char c = toTokenize.charAt(i);
			
			switch(c) {
    			case '(':
    				result.add(new AGFToken(AGFTokenType.OPENBR, "("));
    				break;
    			case ')':
    				result.add(new AGFToken(AGFTokenType.CLOSEBR, ")"));
    				break;
    			case '|':
    				result.add(new AGFToken(AGFTokenType.OR, "|"));
    				break;
    			case '[':
    				result.add(new AGFToken(AGFTokenType.OPENSBR, "["));
    				break;
    			case ']':
    				result.add(new AGFToken(AGFTokenType.CLOSESBR, "]"));
    				break;
    			case '#':
    				result.add(new AGFToken(AGFTokenType.RULE, "#"));
    				break;
    			default: 
    				//only allow normal SPACE codepoint 32, U+0020
    				if(c == 32) {
    					break;
    				}else if(Character.isLetter(c)) {
    					StringBuilder builder = new StringBuilder();
    					while(i < toTokenize.length() && Character.isLetter(toTokenize.charAt(i))) {
    						builder.append(toTokenize.charAt(i));
    						i++;
    					}
    					result.add(new AGFToken(AGFTokenType.WORD, builder.toString()));
    					i--;
    				}else {
    					throw new AGFLexerException("wrong character " + c);
    				}
    				break;
			}
			i++;
		}
		return result;
	}


}
