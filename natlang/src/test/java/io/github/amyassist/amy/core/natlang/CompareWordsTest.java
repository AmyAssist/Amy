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

package io.github.amyassist.amy.core.natlang;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.amyassist.amy.natlang.util.CompareWords;

/**
 * Tests the word comperator
 * @author Felix Burk
 */
public class CompareWordsTest {
	
	@Test
	public void testWords() {
		//levensthein tests
		assertEquals(1, CompareWords.wordDistance("test", "testi"));
		assertEquals(0, CompareWords.wordDistance("test", "test"));
		assertEquals(5, CompareWords.wordDistance("", "testi"));
		assertEquals(4, CompareWords.wordDistance("t", "testi"));
		assertEquals(3, CompareWords.wordDistance("te", "testi"));
		assertEquals(2, CompareWords.wordDistance("tes", "testi"));

		//damaur tests - switching letters
		assertEquals(2, CompareWords.wordDistance("an act", "a cat"));
		assertEquals(1, CompareWords.wordDistance("an act", "an cat"));
		assertEquals(1, CompareWords.wordDistance("tesit", "testi"));
		assertEquals(4, CompareWords.wordDistance("ABZ", "AABBCC"));
		assertEquals(1, CompareWords.wordDistance("one", "on"));
	}

}
