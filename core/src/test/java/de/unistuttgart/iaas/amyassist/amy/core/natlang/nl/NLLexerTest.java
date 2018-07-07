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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test for NL Lexer
 * @author Felix Burk
 */
public class NLLexerTest {
	
	
	/**
	 * test normal words and numbers
	 */
	@Test
	public void testWords() {
		String[] words = {
				"what", "the" , "99", "this", "is", "a", "test"
		};
		checkInput(words);
	}
	
	/**
	 * tests bad chars
	 */
	public void testBadChars() {
		List<Character> badChars = new ArrayList<>(128);
		
		for(char c=0; c < 128; c++) {
			//every ascii character except numbers of letters
			if((c != 32 && c < 48) || (57 < c && c < 65) || (90 < c && c < 97) || 122 < c) {
				badChars.add(c);
			}
		}

		for(Character c : badChars) {	
			NLLexer lexer = new NLLexer(c.toString());
			assertThrows(NLLexerException.class, () -> lexer.next());
		}
	}

	
	/**
	 * standard test for the simple NL Lexer class
	 * @param input 
	 */
	public void checkInput(String[] input) {
		NLLexer lexer = new NLLexer(String.join(" ", input));
		
		int i=0;
		while(lexer.hasNext()) {
			assertThat(lexer.next().getContent(), is(input[i]));
			i++;
		}
	}
	
	/**
	 * standard test, more to come
	 */
	@Test
	public void testTypes() {
		NLLexer lex = new NLLexer("wajjo 9 0 oh 99");
		
		assertThat(lex.next().getType(), is(WordTokenType.WORD));
		assertThat(lex.next().getType(), is(WordTokenType.NUMBER));
		assertThat(lex.next().getType(), is(WordTokenType.NUMBER));
		assertThat(lex.next().getType(), is(WordTokenType.WORD));
		assertThat(lex.next().getType(), is(WordTokenType.NUMBER));

		
	}
}
