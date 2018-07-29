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

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

/**
 * This class implements the porter stemming algorithm see for more information:
 * https://tartarus.org/martin/PorterStemmer/
 * 
 * @author Lars Buttgereit
 */
public class Stemming {
	private String[] inputWords;
	private String output;
	private char[] currentWord;
	
	
	public Stemming() {
		
	}

	private boolean isConsonant(int pos) {
		return false;
	}
	
	private int numberOfSequences() {
		return -1;
	}
	
	private boolean vowelInStem() {
		return false;
	}
	
	private boolean cvc(int pos) {
		return false;
	}

	private boolean doubleConsonant(int pos) {
		return false;
	}
	
	private void step1() {
		
	}
	
	public String stem(String input) {
		if(input != null) {
		this.inputWords = input.split("\\s+");
		this.output = "";
		for(int i = 0; i < this.inputWords.length; i++) {
			this.currentWord = this.inputWords[i].toCharArray();
			step1();
			this.output = this.output.concat(new String(this.currentWord));	
		}
		return this.output;
		}
		return "";
	}
}
