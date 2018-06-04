/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class maps a sentence to one of the given grammars in jsgf format
 * 
 * @author Felix Burk
 */
public class TextToPlugin {

	Set<PluginGrammarInfo> infos;

	public TextToPlugin(Set<PluginGrammarInfo> set) {
		this.infos = set;
		this.setupUglyHashMap();
	}

	/**
	 * returns first matching grammar
	 * 
	 * this method will ignore things before and after the grammar which
	 * means @Grammar(jhdawdap amy says wjapdjawp) will work this is great for
	 * error prone speech to text
	 * 
	 * !the longest matching grammar will be returned!
	 * 
	 * @param text
	 * @return String[] 0 contains matching keyword, 1 contains matching grammar
	 */
	public String[] pluginActionFromText(String text) {
		ArrayList<String> results = new ArrayList<>();
		String finalResult = null;

		for (PluginGrammarInfo currentGrammar : this.infos) {

			for (String keyword : currentGrammar.keywords) {

				int index = text.indexOf(keyword);
				String textTemp = text;

				// keyword found! try to match the grammar
				if (index != -1) {
					textTemp = text.substring(index + keyword.length(), text.length());

					Collection<String> grammars = currentGrammar.grammars;
					for (String s : grammars) {

						String regex = regexFromGrammar(s);
						Pattern p = Pattern.compile(regex);
						Matcher m = p.matcher(textTemp);

						if (m.find())
							results.add(s);

					}
					// find looks for matching substrings
					// ! the first matching grammar is returned !
					if (!results.isEmpty()) {
						finalResult = Collections.max(results, Comparator.comparing(s -> s.length()));
						return new String[] { keyword, finalResult };
					}

				}
			}
		}

		return null;

	}

	/**
	 * some regex fun! because java string handling is weird i have to use alot
	 * of \
	 * 
	 * this method is prone to errors, i tested it as much as possible but if
	 * there are any bugs tell me immediately -Felix B
	 * 
	 * @param grammar
	 * @return the generated regex for the specific grammar
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
				"((zero|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelfe|thirteen"
						+ "|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|"
						+ "ten|twenty|thirty|forty|fifty|sixty|seventy|eigthty|ninety)+|([0-9]+))");

		// at last replace whitespace with an arbitrary number of whitespaces
		// makes things like @Grammar(this has lots of space) possible
		return regex.replaceAll(" ", "\\\\s*");

	}

	/**
	 * convenience method
	 * 
	 * @param word
	 * @return
	 */
	private String convertWord(String word) {
		return "\\b" + word.toLowerCase() + "\\b";
	}

	/**
	 * get a number as int from any string only one number in the sentence is
	 * supported right now! and only numbers ranging from 0 to 99
	 * 
	 * @param text
	 * @return result
	 */
	List<Integer> stringToNumber(String text) {
		List<Integer> results = new ArrayList<>();

		String regex = "((\\bzero\\b|\\bone\\b|\\btwo\\b|\\bthree\\b|\\bfour\\b|\\bfive\\b|\\bsix\\b|\\bseven\\b|\\beight\\b|\\bnine\\b|"
				+ "\\bten\\b|\\beleven\\b|\\btwelfe\\b|\\bthirteen\\b"
				+ "|\\bfourteen\\b|\\bfifteen\\b|sixteen\\b|\\bseventeen\\b|\\beighteen\\b|\\bnineteen\\b|"
				+ "\\bten\\b|\\btwenty\\b|\\bthirty\\b|\\bforty\\b|\\bfifty\\b|\\bsixty\\b|\\bseventy\\b|\\beigthty\\b|\\bninety\\b)\\s{0,1})+";

		String gr;
		Matcher m = Pattern.compile(regex).matcher(text);
		int result;
		while (m.find()) {
			result = 0;
			gr = m.group();
			String[] nmbs = gr.split(" ");
			System.out.println(gr);

			for (String s : nmbs) {
				s = s.replace(" ", "");
				result += Integer.valueOf(stringToNmb.get(s));
			}
			results.add(result);

		}

		return results;
	}

	private HashMap<String, Integer> stringToNmb = new HashMap<>();

	/**
	 * i know this is ugly.. this had to be done quickly
	 */
	private void setupUglyHashMap() {
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
