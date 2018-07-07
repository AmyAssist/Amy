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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Lexer for language input from speech
 * 
 * filters bad characters and splits input in words
 * 
 * @author Felix Burk
 */
public class NLLexer implements Iterator<WordToken>{
	
	/**
	 * contains regex with the corresponding WordTokenType
	 */
	Map<String, WordTokenType> regexTokenType = new HashMap<>();{
		this.regexTokenType.put("[a-zA-Z]+", WordTokenType.WORD);
		this.regexTokenType.put("[0-9]+", WordTokenType.NUMBER);
	}
	
	private final String mToLex;
	private int mIndex = 0;
	
	/**
	 * lexer implemented as Iterator
	 * @param toLex the stirng to lex
	 */
	public NLLexer(String toLex) {
		this.mIndex = 0;
		this.mToLex = toLex.toLowerCase();
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.mIndex < this.mToLex.length();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public WordToken next() {
		if(hasNext()) {
    		StringBuilder currentWord = new StringBuilder();
    		while(this.mIndex < this.mToLex.length()) {
    			char c = this.mToLex.charAt(this.mIndex++);
    			
    			switch(Character.getType(c)) {
    			case Character.LOWERCASE_LETTER:
					//$FALL-THROUGH$
    			case Character.DECIMAL_DIGIT_NUMBER:
    				currentWord.append(c);
    				break;
    				
    			//handles single whitespace characters but not newline, tab or carriage return
    			case Character.SPACE_SEPARATOR:
    				if(currentWord.length() != 0) {
        				return parse(new WordToken(currentWord.toString()));
    				}
    				throw new NLLexerException("more than one whitespace found");
				default:
    				throw new NLLexerException("character not recognized " + c + " stopping");
    			}
    			
    			
    		}
    		return parse(new WordToken(currentWord.toString()));
		}
		throw new NoSuchElementException("thrown by NLLexer");
	}
	
	/**
	 * sets WordTokenType
	 * 
	 * @param next the WordToken
	 * @return the parsed WordToken
	 */
	private WordToken parse(WordToken next) {
		for (Map.Entry<String, WordTokenType> entry : this.regexTokenType.entrySet()){
		    if(next.getContent().matches(entry.getKey())) {
		    	next.setType(entry.getValue());
		    	return next;
		    }
		}
		throw new NLLexerException("no matching word type found");
		
	}
	
	 @Override
	 public void remove() {
	    throw new UnsupportedOperationException();
	}

}
