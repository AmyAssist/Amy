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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * generates a valid *.gram file, keywords get replaced by pre defined rules JSGF specification:
 * https://www.w3.org/TR/jsgf/
 *
 * @author Felix Burk
 */
public class GrammarParser {
	private String name;
	private String wakeup;
	private String sleep;
	private String shutdown;

	private Map<String, String> mapKeywordRule = new HashMap<>();

	private List<String> addedRules = new ArrayList<>();

	/**
	 * initializes the parser
	 *
	 * possible expansions: more custom keywords, weights may be supported, <NULL> and <VOID> support, Unary Operators
	 * (kleene star, plus operator and tags)
	 *
	 * @param name
	 *            The name of the grammar
	 *
	 * @param wakeup
	 *            The wakeup call in this grammar.
	 * @param sleep
	 *            The sleep call in this grammar.
	 * @param shutdown
	 *            The shutdown call in this grammar.
	 */
	public GrammarParser(String name, String wakeup, String sleep, String shutdown) {
		this.wakeup = wakeup;
		this.sleep = sleep;
		this.shutdown = shutdown;
		this.name = name;

		// TODO find another way to store pre defined rules
		this.mapKeywordRule.put("#", "<digit>");

	}

	/**
	 * @return The grammar generated
	 */
	public String getGrammar() {
		// header
		String grammar = "#JSGF V1.0;\n" + "\n" + "/**\n" + " * JSGF Grammar \n" + " */\n" + "\n";

		grammar += "grammar " + this.name + ";\n";
		grammar += "public <wakeup> = ( " + this.wakeup + " );\n";
		grammar += "public <sleep> = ( " + this.sleep + " );\n";
		grammar += "public <shutdown> = ( " + this.shutdown + " );\n";

		grammar += "\n//pre defined rules \n";

		// pre defined rules
		// TODO add them to external file via import rule in JSGF
		grammar += "<digit> = (one | two | three | four | five | six | seven |"
				+ "nine | ten | eleven | twelve | thirteen | fourteen | fifteen | "
				+ "sixteen | seventeen | eighteen | nineteen | twenty | thirty | forty | "
				+ "fifty | sixty  | seventy | eighty | ninety | and )+; \n";

		grammar += "\n//custom rules \n";

		for (String s : this.addedRules) {
			grammar += s.toLowerCase();
		}

		return grammar;
	}

	/**
	 * Adds a rule to the grammar
	 *
	 * @param ruleName
	 *            The name of the rule
	 * @param keyword
	 *            The keyword
	 */
	public void addRule(String ruleName, String[] keywords, String grammar) {
		String rule = "public <" + ruleName + ">" + " = ";

		if (keywords.length > 1) {
			rule += "(";
			for (String keyword : keywords) {
				rule += this.parseKeyword(keyword) + "|";
			}
			rule = rule.substring(0, rule.length() - 1);
			rule += ") " + this.parseKeyword(grammar) + "; \n";
		} else {
			rule += this.parseKeyword(keywords[0]) + " " + this.parseKeyword(grammar) + "; \n";
		}

		this.addedRules.add(rule);
	}

	/**
	 * replace keywords with corresponding pre defined rule
	 *
	 * @param keyword
	 *            The keyword
	 * @return the corresponding rule
	 */
	private String parseKeyword(String keyword) {
		String parsedKeyword = "";

		for (String s : this.mapKeywordRule.keySet()) {
			parsedKeyword = keyword.replace(s, this.mapKeywordRule.get(s));
		}
		return parsedKeyword;
	}

}
