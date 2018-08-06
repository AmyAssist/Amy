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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languageSpecifics.en.EnglishNumberConversion;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languageSpecifics.en.EnglishStemmer;

/**
 * test method for NL Parser, uses predefined grammars and nl
 * 
 * @author Felix Burk
 */
public class NLParserTest {
	
	private EnglishNumberConversion lang;
	
	/**
	 * handles language specifics setup
	 */
	@BeforeEach
	public void setup() {
		this.lang = new EnglishNumberConversion();
	}

	/**
	 * contains a stream of pairs, containing natural language strings and matching AGFNodes
	 * @return Stream containing pairs of natural language strings and matching AGFNodes
	 */
	public static Stream<Pair<String, AGFNode>> testData() {
		AGFNode gram = grammars.get(0);
		AGFNode weather = grammars.get(1);
		AGFNode badGram = grammars.get(5);
		
		return Stream.of(Pair.of("test 10 minutes", gram), Pair.of("test 10 minute", gram), Pair.of("weather", weather),
				Pair.of("weather today", weather), Pair.of("delete alarm 10", grammars.get(2)),
				Pair.of("when does timer 10 ring", grammars.get(3)), Pair.of("spotify play", grammars.get(4)),
				Pair.of("alarm clock create timer on 10 hours 2 minute 10 seconds", badGram),
				Pair.of("alarm clock create timer on 10 hours 11 seconds", badGram));
	}

	private static List<AGFNode> grammars = Arrays.asList(
			new AGFParser(new AGFLexer("test [# (minutes|minute)]")).parseWholeExpression(),
			new AGFParser(new AGFLexer("weather [today]")).parseWholeExpression(),
			new AGFParser(new AGFLexer("delete (alarm|timer) #")).parseWholeExpression(),
			new AGFParser(new AGFLexer("when (does|is) timer # (ringing|ring)")).parseWholeExpression(),
			new AGFParser(new AGFLexer("spotify play")).parseWholeExpression(),
			new AGFParser(new AGFLexer("alarm clock (set|create) timer (for|on) "
					+ "[# (hour|hours)] [# (minute|minutes)] [# (second|seconds)]")).parseWholeExpression());
	
	/**
	 * this methods tests the grammar 
	 * "alarm clock (set|create) timer (for|on) [# (hour|hours)] [# (minute|minutes)] [# (second|seconds)]"
	 * which needs traces back
	 * 
	 * for example: natlang input = "alarm clock create timer on 10 hours 11 seconds"
	 * 
	 * here the first sign in the second optional group matches, but not the or group inside the optional group (minute|minutes)
	 * this means we need to set back the counter to test for the last optional group
	 */
	@Test
	public void testGrammarWithTraceBack() {
		AGFNode node = new AGFParser(new AGFLexer("alarm clock (set|create) timer (for|on) "
				+ "[# (hour|hours)] [# (minute|minutes)] [# (second|seconds)]")).parseWholeExpression();
		List<AGFNode> list = new ArrayList<>();
		list.add(node);
		NLParser nlParser = new NLParser(list, new EnglishStemmer(), false);
		NLLexer lex = new NLLexer(this.lang);
		assertThat(nlParser.matchingNode(lex.tokenize("alarm clock create timer on 10 hours 1 minute 11 seconds")), equalTo(node));
		assertThat(nlParser.matchingNode(lex.tokenize("alarm clock create timer on 10 hours 11 seconds")), equalTo(node));
		assertThat(nlParser.matchingNode(lex.tokenize("alarm clock create timer on 11 seconds")), equalTo(node));
		assertThat(nlParser.matchingNode(lex.tokenize("alarm clock create timer on 1 minute")), equalTo(node));
		assertThat(nlParser.matchingNode(lex.tokenize("alarm clock create timer on 22 hours")), equalTo(node));
		assertThat(nlParser.matchingNode(lex.tokenize("alarm clock create timer on 10 hours 1 minutes")), equalTo(node));
		assertThat(nlParser.matchingNode(lex.tokenize("alarm clock create timer on 1 minute 11 seconds")), equalTo(node));

	}
	
	/**
	 * tests multiple nested groups
	 */
	@Test
	public void testNestedGroups() {
		AGFNode node = new AGFParser(new AGFLexer("this [text make (no|[hello] (#|nothing|nothing nothing [else|to do]))] sense")).parseWholeExpression();
		List<AGFNode> list = new ArrayList<>();
		list.add(node);
		NLParser nlParser = new NLParser(list, new EnglishStemmer(), false);
		NLLexer lex = new NLLexer(this.lang);
		assertThat(nlParser.matchingNode(lex.tokenize("this text make hello nothing nothing else sense")), equalTo(node));
	}
	
	/**
	 * this checks if longer options in optional groups are used
	 */
	@Test
	public void testPreferLongOptionsOP() {
		AGFNode node = new AGFParser(new AGFLexer("this grammar is [very |very very] bad")).parseWholeExpression();
		List<AGFNode> list = new ArrayList<>();
		list.add(node);
		NLParser nlParser = new NLParser(list, new EnglishStemmer(), false);
		NLLexer lex = new NLLexer(this.lang);

		assertThat(nlParser.matchingNode(lex.tokenize("this grammar is very very bad")), equalTo(node));
		assertThat(nlParser.matchingNode(lex.tokenize("this grammar is bad")), equalTo(node));
		assertThat(nlParser.matchingNode(lex.tokenize("this grammar is very bad")), equalTo(node));
	}
	
	/**
	 * this checks if longer options in or groups are used
	 */
	@Test
	public void testPreferLongOptionsOR() {
		AGFNode node = new AGFParser(new AGFLexer("this grammar is (very |very very) bad")).parseWholeExpression();
		List<AGFNode> list = new ArrayList<>();
		list.add(node);
		NLParser nlParser = new NLParser(list, new EnglishStemmer(), false);
		NLLexer lex = new NLLexer(this.lang);

		assertThat(nlParser.matchingNode(lex.tokenize("this grammar is very very bad")), equalTo(node));
		assertThat(nlParser.matchingNode(lex.tokenize("this grammar is very bad")), equalTo(node));
	}
	
	
	/**
	 * tests hard coded nls and grammars
	 * @param testcase all pairs of natlang strings and grammars
	*/
	@ParameterizedTest
	@MethodSource("testData")
	public void testParser(Pair<String, AGFNode> testcase) {
		NLParser nlParser = new NLParser(grammars, new EnglishStemmer(), false);
		NLLexer lex = new NLLexer(this.lang);

		List<WordToken> tokenize = lex.tokenize(testcase.getLeft());
		assertThat(nlParser.matchingNode(tokenize), equalTo(testcase.getRight()));
	}
	
	
	/**
	 * contains natural language strings not matching any grammars
	 * @return a stream of strings, containing natural language strings
	 */
	public static Stream<String> testWrongData() {
		return Stream.of("test 10 blah minutes", "test 10 minute blah blah", "blah blah test 10 minute");
	}

	/**
	 * tests wrong hard coded natural language strings not matching any grammar
	 * @param natLang natural language strings
	*/
	@ParameterizedTest
	@MethodSource("testWrongData")
	public void testWrongGrammar(String natLang) {
		NLParser nlParser = new NLParser(grammars, new EnglishStemmer(), false);
		NLLexer lex = new NLLexer(this.lang);
		List<WordToken> tokenize = lex.tokenize(natLang);

		assertThrows(NLParserException.class, () -> nlParser.matchingNodeIndex(tokenize));

	}

}