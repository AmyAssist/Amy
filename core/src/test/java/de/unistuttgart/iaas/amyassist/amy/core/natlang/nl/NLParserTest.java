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
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * test method for NL Parser, uses predefined grammars and nl
 * 
 * @author Felix Burk
 */
public class NLParserTest {

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
	 * tests hard coded nls and grammars
	 */
	@ParameterizedTest
	@MethodSource("testData")
	public void testParser(Pair<String, AGFNode> testcase) {
		NLParser nlParser = new NLParser(grammars);
		NLLexer lex = new NLLexer();

		List<WordToken> tokenize = lex.tokenize(testcase.getLeft());
		assertThat(nlParser.matchingNode(tokenize), equalTo(testcase.getRight()));
	}

	@Test
	public void testWrongGrammar() {
		NLParser nlParser = new NLParser(grammars);
		NLLexer lex = new NLLexer();
		List<WordToken> tokenize = lex.tokenize("wrong grammar");
		assertThrows(NLParserException.class, () -> nlParser.matchingNodeIndex(tokenize));
	}

}
