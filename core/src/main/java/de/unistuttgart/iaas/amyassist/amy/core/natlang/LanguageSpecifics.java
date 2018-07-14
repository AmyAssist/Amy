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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.nl.WordToken;

/**
 * This class is loading language specific 
 * words from *.natlang files
 * 
 * like numbers 
 * 
 * @author Felix Burk
 */
public class LanguageSpecifics {
	
	private final Logger logger = LoggerFactory.getLogger(LanguageSpecifics.class);

	/**
	 * maps a string containing a word representation of a number
	 * to it's integer counterpart
	 */
	public final Map<String, Integer> wordToNumber;
	
	/**
	 * this class handels all language specific details
	 * such as numbers
	 */
	public LanguageSpecifics() {
		this.wordToNumber = this.readNumbersFromFile();
	}

	
	/**
	 * calculates the number from a string of words
	 * (should work in english and maybe german or spanish
	 * - other languages need a custom method for this)
	 * 
	 * if the word representation of numbers won't make sense
	 * e.g. twenty two ten 
	 * they will just get added together - i think this is an okay way of handling this
	 * 
	 * @param subList the sublist containing the list of word representations 
	 * @return the calculated number
	 */
	public int calcNumber(Iterable<WordToken> subList) {
		int finalNumber = 0;
		int partialNumber = 0;
		for(WordToken t : subList) {
			if(this.wordToNumber.get(t.getContent()) >= 1000) {
				if(partialNumber == 0) partialNumber = 1;
				partialNumber *= this.wordToNumber.get(t.getContent());
				finalNumber += partialNumber;
				partialNumber = 0;
			}else {
				if(this.wordToNumber.get(t.getContent()) == 100) {
					if(partialNumber == 0) partialNumber = 1;
					partialNumber *= this.wordToNumber.get(t.getContent());
				}else {
					partialNumber += this.wordToNumber.get(t.getContent());
				}
			}
		}
		finalNumber += partialNumber;
		return finalNumber;
	}
	
	/**
	 * read numbers of a language from a *.natlang file in 
	 *	/resources/langs/numbers/*.natlang
	 *
	 * @return List of all numbers in the language
	 */
	private final Map<String, Integer> readNumbersFromFile(){
		Map<String, Integer> result = new HashMap<>();
		String[] stringNmbRep;
		
		InputStream grammarFile = this.getClass().getResourceAsStream("englishNumbers.natlang");
		
		if(grammarFile == null) {
			this.logger.error("could not find numbers file");
			return new HashMap<>();
		}
		
		try (
		    InputStreamReader inputStreamReader = new InputStreamReader(grammarFile, "UTF-8");
		    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){
		   
		    //first line contains all numbers seperated by ',' and ending with ';'
		    String s = bufferedReader.readLine();
		    
		    String [] temp = s.split(";");
		    
		    if(temp.length == 1) {
		    	stringNmbRep = temp[0].split(",");
		    	for(String textNumberRep : stringNmbRep) {
		    		String[] split = textNumberRep.split(":");
		    		if(split.length == 2) {
		    			result.put(split[0], parseNumber(split[1]));
		    		}else {
				    	this.logger.error("numbers file is in wrong format");
		    		}
		    	}
		    }else {
		    	this.logger.error("numbers file is in wrong format");
		    }
		    
		} catch (IOException e) {
			this.logger.error(String.format("number file not found %s", e));
		}
		
		return result;
	}

	/**
	 * changes numbers from string to integer
	 * and catches + logs potential NumberFormatExceptions
	 * 
	 * @param numberInt the number as string
	 * @return number as integer
	 */
	private int parseNumber(String numberInt) {
		try {
			return Integer.valueOf(numberInt);
			}catch(NumberFormatException e) {
				this.logger.error(String.format("number in numbers file in wrong format %s" , numberInt));
			}
		return Integer.MAX_VALUE;
	}

}
