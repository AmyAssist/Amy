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
import java.util.Iterator;
import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParseException;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * NLParser implementation, matches NL input to AGFNodes
 * 
 * @author Felix Burk
 */
public class NLParser implements INLParser {
	
	/**
	 * list of all loaded grammars
	 */
	private List<AGFNode> grammars;
	
	/**
	 * list of read tokens
	 */
	private List<WordToken> mRead;
	
	/**
	 * internal iterator of tokens
	 */
	private Iterator<WordToken> mTokens;

	/**
	 * 
	 * @param grammars all possible grammars to match
	 */
	public NLParser(List<AGFNode> grammars) {
		this.grammars = grammars;
	}
	
	
	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.INLParser#matchingNode(java.util.Iterator)
	 */
	@Override
	public AGFNode matchingNode(Iterator<WordToken> nl) {
		List<WordToken> backup = new ArrayList<>();
		nl.forEachRemaining(backup::add);
		
		for(AGFNode agf : this.grammars) {
			this.mRead = new ArrayList<>();
			this.mTokens = backup.iterator();
			
			if(checkNode(agf)) {
				return agf;
			}
		}
		throw new NLParserException("could not find matching grammar");
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.INLParser#matchingNodeIndex(java.util.Iterator)
	 */
	@Override
	public int matchingNodeIndex(Iterator<WordToken> nl) {
		return this.grammars.indexOf(matchingNode(nl));
	}
	
	/**
	 * recursive method to check each node
	 * preorder style
	 * 
	 * @param agf current node to check
	 * @return success
	 */
	private boolean checkNode(AGFNode agf) {		
		switch(agf.getType()) {
		case AGF:
			for(AGFNode node : agf.getChilds()) {
				if(!checkNode(node)) {
					return false;
				}
			}
			break;
		case OPG:
			for(AGFNode node : agf.getChilds()) {
				checkNode(node);
			}
			break;
		case ORG:
			for(AGFNode node : agf.getChilds()) {
				if(checkNode(node)){
					break;
				}
			}
			break;
		case MORPH:
			for(AGFNode node : agf.getChilds()) {
				if(!checkNode(node)) {
					return false;
				}
			}
			break;
		case WORD:
			return match(agf);
		case RULE:
			return matchType(WordTokenType.NUMBER);
		default:
			return false;
			
		}
				
		return true;
	}
	
	/**
	 * match a WordTokenType with current position
	 * in iterator 
	 * 
	 * @param type the type to match
	 * @return success
	 */
	private boolean matchType(WordTokenType type) {
		WordToken token = lookAhead(0);
		if(token != null && token.getType().equals(type)) {
			consume();
			return true;
		}
		return false;
	}

	/**
	 * does the current token match the expected one?
	 * 
	 * @param toMatch the node to match
	 * @return if it matched
	 */
	private boolean match(AGFNode toMatch) {
		WordToken token = lookAhead(0);
		
		if(token != null && toMatch.getContent().equals(token.getContent())){
			consume();
			return true;
		}
		return false;
		
	}
	
	/**
	 * consume a token
	 * 
	 * @return consumed token
	 */
	private WordToken consume() {
		//make sure we read the token
		WordToken token = lookAhead(0);
		if(token != null) {
			return this.mRead.remove(0);
		}
		throw new AGFParseException("could not consume token, end of input");
	}
	
	/**
	 * look ahead as many tokens as needed
	 * 
	 * @param distance needed
	 * @return token at distance
	 */
	private WordToken lookAhead(int distance) {
	    while (distance >= this.mRead.size() && this.mTokens.hasNext()) {
	      this.mRead.add(this.mTokens.next());
	    }
	    
	    if(this.mRead.size() > distance) {
		    // Get the queued token.
		    return this.mRead.get(distance);
	    }
	    
	    return null;
	}
}
