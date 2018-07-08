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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Lexer for language input from speech
 * 
 * filters bad characters and splits input in words
 * 
 * @author Felix Burk
 */
public class NLLexer {

	/**
	 * contains regex with the corresponding WordTokenType
	 */
	Map<String, WordTokenType> regexTokenType = new HashMap<>();
	{
		this.regexTokenType.put("[a-zA-Z]+", WordTokenType.WORD);
		this.regexTokenType.put("[0-9]+", WordTokenType.NUMBER);
	}

	/**
	 * lexer implemented as Iterator
	 * 
	 * @param toLex
	 *            the stirng to lex
	 */
	public List<WordToken> tokenize(String toLex) {
		List<WordToken> list = new LinkedList<>();
		toLex = toLex.toLowerCase();

		StringBuilder currentWord = new StringBuilder();
		for (int mIndex = 0; mIndex < toLex.length(); mIndex++) {
			char c = toLex.charAt(mIndex);

			switch (Character.getType(c)) {
			case Character.LOWERCASE_LETTER:
				//$FALL-THROUGH$
			case Character.DECIMAL_DIGIT_NUMBER:
				currentWord.append(c);
				break;

			// handles single whitespace characters but not newline, tab or carriage return
			case Character.SPACE_SEPARATOR:
				if (currentWord.length() != 0) {
					list.add(parse(new WordToken(currentWord.toString())));
					currentWord = new StringBuilder();
				} else {
					throw new NLLexerException("more than one whitespace found");
				}
				break;
			default:
				throw new NLLexerException("character not recognized " + c + " stopping");
			}

		}
		list.add(parse(new WordToken(currentWord.toString())));
		return list;
	}

	/**
	 * sets WordTokenType
	 * 
	 * @param next
	 *            the WordToken
	 * @return the parsed WordToken
	 */
	private WordToken parse(WordToken next) {
		for (Map.Entry<String, WordTokenType> entry : this.regexTokenType.entrySet()) {
			if (next.getContent().matches(entry.getKey())) {
				next.setType(entry.getValue());
				return next;
			}
		}
		throw new NLLexerException("no matching word type found");
	}
}
