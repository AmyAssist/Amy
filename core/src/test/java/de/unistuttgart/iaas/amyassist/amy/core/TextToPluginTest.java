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

package de.unistuttgart.iaas.amyassist.amy.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.anyOf;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for TextToPlugin mainly used to test the regex expressions feel
 * free to test your plugin grammar here
 * 
 * @author Felix Burk
 */
public class TextToPluginTest {
	
	/**
	 * helper variable to turn words representations of a number to 
	 * its corresponding integer value
	 */
	private HashMap<String, Integer> stringToNmb = new HashMap<>();
	
	/**
	 * all grammars to test are stored here
	 */
	Set<PluginGrammarInfo> infos;
	
	/**
	 * number word representations
	 */
	String[] oneToNine = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
	String[] teens = { "eleven", "twelfe", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
			"nineteen" };
	String[] tens = { "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eigthty", "ninety" };

	/**
	 * setup testing
	 */
	@BeforeEach
	void setup() {
		setupNumberHashMap();
		
		Set<String> grammars = new HashSet<>();
		grammars.add("count count count [count]");
		grammars.add("say (hello|test) [xx|yy] (or|term)");
		grammars.add("play # [#] [#]");
		grammars.add("get devices");
		grammars.add("#");
		grammars.add("# oh # [oh #]");

		ArrayList<String> keywords = new ArrayList<>();
		keywords.add("testGrammar");
		keywords.add("testiGrammar");
		keywords.add("number");

		Set<String> grammars2 = new HashSet<>();
		grammars2.add("amy says [great|bad] (bluuub|blub) things");
		grammars2.add("search # in (artist|track|playlist|album)");
		grammars2.add("this [grammar|(is | really | (bad | hehe))]");

		ArrayList<String> keywords2 = new ArrayList<>();
		keywords2.add("keyword2");
		keywords2.add("keyword3");

		PluginGrammarInfo info1 = new PluginGrammarInfo(keywords, grammars);
		PluginGrammarInfo info2 = new PluginGrammarInfo(keywords2, grammars2);

		this.infos = new HashSet<>();
		this.infos.add(info1);
		this.infos.add(info2);

	}

	/**
	 * tests the numbers and the mapper itself with random input
	 */
	@Test
	void testNumbers() {
		TextToGrammarMapper test = new TextToGrammarMapper(this.infos);
		
		//test all numbers 0-99
		for(int i=0; i<100; i++) {
			assertThat(test.resolveText("number " + String.valueOf(i)).getNumbers().get(0), equalTo(i));
		}
		
		List<String> numbers = new ArrayList<>();
		numbers.addAll(Arrays.asList(this.oneToNine));
		numbers.addAll(Arrays.asList(this.teens));
		numbers.addAll(Arrays.asList(this.tens));
		
		//test single textual numbers zero-ninety
		for(String s : numbers) {
			assertThat(test.resolveText("number " + s).getNumbers().get(0), equalTo(this.stringToNmb.get(s)));
		}
		
		//test numbers with random words thrown in at random positions
		//only 10 times to reduce testing time
		Random random = new SecureRandom();
		StringBuilder inputToTest;
		int wordPosition;
		int wordLength;
		StringBuilder  sb;
		
		int nmbRandomWords = 4;
		int nmbOfInputTests = 10;
		for(int j=0; j<nmbOfInputTests; j++) {
			
			inputToTest = new StringBuilder();
			
			//chances that a random word representation or a random number will be generated are slim enough
			//(probably)
			wordPosition = random.nextInt(nmbRandomWords);
			for(int wordCount = nmbRandomWords; wordCount >= 0; wordCount--) {
				if(wordPosition == wordCount) {
					if(random.nextInt() % 2 == 0) {
	        			inputToTest.append("number " + " thirty two ");
					}else {
						inputToTest.append("number " + " 99 ");
					}
        		}
				
        		wordLength = random.nextInt(11);
        		sb = new StringBuilder(wordLength);
        		for(int i = 0; i < wordLength; i++) {
        			//random ascii character
        			char c = (char) random.nextInt(128);
        			if(!Character.isDigit(c)) {
        				sb.append(c);
        			}
        		}
        		inputToTest.append(sb.toString());
			}
			assertThat(test.resolveText(inputToTest.toString()).getNumbers().get(0), anyOf(equalTo(32), equalTo(99)));
		}
	}
	
