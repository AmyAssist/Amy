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

import java.util.Iterator;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.parselets.MorphemeParselet;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.parselets.ORGroupParselet;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.parselets.OptionalGroupParselet;

/**
 * the AGF Parser implementation
 * 
 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.Parser
 * 
 * @author Felix Burk
 */
public class AGFParser extends Parser {

	/**
	 * 
	 * tells the parser which parslets to call on which token occurence
	 * 
	 * @param tokens
	 *                        the tokens
	 * @param entityToAGF
	 *                        map
	 */
	public AGFParser(Iterator<AGFToken> tokens, Map<String, AGFNode> entityToAGF) {
		super(tokens);

		this.entityToAGF = entityToAGF;

		super.register(AGFTokenType.DOLLAR, new MorphemeParselet());
		super.register(AGFTokenType.WORD, new MorphemeParselet());
		super.register(AGFTokenType.OPENCBR, new MorphemeParselet());
		super.register(AGFTokenType.OPENBR, new ORGroupParselet());
		super.register(AGFTokenType.OPENSBR, new OptionalGroupParselet());

	}

	/**
	 * 
	 * tells the parser which parslets to call on which token occurence
	 * 
	 * @param tokens
	 *                   the tokens
	 */
	public AGFParser(Iterator<AGFToken> tokens) {
		super(tokens);

		super.register(AGFTokenType.DOLLAR, new MorphemeParselet());
		super.register(AGFTokenType.WORD, new MorphemeParselet());
		super.register(AGFTokenType.OPENCBR, new MorphemeParselet());
		super.register(AGFTokenType.OPENBR, new ORGroupParselet());
		super.register(AGFTokenType.OPENSBR, new OptionalGroupParselet());

	}

}
