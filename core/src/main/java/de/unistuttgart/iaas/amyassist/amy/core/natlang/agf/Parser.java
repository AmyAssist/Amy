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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.parselets.IAGFParselet;

/**
 * this class contains all necessary methods a standard top down recursive descent parser needs, with single token
 * lookahead (it's LL(1))
 * 
 * it owns the AGFToken stream (the iterator) and handles lookahead
 * 
 * @author Felix Burk
 */
public class Parser {

	private Iterator<AGFToken> mTokens;

	private final Map<AGFTokenType, IAGFParselet> mAGFParselets = new EnumMap<>(AGFTokenType.class);

	private List<AGFToken> mRead;
	
	/**
	 * maps entity id to corresponding AGFNode
	 */
	protected Map<String, AGFNode> entityToAGF = new HashMap<>();

	/**
	 * constructor
	 * 
	 * @param tokens
	 *            to consume
	 */
	public Parser(Iterator<AGFToken> tokens) {
		this.mTokens = tokens;
		this.mRead = new ArrayList<>();
	}

	/**
	 * does the current token match the expected one?
	 * 
	 * @param expected
	 *            AGFToken
	 * @return if it matched
	 */
	public boolean match(AGFTokenType expected) {
		AGFToken token = lookAhead(0);
		if (token != null) {
			return token.type == expected;
		}
		return false;

	}

	/**
	 * register parselets
	 * 
	 * @param token
	 *            to register
	 * @param node
	 *            to register
	 */
	public void register(AGFTokenType token, IAGFParselet node) {
		this.mAGFParselets.put(token, node);
	}

	/**
	 * tries to parse the whole expression 
	 * 
	 * @return generated AGFNode
	 */
	public AGFNode parseWholeExpression() {
		AGFNode n = new AGFNode("");
		while (this.mTokens.hasNext()) {
			n.addChild(this.parseExpression());
		}
		return n;
	}

	/**
	 * parses only a part of the expression
	 * meaning only one agf parselet
	 * 
	 * @return the node
	 */
	public AGFNode parseExpression() {
		AGFToken token = consume();

		IAGFParselet toParse = this.mAGFParselets.get(token.type);

		if (toParse == null)
			throw new AGFParseException("Could not parse \"" + token.content + "\".");

		return toParse.parse(this, token);
	}

	/**
	 * consume the right token
	 * 
	 * @param expected
	 *            token
	 * @return the AGFToken that was consumed
	 */
	public AGFToken consume(AGFTokenType expected) {
		AGFToken token = lookAhead(0);
		if (token == null)
			throw new AGFParseException("Token is null");
		if (token.type != expected) {
			throw new AGFParseException("Expected token " + expected + " and found " + token.getType());
		}
		return consume();
	}

	/**
	 * consume a token
	 * 
	 * @return consumed token
	 */
	public AGFToken consume() {
		// make sure we read the token
		AGFToken token = lookAhead(0);
		if (token != null) {
			return this.mRead.remove(0);
		}
		throw new AGFParseException("could not consume token, end of input");
	}

	/**
	 * look ahead as many tokens as needed
	 * 
	 * @param distance
	 *            needed
	 * @return token at distance
	 */
	private AGFToken lookAhead(int distance) {
		while (distance >= this.mRead.size() && this.mTokens.hasNext()) {
			this.mRead.add(this.mTokens.next());
		}

		if (this.mRead.size() > distance) {
			// Get the queued token.
			return this.mRead.get(distance);
		}

		return null;
	}

	/**
	 * returns corresponding AGFNode for entity id
	 * @param id of entity
	 * @return corresponding AGFNode
	 */
	public AGFNode getEntityAGF(String id) {
		if(this.entityToAGF != null && this.entityToAGF.get(id) != null) {
			return this.entityToAGF.get(id);
		}
		throw new AGFLexerException("no matching entity found for id " + id);
	}
}