	/**
	 * tests return of whole command
	 */
	@Test
	void testWholeCommand() {
		TextToGrammarMapper test = new TextToGrammarMapper(this.infos);

		assertThat(test.resolveText("testGrammar play ten").getWholeCommand(), equalTo("testgrammar play 10"));
		assertThat(test.resolveText("testGrammar play 10").getWholeCommand(), equalTo("testgrammar play 10"));
		assertThat(test.resolveText("testGrammar play ten two").getWholeCommand(), equalTo("testgrammar play 12"));
		assertThat(test.resolveText("number ten two oh fifty three oh 99").getWholeCommand(), equalTo("number 12 oh 53 oh 99"));
	}
	
	/**
	 * tests the keyword matcher
	 */
	@Test
	void keywordMatcher() {
		TextToGrammarMapper test = new TextToGrammarMapper(this.infos);

		assertThat(test.resolveText("testGrammar play    ten").getMatchingKeyword(), equalTo("testGrammar"));
		assertThat(test.resolveText("ihdaip dapjo daüo üadüa  testGrammar play ten düoadü a kdüawda ").getMatchingKeyword(), equalTo("testGrammar"));
	}
	
	/**
	 * tests the grammar matcher
	 */
	@Test 
	void testGrammarMatcher() {
		TextToGrammarMapper test = new TextToGrammarMapper(this.infos);
		
		assertThat(test.resolveText("testGrammar play ten").getMatchingGrammar(), equalTo("play # [#] [#]"));
		assertThat(test.resolveText("dawdad awddaw da dawd    testGrammar play ten dwad ad awdwad ad").getMatchingGrammar(), equalTo("play # [#] [#]"));
		assertThat(test.resolveText("number ten two oh fifty three oh 99").getMatchingGrammar(), equalTo("# oh # [oh #]"));
		assertThat(test.resolveText("keyword2 amy says blub things").getMatchingGrammar(), equalTo("amy says [great|bad] (bluuub|blub) things"));
		assertThat(test.resolveText("jdwpojdpa keyword2 amy says blub things jdwopajd").getMatchingGrammar(),
				equalTo("amy says [great|bad] (bluuub|blub) things"));
		assertThat(test.resolveText("amy says great blub things").getMatchingGrammar(), equalTo(null));
		assertThat(test.resolveText("testGrammar get devices").getMatchingGrammar(), equalTo("get devices"));
		assertThat(test.resolveText("blah testiGrammar count count count count count blah").getMatchingGrammar(), 
				equalTo("count count count [count]"));
		assertThat(test.resolveText("keyword2 this bad").getMatchingGrammar(), equalTo("this [grammar|(is | really | (bad | hehe))]"));
		assertThat(test.resolveText("keyword3 this").getMatchingGrammar(), equalTo("this [grammar|(is | really | (bad | hehe))]"));


	}
		
	/**
	 * this method sets up a hashMap to map the word representation of a number
	 * to its corresponding integer value
	 * this way of mapping words to a number is kind of ugly - improvements are welcome!
	 */
	private void setupNumberHashMap() {
		int i = 0;
		for (String s : this.oneToNine) {
			this.stringToNmb.put(s, i);
			i++;
		}

		i = 11;
		for (String s : this.teens) {
			this.stringToNmb.put(s, i);
			i++;
		}

		i = 10;
		for (String s : this.tens) {
			this.stringToNmb.put(s, i);
			i += 10;
		}
	}

}
