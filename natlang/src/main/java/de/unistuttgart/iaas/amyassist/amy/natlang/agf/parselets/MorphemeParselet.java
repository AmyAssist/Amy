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

package de.unistuttgart.iaas.amyassist.amy.natlang.agf.parselets;

import de.unistuttgart.iaas.amyassist.amy.natlang.agf.AGFParseException;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.AGFToken;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.AGFTokenType;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.Parser;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.nodes.*;

/**
 * parses the smallest meaningful unit in the AGF Syntax
 * 
 * <Morpheme> := (<Word> | <Rule> | <Number> | <ShortWildcard> | <LongWildcard> | <Entity>)+;
 * 
 * @author Felix Burk
 */
public class MorphemeParselet implements IAGFParselet {

	/**
	 * parses a morphene parcelet
	 * 
	 * @param parser
	 *                   the parser
	 * @param token
	 *                   the token found
	 * @return a MorpheneNode
	 */
	@Override
	public AGFNode parse(Parser parser, AGFToken token) {
		MorphemeNode morph = new MorphemeNode("");

		parseMorph(morph, token, parser);

		// first one was already consumed by the parser
		// the following ones have to be consumed "by hand"
		while (parser.match(AGFTokenType.OPENCBR) || parser.match(AGFTokenType.CLOSECBR)
				|| parser.match(AGFTokenType.WORD) || parser.match(AGFTokenType.DOLLAR) 
				|| parser.match(AGFTokenType.PLUS) || parser.match(AGFTokenType.ASTERISC)) {
			AGFToken t = parser.consume();
			parseMorph(morph, t, parser);
		}

		return morph;
	}

	/**
	 * parses a single morpheme
	 * 
	 * <Morpheme> := (<Word> | <Rule> | <Number> | <ShortWildcard> | <LongWildcard> | <Entity>);
	 * 
	 * @param morph
	 *                   the morpheme node
	 * @param token
	 *                   the corresponding token
	 * @param parser
	 *                   the corresponding parser
	 */
	private void parseMorph(MorphemeNode morph, AGFToken token, Parser parser) {
		// Parse Word
		if (token.type == AGFTokenType.WORD) {
			morph.addChild(new WordNode(token.content));
			// Parse Entity ex: {someId}
		} else if (token.type == AGFTokenType.OPENCBR) {
			if (parser.match(AGFTokenType.WORD)) {
				AGFToken word = parser.consume();
				AGFNode node = parser.getEntityAGF(word.content);
				EntityNode entity = new EntityNode(word.content);
				entity.addChild(node);

				if (parser.consume().type != AGFTokenType.CLOSECBR) {
					throw new AGFParseException("} missing or entity name contains a whitespace");
				}
				morph.addChild(entity);

			} else {
				throw new AGFParseException("} missing or entity name contains a whitespace");
			}
		} else if (token.type == AGFTokenType.DOLLAR) {
			parseNumberExpression(morph, parser);
		} else if(token.type == AGFTokenType.PLUS) {
			AGFNode node = new ShortWNode("");
			morph.addChild(node);
		} else if(token.type == AGFTokenType.ASTERISC) {
			AGFNode node = new LongWNode("");
			morph.addChild(node);
		}

	}


	/**
	 * Parse number expression ex: $(0,100,10)
	 * 
	 * @param morph
	 *                   the morpheme node
	 * @param token
	 *                   the corresponding token
	 * @param parser
	 *                   the corresponding parser
	 */
	private void parseNumberExpression(MorphemeNode morph, Parser parser) {
		String[] numbersInsideExpression;
		if (parser.match(AGFTokenType.OPENBR)) {
			parser.consume();
			numbersInsideExpression = getNumberExpressionContent(parser);

			if (parser.match(AGFTokenType.CLOSEBR)) {
				parser.consume();
				try {
					int min = Integer.parseInt(numbersInsideExpression[0]);
					int max = Integer.parseInt(numbersInsideExpression[1]);
					int stepSize = Integer.parseInt(numbersInsideExpression[2]);

					NumberNode numberNode = new NumberNode(min, max, stepSize);
					morph.addChild(numberNode);
				} catch (NumberFormatException e) {
					throw new AGFParseException(
							"number inside number expression could not be converted " + e.getMessage());
				}
			} else {
				throw new AGFParseException("missing ) in number expression");
			}
		}

	}

	/**
	 * parses (0,10,1)
	 * @param parser instance
	 * @return String[3] for the three numbers ex: (0,10,1)
	 */
	private String[] getNumberExpressionContent(Parser parser) {
		String[] numbersInsideExpression = new String[3];
		int j = 0;
		for (int i = 1; i <= 5; i++) {
			if (i % 2 == 0) {
				if (!parser.match(AGFTokenType.COMMA)) {
					throw new AGFParseException("missing comma in number expression");
				}
				parser.consume();
			} else {
				if (!parser.match(AGFTokenType.WORD)) {
					throw new AGFParseException("missing number inside number expression");
				}
				AGFToken numberToken = parser.consume();
				numbersInsideExpression[j] = numberToken.content;
				j++;
			}
		}
		return numbersInsideExpression;
	}

}
