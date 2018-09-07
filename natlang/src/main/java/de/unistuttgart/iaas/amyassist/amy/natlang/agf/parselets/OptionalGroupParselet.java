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

import de.unistuttgart.iaas.amyassist.amy.natlang.agf.AGFToken;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.AGFTokenType;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.Parser;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.nodes.OptionalGroupNode;

/**
 * optional group parselet
 * 
 * <ORGroupParselet> := "[" <AGFNode>+ ( "|" <AGFNode>+ )* "]";
 * 
 * @author Felix Burk
 */
public class OptionalGroupParselet implements IAGFParselet {

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.natlang.agf.parselets.IAGFParselet#parse(de.unistuttgart.iaas.amyassist.amy.natlang.agf.Parser,
	 *      de.unistuttgart.iaas.amyassist.amy.natlang.agf.AGFToken)
	 */
	@Override
	public OptionalGroupNode parse(Parser parser, AGFToken token) {
		// Parse the |-separated arguments until we hit, "]".
		OptionalGroupNode node = new OptionalGroupNode("");

		AGFNode agfNode = new AGFNode("");

		while (!parser.match(AGFTokenType.CLOSESBR)) {
			if (parser.match(AGFTokenType.OR)) {
				parser.consume();
				node.addChild(agfNode);
				agfNode = new AGFNode("");
			}
			agfNode.addChild(parser.parseExpression());
		}
		node.addChild(agfNode);
		// consume last token
		parser.consume(AGFTokenType.CLOSESBR);

		return node;
	}

}