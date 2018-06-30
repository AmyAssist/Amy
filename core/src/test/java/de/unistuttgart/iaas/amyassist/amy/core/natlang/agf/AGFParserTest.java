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

import java.util.List;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * TODO: Description
 * @author Felix Burk
 */
public class AGFParserTest {
	
	
	@Test
	public void test() {
		AGFLexer lex = new AGFLexer();
		List<AGFToken> list = lex.tokenize("hello # (a(b|x|d)|#)");
		AGFParser parser = new AGFParser(list.iterator());
		
	
		AGFNode node = parser.parseWholeExpression();
		
		System.out.println(node.printSelf());
		
	}

}
