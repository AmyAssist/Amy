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

package io.github.amyassist.amy.natlang.nl;

import java.util.*;

import org.apache.commons.lang3.StringUtils;

import io.github.amyassist.amy.natlang.agf.AGFParseException;
import io.github.amyassist.amy.natlang.agf.nodes.*;
import io.github.amyassist.amy.natlang.languagespecifics.Stemmer;
import io.github.amyassist.amy.natlang.util.CompareWords;

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

	/**
	 * current index of read tokens
	 */
	private int currentIndex;

	/**
	 * a stemmer instance - this handles matches like
	 * likes - like
	 */
	private Stemmer stemmer;
	
	/**
	 * a deque containing helper ORGroups to indicate 
	 * the + wildcard when to stop matching
	 */
	private Deque<AGFNode> shortWCStopper;
	
	/**
	 * contains all matched strings
	 * meaning the content of the node that matched
	 * and if a wildcard matched to matching string
	 */
	private List<String> matchedStrings;

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
			this.matchedStrings = new ArrayList<>();
			this.shortWCStopper = generateStopperDeque(agf);
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
	 * use this method to generate the stack
	 * see {@link #generateStopperDeque(AGFNode, Deque) generateStopperStack}}
	 * 
	 * @param agf node to generate stack from
	 * @return the generated deque
	 */
	public Deque<AGFNode> generateStopperDeque(AGFNode agf) {
		Deque<AGFNode> stack = new ArrayDeque<>();
		this.possibleStoppers = new ArrayList<>();
		generateStopperDeque(agf, stack);
		if(!this.possibleStoppers.isEmpty()) {
			stack.push(generateHelperORGroup(this.possibleStoppers));
			this.foundShortWC = false;
		}
		return stack;
	}
	
	/**
	 * generates a helper ORGroup.
	 * This ORGroup is used in {{@link #generateStopperDeque(AGFNode, Deque)}
	 * the children in the group are possible stoppers for short wildcards.
	 * for example:
	 * test + [test2] [test3] test4
	 * then the helperORGroup used as a stopper would look like this
	 * (test2|test3|test4) 
	 * because the optional groups might not be used
	 * 
	 * @param children to add to group
	 * @return ORGroup inside an AGFNode to maintain the traceback ability
	 */
	private AGFNode generateHelperORGroup(List<AGFNode> children) {
		AGFNode parent = new AGFNode("");
		ORGroupNode orGroup = new ORGroupNode("");
		for(AGFNode node : children) {
			orGroup.addChild(node);
		}
		parent.addChild(orGroup);
		return parent;
	}

	/**
	 * indicated if a + wildcard has been found
	 */
	private boolean foundShortWC = false;
	/**
	 * a list of all possible stoppers for + wildcards
	 * that will be combined in a generated Helper ORGroup
	 */
	private List<AGFNode> possibleStoppers;
	/**
	 * generates a deque which tells a short wildcard when to stop
	 * e.g. in the grammar "test + testi + testo" 
	 * the stack would contain "testo", "testi". The first + wildcard then knows
	 * to stop at "testi", because its on last in the queue
	 *  "testi" gets removed after + matched. 
	 * Then the next + knows to stop at testo because its last in the queue.
	 * 
	 * @param agf node to generate stack from
	 * @param stack the stack
	 */
	private void generateStopperDeque(AGFNode agf, Deque<AGFNode> stack) {
		for(AGFNode node : agf.getChilds()) {
			if(node.getType() == AGFNodeType.SHORTWC && !this.foundShortWC) {
				this.foundShortWC = true;
			}else if(this.foundShortWC) {
				//optinal group hack
				if(node.getType() == AGFNodeType.OPG) {
					for(AGFNode opChild : node.getChilds()) {
						this.possibleStoppers.add(opChild);
					}
				}else {
					this.possibleStoppers.add(node);
					stack.push(generateHelperORGroup(this.possibleStoppers));
					this.possibleStoppers = new ArrayList<>();

					this.foundShortWC = false;
				}
				
			}else {
				generateStopperDeque(node, stack);
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
	 * another ugly internal state for infinite * wildcards 
	 * if this is true everything will be skipped and return true
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
					this.matchedStrings = this.matchedStrings.subList(0, this.currentIndex);
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
			return matchLongWC();
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
	 * matches a greedy long wildcard * with infinite input
	 * this consumes the whole input left! only use * at the end of a grammar
	 * 
	 * @return if it matches
	 */
	private boolean matchLongWC() {
		this.wildcardSkip = true;
		//quick hack - match at least one character
		if(this.currentIndex == this.mRead.size()) {
			return false;
		}
		for (int i = this.currentIndex; i < this.mRead.size(); i++) {
			EndToken token = lookAhead(0);
			if(token != null) {
				this.matchedStrings.add(token.getContent());
				consume();
			}else {
				break;
			}
		}
		return true;
	}

	/**
	 * matches a short non greedy wildcard with fixed skip size
	 * this uses the internal deque to check the end of the wildcards
	 * 
	 * @param agf to check
	 * @return if the short wildcard matched
	 */
	private boolean matchShortWC(AGFNode agf) {
		AGFNode endNode = this.shortWCStopper.peekLast();
		if(endNode == null) {
			return false;
		}
		
		ShortWNode wc = (ShortWNode) agf;
		int i;
		int skips = 0;
		int index = this.currentIndex;
		for(i=0; (i < wc.getMaxWordLength() && i < this.mRead.size()-index); i++) {
			EndToken token = this.lookAhead(0);
			if(checkNode(endNode)) {
				//undo consume from checkNode
				this.currentIndex = index+skips;
				this.shortWCStopper.pollLast();
				break;
			}else if(token != null) {
				//skip current word because it did not match the end criteria
				this.matchedStrings.add(token.getContent());
				skips++;
				consume();
			}
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
			if (matched) {
				String content = StringUtils.join(this.matchedStrings.subList(startIndex, endIndex), " ");
				entity.setUserProvidedContent(content);

			}
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

	/**
	 * compare a AGFNode to a word endtoken
	 * 
	 * @param toMatch node to compare to
	 * @param token to compare to
	 * @return if a match is possible
	 */
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
			this.matchedStrings.add(toMatch.getContent());
			return true;
		}
		if(nodeContent.length() > 3 && !CompareWords.isDistanceBigger(nodeContent, tokenContent, 1)) {
			this.matchedStrings.add(toMatch.getContent());
			return true;
		}
		return false;
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
	
	/**
	 * checks if a endtoken number matches an AGFNode
	 * 
	 * @param agf to compare to
	 * @param token to compare to
	 * @return if a match is possible
	 */
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
		this.matchedStrings.add(token.getContent());
		return true;
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
