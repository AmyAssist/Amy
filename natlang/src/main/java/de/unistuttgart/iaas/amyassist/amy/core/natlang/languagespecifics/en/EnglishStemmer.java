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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.en;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.Stemmer;

/**
 * This class implements the porter stemming algorithm see for more information:
 * https://tartarus.org/martin/PorterStemmer/def.txt Only English is supported
 * 
 * @author Lars Buttgereit
 */
public class EnglishStemmer implements Stemmer {
	private String currentWord;

	/**
	 * decide if the character at the position of the current word is a consonant
	 * 
	 * @param pos
	 *                the position of the current word
	 * @return true if a consonant, else false
	 */
	private boolean isConsonant(int pos) {
		switch (this.currentWord.charAt(pos)) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
			return false;
		case 'y':
			if (pos == 0) {
				return true;
			}
			return !isConsonant(pos - 1);
		default:
			return true;
		}
	}

	/**
	 * this method calculates the amount of sequences of vocals and consonants. [C] (VC)^n [V] n is the amount of the
	 * sequences
	 * 
	 * @param lastCharacter
	 *                          the last character from the substring to calculate. For example you need only the amount
	 *                          of sequences from the stem
	 * @return the amount of sequences
	 */
	private int amountOfSequences(int lastCharacter) {
		int changes = 0;
		boolean wasConsonant = true;
		boolean firstConsonants = true;
		for (int i = 0; i < this.currentWord.substring(0, lastCharacter + 1).length(); i++) {
			firstConsonants = (isConsonant(i) && firstConsonants);
			if (isConsonant(i) != wasConsonant && !firstConsonants) {
				wasConsonant = isConsonant(i);
				changes++;
			} else if (!firstConsonants) {
				wasConsonant = isConsonant(i);
			}
		}
		return changes / 2;
	}

	/**
	 * when the word ends with the given string return the end of the stem, else -1
	 * 
	 * @param ending
	 *                   the chracters after the stem
	 * @return the index of the last character from the stem
	 */
	private int stemEnd(String ending) {
		if (this.currentWord.endsWith(ending)) {
			return this.currentWord.length() - ending.length() - 1;
		}
		return -1;
	}

	/**
	 * checks if the stem has a vocal
	 * 
	 * @param endOfStem
	 *                      from the current word
	 * @return true if a vocal is in the stem, else false
	 */
	private boolean vowelInStem(int endOfStem) {
		for (int i = 0; i <= endOfStem; i++) {
			if (!isConsonant(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checks if the end of the word has two consonants
	 * 
	 * @return ture if the word has two consonants at the end else false
	 */
	private boolean endWithDoubleConsonant() {
		return isConsonant(this.currentWord.length() - 1) && this.currentWord
				.charAt(this.currentWord.length() - 1) == this.currentWord.charAt(this.currentWord.length() - 2);
	}

	/**
	 * verified if the stem as the form consonant-vocal-consonant and the third character is no w, x or y
	 * 
	 * @param endOfStem
	 *                      the end of the stem from the current word
	 * @return true if the cvc condition is fulfilled, else false
	 */
	private boolean cvcStem(int endOfStem) {
		if (endOfStem < 2 || !isConsonant(endOfStem) || isConsonant(endOfStem - 1) || !isConsonant(endOfStem - 2)) {
			return false;
		}
		char lastConsonant = this.currentWord.charAt(endOfStem);
		return !(lastConsonant == 'w' || lastConsonant == 'x' || lastConsonant == 'y');

	}

	/**
	 * execute the step 1 from the porter stemmer algorithm
	 */
	private void step1() {
		step1a();
		step1b();
	}

	/**
	 * execute the step 1a from the porter stemmer algorithm
	 */
	private void step1a() {
		if (stemEnd("sses") > -1) {
			this.currentWord = this.currentWord.substring(0, stemEnd("sses") + 1).concat("ss");
		} else if (stemEnd("ies") > -1) {
			this.currentWord = this.currentWord.substring(0, stemEnd("ies") + 1).concat("i");
		} else if ((stemEnd("ss") <= -1) && stemEnd("s") > -1 && this.currentWord.length() > 2) {
			this.currentWord = this.currentWord.substring(0, stemEnd("s") + 1).concat("");
		}
	}

	/**
	 * execute the step 1b from the porter stemmer algorithm
	 */
	private void step1b() {
		if (stemEnd("eed") > -1 && amountOfSequences(stemEnd("eed")) > 0) {
			this.currentWord = this.currentWord.substring(0, stemEnd("eed") + 1).concat("ee");
			return;
		} else if ((stemEnd("eed") <= -1) && stemEnd("ed") > -1 && vowelInStem(stemEnd("ed"))) {
			this.currentWord = this.currentWord.substring(0, stemEnd("ed") + 1).concat("");
		} else if (stemEnd("ing") > -1 && vowelInStem(stemEnd("ing"))) {
			this.currentWord = this.currentWord.substring(0, stemEnd("ing") + 1).concat("");
		} else {
			return;
		}
		if (this.currentWord.endsWith("at") || this.currentWord.endsWith("bl") || this.currentWord.endsWith("iz")) {
			this.currentWord = this.currentWord.concat("e");
		} else if (endWithDoubleConsonant() && !(this.currentWord.endsWith("l") || this.currentWord.endsWith("z")
				|| this.currentWord.endsWith("s"))) {
			this.currentWord = this.currentWord.substring(0, this.currentWord.length() - 1);
		} else if (amountOfSequences(this.currentWord.length() - 1) > 1 && endWithDoubleConsonant()
				&& this.currentWord.charAt(this.currentWord.length() - 1) == 'l') {
			this.currentWord = this.currentWord.substring(0, this.currentWord.length() - 1);
		} else if (amountOfSequences(this.currentWord.length() - 1) == 1 && cvcStem(this.currentWord.length() - 1)) {
			this.currentWord = this.currentWord.concat("e");
		}
	}

	/**
	 * execute the step 5 from the porter stemmer algorithm without (m > 1 and *d and *L)
	 */
	private void step5() {
		int wordLength = this.currentWord.length();
		if (this.currentWord.endsWith("e") && ((amountOfSequences(wordLength - 2) == 1 && !cvcStem(wordLength - 2))
				|| amountOfSequences(wordLength - 2) > 1)) {
			this.currentWord = this.currentWord.substring(0, wordLength - 1);
		}
	}

	/**
	 * this method stem the input string. Only step 1 and step 5 are implemented. The other are not necessary for our
	 * project. detailed info for the different steps can you read here:
	 * https://tartarus.org/martin/PorterStemmer/def.txt
	 * 
	 * @param input
	 *                  a string with one or more words
	 * @return the stemmed string out of the input string
	 */
	@Override
	public String stem(String input) {
		if (input != null) {
			String[] inputWords = input.split("\\s+");
			String output = "";
			for (String word : inputWords) {
				this.currentWord = word.toLowerCase();
				step1();
				step5();
				output = output.concat(this.currentWord);
			}
			return output;
		}
		return "";
	}
}
