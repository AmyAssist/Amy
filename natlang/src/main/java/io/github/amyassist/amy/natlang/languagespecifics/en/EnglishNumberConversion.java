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

package io.github.amyassist.amy.natlang.languagespecifics.en;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import io.github.amyassist.amy.natlang.languagespecifics.NumberConversion;
import io.github.amyassist.amy.natlang.nl.EndToken;

/**
 * This class is loading language specific numbers
 * 
 * @author Felix Burk
 */
public class EnglishNumberConversion implements NumberConversion {
	/**
	 * maps a string containing a word representation of a number to it's integer counterpart
	 */
	private final Map<String, Integer> wordToNumber;

	/**
	 * this class handels all language specific details such as numbers
	 */
	public EnglishNumberConversion() {
		this.wordToNumber = this.readNumbersFromFile();
	}

	/**
	 * calculates the number from a string of words (should work in english and maybe german or spanish - other
	 * languages need a custom method for this)
	 * 
	 * if the word representation of numbers won't make sense e.g. twenty two ten they will just get added together - i
	 * think this is an okay way of handling this
	 * 
	 * @param subList
	 *                    the sublist containing the list of word representations
	 * @return the calculated number
	 */
	@Override
	public int calcNumber(Iterable<EndToken> subList) {
		int finalNumber = 0;
		int partialNumber = 0;
		for (EndToken t : subList) {
			if (this.wordToNumber.get(t.getContent()) >= 1000) {
				if (partialNumber == 0)
					partialNumber = 1;
				partialNumber *= this.wordToNumber.get(t.getContent());
				finalNumber += partialNumber;
				partialNumber = 0;
			} else {
				if (this.wordToNumber.get(t.getContent()) == 100) {
					if (partialNumber == 0)
						partialNumber = 1;
					partialNumber *= this.wordToNumber.get(t.getContent());
				} else {
					partialNumber += this.wordToNumber.get(t.getContent());
				}
			}
		}
		finalNumber += partialNumber;
		return finalNumber;
	}

	/**
	 * read numbers of a language from a *.natlang file in
	 * /resources/de/unistuttgart/iaas/amyassist/amy/core/natlang/languageSpecifics/en
	 *
	 * @return List of all numbers in the language
	 */
	private final Map<String, Integer> readNumbersFromFile() {
		Map<String, Integer> result = new HashMap<>();

		InputStream grammarFile = this.getClass().getResourceAsStream("englishNumbers.natlang");

		if (grammarFile == null) {
			throw new IllegalStateException("could not find numbers file");
		}

		try (InputStreamReader inputStreamReader = new InputStreamReader(grammarFile, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

			// every line contains the number seperated by ',' and ending with ';'
			Stream<String> lines = bufferedReader.lines();

			Stream<String> matchingLines = lines.filter(l -> l.matches("[a-zA-Z]+:[0-9]*,"));

			Iterator<String> iter = matchingLines.iterator();

			while (iter.hasNext()) {
				String curr = iter.next();
				curr = curr.substring(0, curr.indexOf(','));
				String[] split = curr.split(":");
				if (split.length == 2) {
					result.put(split[0], parseNumber(split[1]));
				} else {
					throw new IllegalStateException("numbers file is in wrong format");
				}
			}

			lines.close();
			matchingLines.close();

		} catch (IOException e) {
			throw new IllegalStateException("number file not found", e);
		}

		return result;
	}

	/**
	 * changes numbers from string to integer and catches + logs potential NumberFormatExceptions
	 * 
	 * @param numberInt
	 *                      the number as string
	 * @return number as integer
	 */
	private int parseNumber(String numberInt) {
		try {
			return Integer.valueOf(numberInt);
		} catch (NumberFormatException e) {
			throw new IllegalStateException("number in numbers file in wrong format", e);
		}
	}

	/**
	 * Get's {@link #wordToNumber wordToNumber}
	 * 
	 * @return wordToNumber
	 */
	@Override
	public Map<String, Integer> getWordToNumber() {
		return this.wordToNumber;
	}
}
