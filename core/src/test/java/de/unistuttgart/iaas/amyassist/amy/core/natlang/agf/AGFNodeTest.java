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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * Tests some convenience methods used by AGFNode objects
 * 
 * @author Felix Burk
 */
public class AGFNodeTest {
	
	/**
	 * list of nodes to test
	 */
	List<AGFNode> nodesToTest;
	
	/**
	 * initializes list of AGFNodes to test against
	 */
	@BeforeEach
	public void initTests() {
		this.nodesToTest = new ArrayList<>();
		this.nodesToTest.add(new AGFParser(new AGFLexer("test [x|y]")).parseWholeExpression());
		this.nodesToTest.add(new AGFParser(new AGFLexer("test [x|y]")).parseWholeExpression());
		this.nodesToTest.add(new AGFParser(new AGFLexer("test [x|y] testi")).parseWholeExpression());
		this.nodesToTest.add(new AGFParser(new AGFLexer("test [x|testi]")).parseWholeExpression());
		this.nodesToTest.add(new AGFParser(
				new AGFLexer("(set|create) timer (for|on) [{num} (hour|hours)] [{num} (minute|minutes)] [{num} (second|seconds)]")).parseWholeExpression());
		this.nodesToTest.add(new AGFParser(
				new AGFLexer("(set|create) timer (for|on) [{num} (hour|hours)] [{num} (minute|minutes)] [{num} (second|second)]")).parseWholeExpression());

	}
	
	
	/**
	 * tests the equal to method
	 */
	@Test
	public void testEqualTo() {
		for(AGFNode node : this.nodesToTest) {
			assertEquals(true, node.equalTo(node));
		}
		
		assertEquals(true, this.nodesToTest.get(0).equalTo(this.nodesToTest.get(1)));
		assertEquals(false, this.nodesToTest.get(0).equalTo(this.nodesToTest.get(2)));
		assertEquals(false, this.nodesToTest.get(0).equalTo(this.nodesToTest.get(3)));
		assertEquals(false, this.nodesToTest.get(4).equalTo(this.nodesToTest.get(5)));


	}

}
