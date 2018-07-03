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

import java.util.ArrayList;
import java.util.List;

/**
 * Lexer for language input from speech
 * 
 * finds patterns in the language 
 * e.g. Rules (numbers, strings from the registry etc) and normal words
 * 
 * @author Felix Burk
 */
public class NLLexer {
	
	
	/**
	 * lexes
	 * @param toLex string to lex
	 * @return list of word tokens
	 */
	public List<WordToken> lex(String toLex){
		toLex = toLex.toLowerCase();
		System.out.println(toLex);
		List<WordToken> result = new ArrayList<>();
		
		StringBuilder currentWord = new StringBuilder();
		for(char c : toLex.toCharArray()) {
			switch(Character.getType(c)) {
			case Character.LOWERCASE_LETTER:
			case Character.DECIMAL_DIGIT_NUMBER:
				currentWord.append(c);
				break;
			//whitespace characters but not newline, tab or carriage return
			case Character.SPACE_SEPARATOR:
				result.add(new WordToken(currentWord.toString()));
				currentWord = new StringBuilder();
				break;
			default:
				throw new NLLexerException("character not recognized " + c + " stopping");
			}
			
			
		}
		result.add(new WordToken(currentWord.toString()));
		
		return result;
	}

}
