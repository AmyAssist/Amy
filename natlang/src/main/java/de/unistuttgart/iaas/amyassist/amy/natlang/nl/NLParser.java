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

package de.unistuttgart.iaas.amyassist.amy.natlang.nl;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.natlang.agf.AGFParseException;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.nodes.*;
import de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.Stemmer;
import de.unistuttgart.iaas.amyassist.amy.natlang.util.CompareWords;

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
	private List<EndToken> mRead;

	private int currentIndex;

	private Stemmer stemmer;
	
	private Deque<AGFNode> shortWCStopper;

	/**
	 *
	 * @param grammars
	 *            all possible grammars to match
	 * @param stemmer
	 *            which stemmer should be used
	 */
	public NLParser(List<AGFNode> grammars, Stemmer stemmer) {
		this.grammars = grammars;
		this.stemmer = stemmer;
	}

	@Override
	public AGFNode matchingNode(List<EndToken> nl) {
		this.mRead = nl;
		for (AGFNode agf : this.grammars) {
			this.shortWCStopper = generateStopperStack(agf);
			agf.deleteEntityContent();
			AGFNode nodeSorted = sortChildsOfOrAndOp(agf);
			this.currentIndex = 0;
			if (checkNode(nodeSorted) && this.currentIndex == nl.size()) {
				return agf;
			}
		}
		throw new NLParserException("could not find matching grammar for tokens" + nl);
	}

	/**
	 * @param agf
	 * @return
	 */
	private Deque<AGFNode> generateStopperStack(AGFNode agf) {
		Deque<AGFNode> stack = new ArrayDeque<>();
		generateStopperStack(agf, stack, false);
		return stack;
	}

	
	boolean x = false;
	/**
	 * @param agf
	 * @param stack 
	 * @param b
	 */
	private void generateStopperStack(AGFNode agf, Deque<AGFNode> stack, boolean b) {
		for(AGFNode node : agf.getChilds()) {
			if(node.getType() == AGFNodeType.SHORTWC && !x) {
				x = true;
				generateStopperStack(node, stack, true);
			}else if(x) {
				x = false;
				stack.push(node);
			}else {
				generateStopperStack(node, stack, b);
			}
		}
	}

	/**
	 * sorts childs of or groups and optional groups by size size meaning number of words inside the sentences seperated
	 * by '|'
	 *
	 * this prevents problems like [very | very very] very not beeing recognized with the input very very very because
	 * this parser is greedy and picks the first matching sentence it finds. If we just order the sentences in or and
	 * optional groups by number of leafes (meaning words/rules) we will be fine
	 *
	 * @param node
	 *            to sort
	 * @return sorted node
	 */
	public AGFNode sortChildsOfOrAndOp(AGFNode node) {

		if (node.getType().equals(AGFNodeType.OPG) || node.getType().equals(AGFNodeType.ORG)) {
			Collections.sort(node.getChilds(), (n1, n2) -> Integer.compare(n1.countLeafes(), n2.countLeafes()));
			Collections.reverse(node.getChilds());
		}

		for (AGFNode child : node.getChilds()) {
			sortChildsOfOrAndOp(child);
		}

		return node;
	}

	@Override
	public int matchingNodeIndex(List<EndToken> nl) {
		return this.grammars.indexOf(matchingNode(nl));
	}

	/**
	 * another ugly internal state for wildcards if this is true everything will be skipped and return true
	 */
	boolean wildcardSkip = false;

	/**
	 * recursive method to check each node preorder style
	 *
	 * @param agf
	 *            current node to check
	 * @return success
	 */
	private boolean checkNode(AGFNode agf) {
		switch (agf.getType()) {
		case AGF:
			int traceBack = this.currentIndex;
			for (AGFNode node : agf.getChilds()) {
				if (!checkNode(node)) {
					this.currentIndex = traceBack;
					agf.deleteEntityContent();
					return false;
				}
				if (this.wildcardSkip) {
					return true;
				}
			}
			break;
		case OPG:
			for (AGFNode node : agf.getChilds()) {
				checkNode(node);
			}
			break;
		case ORG:
			for (AGFNode node : agf.getChilds()) {
				if (checkNode(node)) {
					return true;
				}
			}
			return false;
		case MORPH:
			for (AGFNode node : agf.getChilds()) {
				if (!checkNode(node)) {
					return false;
				}
			}
			break;
		case SHORTWC:
			return matchShortWC(agf);
		case LONGWC:
			this.wildcardSkip = true;
			for (int i = this.currentIndex; i < this.mRead.size(); i++) {
				consume();
			}
			break;
		case WORD:
			return match(agf);
		case NUMBER:
			return matchNumber(agf);
		case ENTITY:
			return fillEntity(agf);
		default:
			return false;

		}
		return true;
	}

	/**
	 * matches a short wildcard with fixed skip size
	 * this uses the internal stack to check the end of the wildcards
	 * @param agf to check
	 * @return if the short wildcard matched
	 */
	private boolean matchShortWC(AGFNode agf) {
		AGFNode endNode = this.shortWCStopper.peek();
		if(endNode == null) {
			return false;
		}
		ShortWNode wc = (ShortWNode) agf;
		int i;
		int index = this.currentIndex;
		for(i=0; (i <= wc.getMaxWordLength() && i < this.mRead.size()-index); i++) {
			EndToken token = this.lookAhead(0);
			if(checkNode(endNode)) {
				//undo consume from checkNode
				this.currentIndex--;
				this.shortWCStopper.pop();
				break;
			}else if(token != null) {
				//skip current word because it did not match the end criteria
				consume();
			}
		}
		return true;
	}

	/**
	 * checks if a number is at the current index and if the number matches the conditions of the NumberNode e.g. is in
	 * correct range and stepsize
	 *
	 * @param agf
	 *            to match
	 * @return true if the node matches
	 */
	private boolean matchNumber(AGFNode agf) {
		EndToken token = lookAhead(0);
		return matchNumber(agf, token);
	}
	
	private boolean matchNumber(AGFNode agf, EndToken token) {
		if (token == null || token.getContent() == null) {
			return false;
		}
		try {
			NumberNode numberNode = (NumberNode) agf;
			numberNode.setContainedNumber(token.getContent().trim());
			consume();
		} catch (ClassCastException e) {
			throw new NLParserException("Node Type Number was no NumberNode " + e);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * fills entity content and checks if the entity matches
	 *
	 * @param agf
	 *            to match
	 * @return true if the entity matched
	 */
	private boolean fillEntity(AGFNode agf) {
		int startIndex = this.currentIndex;
		boolean matched = true;
		for (AGFNode node : agf.getChilds()) {
			matched = checkNode(node) && matched;
		}
		int endIndex = this.currentIndex;

		try {
			EntityNode entity = (EntityNode) agf;
			StringBuilder b = new StringBuilder();
			for (int i = startIndex; i <= endIndex - 1; i++) {
				b.append(this.mRead.get(i) + " ");
			}
			if (matched) 
				entity.setUserProvidedContent(b.toString().trim());
			return matched;
		} catch (ClassCastException e) {
			throw new NLParserException("Node Type Entity was no EntityNode " + e);
		}
	}

	/**
	 * does the current token match the expected one?
	 *
	 * @param toMatch
	 *            the node to match
	 * @return if it matched
	 */
	private boolean match(AGFNode toMatch) {
		EndToken token = lookAhead(0);

		if (compareWord(toMatch, token)) {
			consume();
			return true;
		}
		return false;
	}

	private boolean compareWord(AGFNode toMatch, EndToken token) {
		if(toMatch == null || toMatch.getContent() == null || token == null || token.getContent() == null) 
			return false;
		
		String nodeContent = toMatch.getContent();
		String tokenContent = token.getContent();
		
		if(this.stemmer != null) {
			nodeContent = this.stemmer.stem(toMatch.getContent());
			tokenContent = this.stemmer.stem(token.getContent());
		}
		if (nodeContent.equals(tokenContent)) {
			return true;
		}
		return !CompareWords.isDistanceBigger(nodeContent, tokenContent, 1);
	}

	/**
	 * consume a token
	 *
	 * @return consumed token
	 */
	private EndToken consume() {
		if (this.mRead.size() > this.currentIndex) {
			return this.mRead.get(this.currentIndex++);
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
	private EndToken lookAhead(int distance) {
		int index = this.currentIndex + distance;
		if (this.mRead.size() > index) {
			// Get the queued token.
			return this.mRead.get(index);
		}

		return null;
	}
}
