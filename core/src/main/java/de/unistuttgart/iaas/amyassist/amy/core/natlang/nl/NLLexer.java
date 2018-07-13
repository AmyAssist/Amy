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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	
	private final Map<String, Integer> wordToNumber;

	/**
	 * 
	 */
	public NLLexer() {
		this.wordToNumber = this.readNumbers();
		
		if(!this.wordToNumber.isEmpty()) {
			String regex = "((\\b"+ String.join("\\b|\\b", this.wordToNumber.keySet()) + "\\b)\\s{0,1})+";
			this.regexTokenType.put(regex, WordTokenType.NUMBER);
		}else {
			this.logger.error("problem with numbers file, written numbers will not be recognized");
		}
		
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
		return concatNumbers(list);
	}

	/**
	 * this method changes WordToken content of numbers to decimal numbers
	 * and adds numbers together not surrounded by words
	 * 
	 * @param list 
	 * @return
	 */
	private List<WordToken> concatNumbers(List<WordToken> list) {
		List<WordToken> result = new ArrayList<>();
		
		WordTokenType lookAhead;
		for(int i = 0; i < list.size(); i++) {
			int newNumber = 0;
			int partialNumber = 0;
			boolean foundNumber = false;
			while(i < list.size() && list.get(i).getType() == WordTokenType.NUMBER &&
					list.get(i).getContent().matches("[a-zA-Z]+")) {
				String currentNum = list.get(i).getContent();
				foundNumber = true;
				if(this.wordToNumber.get(currentNum) >= 1000) {
					if(partialNumber == 0) partialNumber = 1;
					partialNumber *= this.wordToNumber.get(currentNum);
					newNumber += partialNumber;
					partialNumber = 0;
				}else {
					if(this.wordToNumber.get(currentNum) == 100) {
						if(partialNumber == 0) {
							partialNumber = 1;
						}
						partialNumber *= this.wordToNumber.get(currentNum);
					}else {
						partialNumber += this.wordToNumber.get(currentNum);
					}
				}
				i++;
			}
			newNumber += partialNumber;
			if(foundNumber) {
				WordToken t = new WordToken(String.valueOf(newNumber));
				t.setType(WordTokenType.NUMBER);
				result.add(t);
			}
			if(i < list.size()) {
				result.add(list.get(i));
			}
		}
		return result;
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
	
	/**
	 * read numbers of a language from a *.natlang file in 
	 *	/resources/langs/numbers/*.natlang
	 *
	 * @return List of all numbers in the language
	 */
	public final Map<String, Integer> readNumbers(){
		Map<String, Integer> result = new HashMap<>();
		String[] stringNmbRep;
		
		InputStream grammarFile = this.getClass().getResourceAsStream("englishNumbers.natlang");
		
		if(grammarFile == null) {
			this.logger.error("could not find numbers file");
			return new HashMap<>();
		}
		
		//wontfix
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
			this.logger.error("number file not found");
		}
		
		return result;
	}

	/**
	 * changes numbers from string to integer
	 * 
	 * @param numberInt the number as string
	 * @return number as integer
	 */
	private int parseNumber(String numberInt) {
		try {
			return Integer.valueOf(numberInt);
			}catch(NumberFormatException e) {
				this.logger.error("number in numbers file in wrong format " + numberInt);
			}
		return Integer.MAX_VALUE;
	}
}





