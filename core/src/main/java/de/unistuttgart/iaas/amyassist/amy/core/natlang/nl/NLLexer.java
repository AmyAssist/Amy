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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.ChooseLanguage;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.INumberConversion;

/**
 * Lexer for language input from speechs
 * 
 * filters bad characters and splits input in words
 * 
 * @author Felix Burk
 */
public class NLLexer {

	private final Logger logger = LoggerFactory.getLogger(NLLexer.class);

	/**
	 * contains regex with the corresponding WordTokenType
	 */
	private final Map<String, WordTokenType> regexTokenType = new HashMap<>();

	private ChooseLanguage language;

	private INumberConversion numberConversion;

	/**
	 * this class handles natural language input of any type
	 * 
	 * @param iNumberConversion
	 *            language specific details
	 * 
	 */
	public NLLexer(ChooseLanguage language) {
		this.language = language;
		this.numberConversion = language.getNumberConversion();
		if (!this.numberConversion.getWordToNumber().isEmpty()) {
			String regex = "((\\b" + String.join("\\b|\\b", this.numberConversion.getWordToNumber().keySet())
					+ "\\b)\\s{0,1})+";
			this.regexTokenType.put(regex, WordTokenType.NUMBER);
		} else {
			this.logger.error("problem with numbers file, written numbers will not be recognized");
		}

		this.regexTokenType.put("[a-zA-Z]+", WordTokenType.WORD);
		this.regexTokenType.put("[0-9]+", WordTokenType.NUMBER);
	}

	/**
	 * lexer implemented as Iterator
	 * 
	 * @param nlInput
	 *            the stirng to lex
	 * @return returns processed list of WordTokens
	 */
	public List<WordToken> tokenize(String nlInput) {
		List<WordToken> list = new LinkedList<>();
		String toLex = nlInput.toLowerCase().trim();
		toLex = this.language.getTimeUtility().formatTime(toLex);
		toLex = this.language.getTimeUtility().formatDate(toLex);

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
		return concatNumbers(list);
	}

	/**
	 * this method changes WordToken content of numbers to decimal numbers and adds numbers together not surrounded by
	 * words
	 * 
	 * @param list
	 *            of all tokens containing potential written numbers that have to be merged
	 * @return final list containing the correct numbers
	 */
	private List<WordToken> concatNumbers(List<WordToken> list) {
		List<WordToken> result = new ArrayList<>();
		List<WordToken> numbers = new ArrayList<>();
		for (WordToken wordToken : list) {
			if (wordToken.getType() == WordTokenType.NUMBER && wordToken.getContent().matches("[a-zA-Z]+")) {
				numbers.add(wordToken);
			} else {
				if (!numbers.isEmpty()) {
					result.add(this.fromNumbers(numbers));
					numbers.clear();
				}

				result.add(wordToken);
			}
		}
		if (!numbers.isEmpty()) {
			result.add(this.fromNumbers(numbers));
		}
		return result;
	}

	private WordToken fromNumbers(List<WordToken> numbers) {
		int finalNumber = this.numberConversion.calcNumber(numbers);
		WordToken t = new WordToken(String.valueOf(finalNumber));
		t.setType(WordTokenType.NUMBER);
		return t;
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
