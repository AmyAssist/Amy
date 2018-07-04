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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This parser looks for rules inside the natural language input
 * this means it fills the word tokens with necessary types
 * 
 * @author Felix Burk
 */
public class NLParser {
	
	/**
	 * contains regex with the corresponding WordTokenType
	 */
	Map<String, WordTokenType> regexTokenType = new HashMap<>();{
		this.regexTokenType.put("[a-zA-Z]+", WordTokenType.WORD);
		this.regexTokenType.put("[0-9]+", WordTokenType.NUMBER);
	}
	
	/**
	 * the result list
	 */
	List<WordToken> result;
	
	/**
	 * the word token list as iterator
	 */
	Iterator<WordToken> mIter;
	
	/**
	 * constructor
	 * 
	 * @param iter the iterator containing word tokens
	 */
	public NLParser(Iterator<WordToken> iter) {
		this.mIter = iter;
		this.result = new ArrayList<>();
		
		while(iter.hasNext()) {
			parse(iter.next());
		}
	}

	/**
	 * parses a single WordToken
	 * 
	 * @param next the WordToken
	 */
	private void parse(WordToken next) {
		for (Map.Entry<String, WordTokenType> entry : this.regexTokenType.entrySet()){
		    if(next.getContent().matches(entry.getKey())) {
		    	next.setType(entry.getValue());
		    	this.result.add(next);
		    	break;
		    }
		}
		
	}
	
	/**
	 * the result as a new iterator
	 * @return iterator containing word tokens
	 */
	public Iterator<WordToken> getResult(){
		return this.result.iterator();
	}

}
