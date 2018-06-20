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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class maps a sentence to one of the given grammars in jsgf format
 * 
 * @author Felix Burk
 */
public class TextToGrammarMapper {
	
	protected final Logger logger = LoggerFactory.getLogger(TextToGrammarMapper.class);
	
	/**
	 * 
	 */
	Set<PluginGrammarInfo> infos;
		
	/**
	 * helper variable to turn words representations of a number to 
	 * its corresponding integer value
	 */
	private HashMap<String, Integer> stringToNmb = new HashMap<>();
	
	private PluginGrammarInfo matchingGrammar;


	/**
	 * 
	 * @param set set
	 */
	public TextToGrammarMapper(Set<PluginGrammarInfo> set) {
		this.infos = set;
		setupNumberHashMap();
	}
	
	/**
	 * returns generated Grammar Command
	 * @param inputText inputText
	 * @return grammarCommand grammarCommand
	 */
	public GrammarCommand resolveText(String inputText) {
		GrammarCommand grammarCommand = new GrammarCommand();
		
		Map<Integer, Integer[]> numbersToPositionInInput = extractNumbers(inputText);
		grammarCommand.setNumbers(new ArrayList<>(numbersToPositionInInput.keySet()));
		
		String inputParsedNumbers = inputText;
		for(Map.Entry<Integer, Integer[]> entry : numbersToPositionInInput.entrySet()) {
			String nmb = inputParsedNumbers.substring(entry.getValue()[0].intValue(), entry.getValue()[1].intValue());
			inputParsedNumbers = inputParsedNumbers.replace(nmb, " " + entry.getKey().toString() + " ");
		}
		inputParsedNumbers = inputParsedNumbers.trim();
		//replace double spacings accidentally created by replace in for loop
		//reason: the number matcher in extract numbers matches "thirty two " not "thirty two" 
		inputParsedNumbers = inputParsedNumbers.replaceAll("\\s+", " ");
		grammarCommand.setWholeCommand(inputParsedNumbers);
		
		String matchingKeyword = extractMatchingKeyword(inputText);
		grammarCommand.setMatchingKeyword(matchingKeyword);
		
		if(matchingKeyword != null) {
			String grammar = stringToGrammar(inputText, matchingKeyword);
			if(grammar != null) {
    			grammarCommand.setMatchingGrammar(stringToGrammar(inputText, matchingKeyword));
			}
		}
			
		
		return grammarCommand; 
	}
	
	
	/**
	 * @param grammar
	 * @param inputText
	 * @return
	 */
	private HashMap<String, Boolean> resolveOptionalGroups(String grammar, String inputText) {
		List<String> groups = getGroups(grammar, new ArrayList<>(), '[', ']');
		
	
		return null;
	}

	/**
	 * @param grammar grammar 
	 * @param inputText inputText
	 * @return HashMap<String,Boolean>
	 */
	private List<String> getGroups(String grammar, List<String> groups, char start, char end) {
		int openBr = 0;
		int startBrIndize = 0;
		char c;
		
		for(int i = 0; i<grammar.length(); i++) {
			c = grammar.charAt(i);
			if(c == start) {
				openBr++;
				if(startBrIndize == 0)
					startBrIndize = i;
			}else if(c == end) {
				openBr--;
				
				if(openBr == 0) {
					String group = grammar.substring(startBrIndize+1, i);
					groups.add(grammar.substring(startBrIndize, i+1));
					if(group.length() >= 1) {
						getGroups(group, groups, start, end);
					}
				}
			}
			
		}
		return groups;
	}

	/**
	 * extracts first matching keyword
	 * 
	 * @param inputText inputText
	 * @return matchingKeyword
	 */
	private String extractMatchingKeyword(String inputText) {
		
		String keywordResult = null;
		
		for(PluginGrammarInfo currentGrammar : this.infos) {
			for(String keyword : currentGrammar.keywords) {
				int index = inputText.indexOf(keyword);
				
				if(index != -1) {
					if(keywordResult == null) {
						keywordResult = keyword;
						
						this.matchingGrammar = currentGrammar;
					}else {
						this.logger.warn("multiple matching keywords found");
					}
    				
				}
			}
		}
		return keywordResult;
	}

	/**
	 * returns a matching grammar for the given input text
	 * 
	 * @param inputText inputText
	 * @return the matching grammar
	 */
	private String stringToGrammar(String inputText, String keyword) {
		String text = inputText.substring(inputText.indexOf(keyword)+keyword.length(), inputText.length());
		
		List<String> grammarResults = new ArrayList<>();
		
		for(String grammar : this.matchingGrammar.grammars) {
			String regex = regexFromGrammar(grammar);
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(text);
			
			if (m.find()) {
				grammarResults.add(grammar);
			}
		}
		return Collections.max(grammarResults, Comparator.comparing(String::length));
		
	}

