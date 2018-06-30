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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.parselets.IAGFParselet;

/**
 * interface for an AGF parser
 * @author Felix Burk
 */
public class Parser {
	
	private Iterator<AGFToken> mTokens;
	
	private final Map<AGFTokenType, IAGFParselet> mAGFParselets =
			 new HashMap<>();
	
	private List<AGFToken> mRead;
	
	/**
	 * constructor
	 * @param tokens
	 */
	public Parser(Iterator<AGFToken> tokens) {
		this.mTokens = tokens;
		this.mRead = new ArrayList<>();
	}
	
	/**
	 * does the current token match the expected one?
	 * @param expected AGFToken
	 * @return if it matched
	 */
	public boolean match(AGFTokenType expected) {
		AGFToken token = lookAhead(0);
		if(token != null) {
			return token.type == expected;
		}
		return false;
		
	}
	
	/**
	 * register parselets
	 * @param token to register
	 * @param node to register
	 */
	public void register(AGFTokenType token, IAGFParselet node) {
		this.mAGFParselets.put(token, node);
	}
	
	/**
	 * 
	 * @return
	 */
	public AGFNode parseWholeExpression() {
		AGFNode n = new AGFNode("");
		while(this.mTokens.hasNext()) {
			n.addChild(this.parseExpression());
		}
		return n;
	}
	
	/**
	 * parse the expression
	 * @return the node
	 */
	public AGFNode parseExpression() {
		AGFToken token = consume();
		System.out.println("consuming... " + token.getType());
		
		IAGFParselet toParse = this.mAGFParselets.get(token.type);
		
		if (toParse == null) throw new AGFParseException("Could not parse \"" +
				token.content + "\".");
			
		return toParse.parse(this, token);
	}
	
	/**
	 * consume the right token
	 * @param expected token
	 * @return
	 */
	public AGFToken consume(AGFTokenType expected) {
	    AGFToken token = lookAhead(0);
	    if (token.type != expected) {
	      throw new RuntimeException("Expected token " + expected +
	          " and found " + token.getType());
	    }
	    return consume();
	}

	/**
	 * consume a token
	 * @return consumed token
	 */
	public AGFToken consume() {
		//make sure we read the token
		lookAhead(0);
		return this.mRead.remove(0);
	}
	
	/**
	 * look ahead as many tokens as needed
	 * @param distance needed
	 * @return token at distance
	 */
	private AGFToken lookAhead(int distance) {
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
