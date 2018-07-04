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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.JSGFGenerator;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * test for JSGF generator
 * @author Felix Burk
 */
public class JSGFGeneratorTest {
	
	/**
	 * tests if digit rules get changed
	 */
	@Test
	public void testDigit() {
		AGFParser parser = new AGFParser(new AGFLexer("set timer on # minutes"));
		AGFNode node = parser.parseWholeExpression();
		
		JSGFGenerator gen = new JSGFGenerator("test", "test", "test", "test");
		String s = gen.addRule(node, "testi");
		assertEquals(s.trim().replaceAll("\\s{2,}", " "), "public <testi> = set timer on <digit> minutes;");
		
	}
	
	/**
	 * tests a big example, with lots of variations
	 * does not consider whitespace
	 */
	@Test
	public void testBigSyntax() {
		AGFParser parser = new AGFParser(new AGFLexer("set timer on (x|(wa|d)) [# [x] hours] [# minutes] [# seconds] test"));
	
		AGFNode node = parser.parseWholeExpression();
				
		JSGFGenerator gen = new JSGFGenerator("test", "test", "test", "test");
		String s = gen.addRule(node, "test");
		assertEquals(s.replaceAll("\\s*", "").trim(), "public <test> = set timer on (x|(wa|d)) [<digit> [x] hours] [<digit> minutes] [<digit> seconds] test;".replaceAll("\\s*", ""));
		
	}
}
