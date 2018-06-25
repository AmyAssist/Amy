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

package de.unistuttgart.iaas.amyassist.amy.naturallang;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

/**
 * An object used to describe commands relating to an existing 
 * grammar by some plugin
 * 
 * @author Felix Burk
 */
public class GrammarCommand {
	
	/**
	 * list of all numbers in the same order as 
	 * specified in the grammar
	 */
	private List<Integer> numbers;
	
	/**
	 * list of optional groups and if they are being used by this command
	 */
	private HashMap<String, Boolean> optionalGroups;
	
	/**
	 * list of all OR groups and which word/group of them is used in this command
	 */
	private HashMap<String, String> wordUsedInOrGroup;
	
	/**
	 * contains the whole command as a string
	 */
	private String wholeCommand;
	
	/**
	 * contains the longest matching grammar
	 */
	private String matchingGrammar;
	
	/**
	 * contains the matching keyword
	 */
	private String matchingKeyword;

	/**
	 * returns if an optional group has been used in this command
	 * groups may be several words or single words. Everything surrounded
	 * by [ ] parentheses
	 * 
	 * @param group of words or single word to check if used
	 * @return boolean value if used
	 */
	public boolean isOptionalGroupUsed(String group){
		//return this.optionalGroups.get(group).booleanValue();
		throw new NotImplementedException("isOptionalGroupUsed() is not implemented yet");
	}
	
	/**
	 * returns the correct word used in an or group specified
	 * in your grammar
	 * 
	 * @param group string used in your grammar
	 * @return String the word used in the specified group
	 */
	public String wordUsedInOrGroup(String group){
		//return this.wordUsedInOrGroup.get(group);
		throw new NotImplementedException("wordUsedInOrGroup() is not implemented yet");
	}
	
	/**
	 * 
	 * @return whole command in string format, lower case
	 */
	public String getWholeCommand() {
		return this.wholeCommand.toLowerCase();
	}
	
	/**
	 * Get's {@link #matchingGrammar matchingGrammar}
	 * @return  matchingGrammar
	 */
	public String getMatchingGrammar() {
		return this.matchingGrammar;
	}
	
	/**
	 * Get's {@link #matchingKeyword matchingKeyword}
	 * @return  matchingKeyword
	 */
	public String getMatchingKeyword() {
		return this.matchingKeyword;
	}
	
	// --- getter and setters for pre defined rules ---
	
	/**
	 * returns all numbers as List<Integer>
	 * order is the same as specified in your grammar
	 * @return List<Integer> of numbers used in the command
	 */
	public List<Integer> getNumbers(){
		return this.numbers;
	}
	
	
	/**
	 * Set's {@link #numbers numbers}
	 * @param numbers numbers
	 */
	void setNumbers(List<Integer> numbers){
		this.numbers = numbers;
	}
	
	// --- getter and setters for pre defined rules end ---

	/**
	 * Set's {@link #wholeCommand wholeCommand}
	 * @param wholeCommand  wholeCommand
	 */
	void setWholeCommand(String wholeCommand) {
		this.wholeCommand = wholeCommand;
	}

	/**
	 * Set's {@link #optionalGroups optionalGroups}
	 * @param optionalGroups  groups
	 */
	void setOptionalGroups(HashMap<String, Boolean> optionalGroups) {
		this.optionalGroups = optionalGroups;
	}

	/**
	 * Set's {@link #wordUsedInOrGroup wordUsedInOrGroup}
	 * @param wordUsedInOrGroup  wordUsedInOrGroup
	 */
	void setWordUsedInOrGroup(HashMap<String, String> wordUsedInOrGroup) {
		this.wordUsedInOrGroup = wordUsedInOrGroup;
	}
	
	/**
	 * Set's {@link #matchingGrammar matchingGrammar}
	 * @param matchingGrammar  matchingGrammar
	 */
	void setMatchingGrammar(String matchingGrammar) {
		this.matchingGrammar = matchingGrammar;
	}
	
	/**
	 * Set's {@link #matchingKeyword matchingKeyword}
	 * @param matchingKeyword  matchingKeyword
	 */
	void setMatchingKeyword(String matchingKeyword) {
		this.matchingKeyword = matchingKeyword;
	}
	
	/**
	 * custom exception, thrown if multiple keywords match
	 * with this GrammarCommandType

	 * @author Felix Burk
	 */
	public class KeywordsException extends Exception {
	    public KeywordsException(String message) {
	        super(message);
	    }
	}
	
}