	/**
	 * @param grammar grammar
	 * @return string
	 */
	private String regexFromGrammar(String grammar) {
		String regex = grammar;

		// find all occurences of optional words example: [test|test] or [test]
		// and replace them with: (test|test)?
		Matcher mmm = Pattern.compile("\\[(.*?)\\]").matcher(regex);
		while (mmm.find()) {
			regex = regex.replace(mmm.group(), "(" + mmm.group().substring(1, mmm.group().length() - 1) + ")?");
		}

		// find all words and replace them with \\bword\\b
		Matcher mm = Pattern.compile("[a-zA-Z0-9]+").matcher(regex);
		while (mm.find()) {
			String s = mm.group();
			regex = regex.replace("\\b" + s + "\\b", convertWord(s));
		}

		// replace convenience characters for pre defined rules with
		// corresponding regex
		// (this might be hard for other pre defined rules in the future, but
		// numbers are easy)
		// \\bnumber\\b is not needed here because of the previous for loop
		regex = regex.replaceAll("#",
				"(((zero|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelfe|thirteen"
						+ "|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|"
						+ "ten|twenty|thirty|forty|fifty|sixty|seventy|eigthty|ninety)+|([0-9]+))\\\\s{0,1})+");

		// at last replace whitespace with an arbitrary number of whitespaces
		// makes things like @Grammar(this has lots of space) possible
		return regex.replaceAll(" ", "\\\\s*");

	}
	
	/**
	 * convenience method
	 * 
	 * @param word word
	 * @return converted word
	 */
	private String convertWord(String word) {
		return "\\b" + word.toLowerCase() + "\\b";
	}


	/**
	 * extracts all numbers of a given sentence and saves them in the same 
	 * order in a List<Integer>
	 * @param text the sentence to extract numbers from
	 * @return the integer List
	 */
	private Map<Integer, Integer[]> extractNumbers(String text) {
		Map<Integer, Integer[]> integerList = new HashMap<>();
		
		//matches all textual number representations zero to ninety nine and all integer representations 0-99
		String extractNumbersRegex = "(((\\bzero\\b|\\bone\\b|\\btwo\\b|\\bthree\\b|"
				+ "\\bfour\\b|\\bfive\\b|\\bsix\\b|\\bseven\\b|\\beight\\b|\\bnine\\b|"
				+ "\\bten\\b|\\beleven\\b|\\btwelfe\\b|\\bthirteen\\b"
				+ "|\\bfourteen\\b|\\bfifteen\\b|sixteen\\b|\\bseventeen\\b|\\beighteen\\b|\\bnineteen\\b|"
				+ "\\bten\\b|\\btwenty\\b|\\bthirty\\b|\\bforty\\b|\\bfifty\\b|\\bsixty\\b|\\bseventy\\b|\\beigthty\\b|\\bninety\\b"
				+ ")\\s{0,1})+|((0|[1-9][0-9]{0,1})))";
		
		Matcher numberMatcher = Pattern.compile(extractNumbersRegex).matcher(text);
		
		String group;
		int tmpNmb = 0;
		while(numberMatcher.find()) {
			group = numberMatcher.group();

			group = group.trim();
			
			tmpNmb = 0;
			
			final int powerTen = 10;
			
			if(Character.isDigit(group.charAt(0))) {
				//number representation.. 
				group = group.replace(" ", "");
				
				for(int i=0; i<group.length(); i++) {
					tmpNmb += Math.pow(powerTen, group.length()-1f-i) * (group.charAt(i)-'0');
				}
			}else {
				//word representation.. 
				String[] nmbs = group.split(" ");

				for (String s : nmbs) {
					s = s.replace(" ", "");
					tmpNmb += this.stringToNmb.get(s).intValue();
				}
			}
			integerList.put(tmpNmb, new Integer[]{numberMatcher.start(), numberMatcher.end()});

		}
		
		return integerList;
		
	}
	/**
	 * this method sets up a hashMap to map the word representation of a number
	 * to its corresponding integer value
	 * this way of mapping words to a number is kind of ugly - improvements are welcome!
	 */
	private void setupNumberHashMap() {
		String[] oneToNine = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
		String[] teens = { "eleven", "twelfe", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
				"nineteen" };
		String[] tens = { "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eigthty", "ninety" };

		int i = 0;
		for (String s : oneToNine) {
			this.stringToNmb.put(s, i);
			i++;
		}

		i = 11;
		for (String s : teens) {
			this.stringToNmb.put(s, i);
			i++;
		}

		i = 10;
		for (String s : tens) {
			this.stringToNmb.put(s, i);
			i += 10;
		}
	}	


}
