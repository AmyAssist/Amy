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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Lists;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.LanguageSpecifics;

/**
 * Test for NL Lexer
 * 
 * @author Felix Burk
 */
public class NLLexerTest {
	
	private LanguageSpecifics lang;
	
	/**
	 * handles language specifics setup
	 */
	@BeforeEach
	public void setup() {
		this.lang = new LanguageSpecifics();
	}

	/**
	 * stream of bad characters that should not be matched
	 * @return the stream
	 */
	public static Stream<Character> badCharacters() {
		// every ascii character except numbers of letters
		return IntStream.range(0, 128).mapToObj(i -> (char) i)
				.filter(c -> (c != 32 && c < 48) || (57 < c && c < 65) || (90 < c && c < 97) || 122 < c);
	}
	
	/**
	 * tests bad chars
	 * @param badCharacter to test
	 */
	@ParameterizedTest
	@MethodSource("badCharacters")
	public void testBadChars(Character badCharacter) {
		NLLexer lexer = new NLLexer(this.lang);
		assertThrows(NLLexerException.class, () -> lexer.tokenize(badCharacter.toString()));
	}
	
	/**
	 * test normal words and numbers
	 * @return Stream of lists
	 */
	public static Stream<List<String>> testWords() {
		return Stream.of(Arrays.asList("what", "the", "99", "this", "is", "a", "test"));
	}
	

	/**
	 * tests if content of WordTokens matches the nl input
	 * 
	 * @param input word to test
	 */
	@ParameterizedTest
	@MethodSource("testWords")
	public void checkInput(List<String> input) {
		NLLexer lexer = new NLLexer(this.lang);
		List<WordToken> tokenize = lexer.tokenize(String.join(" ", input));
		assertThat(Lists.transform(tokenize, WordToken::getContent), is(input));
	}

	/**
	 * standard test to check if types are correct
	 */
	@Test
	public void testTypes() {
		NLLexer lex = new NLLexer(this.lang);
		List<WordToken> tokenize = lex.tokenize("wajjo 9 0 oh 99 oh one");
		assertThat(Lists.transform(tokenize, WordToken::getType), 
				contains(WordTokenType.WORD, WordTokenType.NUMBER,
				WordTokenType.NUMBER, WordTokenType.WORD, WordTokenType.NUMBER,
				WordTokenType.WORD, WordTokenType.NUMBER));
	}
	
	/**
	 * tests concatination of numbers
	 * because internally written numbers e.g. two hundred 
	 * get recognized as two WordTokens, thats why the NLLexer uses a concat method
	 * to merge the tokens "two" and "hundred" into a new token containing "200"
	 * with the help of number conversions
	 */
	@Test
	public void testConcatNumbers() {
		NLLexer lex = new NLLexer(this.lang);
		List<WordToken> tokenize = lex.tokenize("test one hundred twenty two test");
		
		assertThat(Lists.transform(tokenize, WordToken::getType), 
				contains(WordTokenType.WORD, WordTokenType.NUMBER,
				WordTokenType.WORD));
		assertThat(new Boolean(true), is(tokenize.get(1).getContent().equals("122")));
	}
	
	/**
	 * stream of all numbers that have to be present to calculate the right number
	 * @return the stream
	 */
	public static Stream<Integer> fileNumbers(){
		Stream<Integer> zeroToNineteen = IntStream.range(0, 20).boxed();
		Stream<Integer> until90 = IntStream.iterate(20, i -> i + 10).limit(8).boxed();
		Stream<Integer> untilMillion = IntStream.of(100,1000000).boxed();
		return Stream.concat(
			      Stream.concat(zeroToNineteen, until90), untilMillion);
	}
	
	/**
	 * tests if all numbers are read correctly from the file
	 * @param number to check
	 */
	@ParameterizedTest
	@MethodSource("fileNumbers")
	public void testNumbersFileReader(int number) {
		Map<String, Integer> numbers = this.lang.wordToNumber;
		assertThat(new Boolean(true), is(numbers.values().contains(number)));
	}
	
	/**
	 * returns pair of strings and their matching digit representation of the containting number
	 * @return the pair
	 */
	public static Stream<Pair<String, Integer>> numberStringToInt() {
		return Stream.of(Pair.of("10", 10), Pair.of("twenty two", 22), Pair.of("three thousand", 3000),
				Pair.of("twenty two million", 22000000), Pair.of("twenty two million forty six", 22000046),
				Pair.of("ten", 10), Pair.of("twenty two million thirty thousand nine hundred forty six", 22030946),
				Pair.of("twenty two million nine hundred two thousand seven hundred forty nine", 22902749));
	}	
	
	/**
	 * tests the pair stream of numberStringToInt
	 * @param pair to test
	 */
	@ParameterizedTest
	@MethodSource("numberStringToInt")
	public void testNumberConversion(Pair<String, Integer> pair) {
		NLLexer lex = new NLLexer(this.lang);
		List<WordToken> numbers = lex.tokenize(pair.getLeft());
		assertThat(new Boolean(true), is(numbers.get(0).getContent().equals((String.valueOf(pair.getRight())))));

	}
	
}