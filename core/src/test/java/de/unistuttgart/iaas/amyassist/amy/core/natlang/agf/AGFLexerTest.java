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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;



/**
 * Test lexer implementation
 * @author Felix Burk
 */
public class AGFLexerTest {
	
	
	private List<AGFToken> getListFromLexer(String toLex) {
		AGFLexer lex = new AGFLexer(toLex);
		List<AGFToken> result = new ArrayList<>();
		lex.forEachRemaining(result::add);
		return result;
	}

	/**
	 * tests a single word and its content
	 */
	@Test
	public void singleWord() {
		List<AGFToken> toCheck = getListFromLexer("x");
		assertThat(toCheck.get(0).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(0).content, equalTo("x"));
		
		toCheck = getListFromLexer("test");
		assertThat(toCheck.get(0).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(0).content, equalTo("test"));

	}
	
	/**
	 * tests multiple words
	 */
	@Test
	public void words() {
		List<AGFToken> toCheck = getListFromLexer("hello x y a b");
		assertThat(toCheck.get(0).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(1).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(2).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(3).type, equalTo(AGFTokenType.WORD));		
		assertThat(toCheck.get(4).type, equalTo(AGFTokenType.WORD));
		
		assertThat(toCheck.get(0).content, equalTo("hello"));
		assertThat(toCheck.get(1).content, equalTo("x"));
		assertThat(toCheck.get(2).content, equalTo("y"));
		assertThat(toCheck.get(3).content, equalTo("a"));		
		assertThat(toCheck.get(4).content, equalTo("b"));
	}
	
	/**
	 * tests optional groups like [x|y] and [x]
	 */
	@Test
	public void optionalOr() {
		List<AGFToken> toCheck = getListFromLexer("hello [x|y]");
		assertThat(toCheck.get(0).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(1).type, equalTo(AGFTokenType.OPENSBR));
		assertThat(toCheck.get(2).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(3).type, equalTo(AGFTokenType.OR));		
		assertThat(toCheck.get(4).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(5).type, equalTo(AGFTokenType.CLOSESBR));
		
		
		toCheck = getListFromLexer("hello [x|y] [z]");
		assertThat(toCheck.get(6).type, equalTo(AGFTokenType.OPENSBR));
		assertThat(toCheck.get(7).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(8).type, equalTo(AGFTokenType.CLOSESBR));

	}
	
	/**
	 * tests or groups like (x|y) and (x)
	 */
	@Test
	public void orGroups() {
		List<AGFToken> toCheck = getListFromLexer("hello (x|y)");
		assertThat(toCheck.get(0).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(1).type, equalTo(AGFTokenType.OPENBR));
		assertThat(toCheck.get(2).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(3).type, equalTo(AGFTokenType.OR));		
		assertThat(toCheck.get(4).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(5).type, equalTo(AGFTokenType.CLOSEBR));
		
		
		toCheck = getListFromLexer("hello [x|y] (z)");
		assertThat(toCheck.get(6).type, equalTo(AGFTokenType.OPENBR));
		assertThat(toCheck.get(7).type, equalTo(AGFTokenType.WORD));
		assertThat(toCheck.get(8).type, equalTo(AGFTokenType.CLOSEBR));

	}
	
	/**
	 * check all ascii characters not compliant with AGF throw an exception
	 */
	@Test
	public void exceptions() {
		List<String> ascii = new ArrayList<>();
		char c = 0;
		for(c = 0; c <= 127; c++) {
			ascii.add(String.valueOf(c));
		}
		
		//remove characters okay to use
		for(c = 'a'; c <= 'z'; c++) {
			ascii.remove(String.valueOf(c));
		}
		for(c = 'A'; c <= 'Z'; c++) {
			ascii.remove(String.valueOf(c));
		}

		ascii.remove("(");
		ascii.remove(")");
		ascii.remove("[");
		ascii.remove("]");
		ascii.remove("|");
		ascii.remove("{");
		ascii.remove("}");
		//normal whitespace
		ascii.remove(32);
		
		for(String s : ascii) {
			assertThrows(AGFLexerException.class, () -> getListFromLexer(s));
		}
	}
	

}
