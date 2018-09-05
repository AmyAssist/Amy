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

import java.util.Collections;
import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParseException;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNodeType;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.EntityNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.NumberNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.ShortWNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.Stemmer;

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

	private int currentIndex;

	private Stemmer stemmer;

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
	public AGFNode matchingNode(List<WordToken> nl) {
		this.mRead = nl;
		for (AGFNode agf : this.grammars) {
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
	public int matchingNodeIndex(List<WordToken> nl) {
		return this.grammars.indexOf(matchingNode(nl));
	}

	/**
	 * ugly internal state variable to show how many skips are allowed currently
	 */
	int wcSkips = 0;

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
			this.wcSkips = ((ShortWNode) agf).getMaxWordLength();
			break;
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
	 * checks if a number is at the current index and if the number matches the conditions of the NumberNode e.g. is in
	 * correct range and stepsize
	 *
	 * @param agf
	 *            to match
	 * @return true if the node matches
	 */
	private boolean matchNumber(AGFNode agf) {
		WordToken token = lookAhead(0);

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
		WordToken token = lookAhead(0);

		if (compareWord(toMatch, token)) {
			consume();
			return true;
		}

		// greedy match words with fixed size of skips for wildcards
		if (this.wcSkips != 0) {
			int lookaheadSize = 0;
			for (int i = 1; i <= this.wcSkips; i++) {
				WordToken temp = lookAhead(i);
				if (compareWord(toMatch, temp)) {
					lookaheadSize = i;
					break;
				}
			}
			for (int i = 0; i < lookaheadSize + 1; i++) {
				consume();
			}
			if (lookaheadSize != 0) {
				return true;
			}
		}
		this.wcSkips = 0;
		return false;
	}

	private boolean compareWord(AGFNode toMatch, WordToken token) {
		if (this.stemmer != null && token != null
				&& this.stemmer.stem(toMatch.getContent()).equals(this.stemmer.stem(token.getContent()))) {
			return true;
		}
		return this.stemmer == null && token != null && toMatch.getContent().equals(token.getContent());
	}

	/**
	 * consume a token
	 *
	 * @return consumed token
	 */
	private WordToken consume() {
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
	private WordToken lookAhead(int distance) {
		int index = this.currentIndex + distance;
		if (this.mRead.size() > index) {
			// Get the queued token.
			return this.mRead.get(index);
		}

		return null;
	}
}
