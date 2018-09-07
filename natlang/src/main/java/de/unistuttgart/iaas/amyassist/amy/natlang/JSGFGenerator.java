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

package de.unistuttgart.iaas.amyassist.amy.natlang;

import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.natlang.agf.nodes.AGFNode;

/**
 * This class generates JSGF grammars from AGF Syntax Trees
 *  
 * @author Felix Burk, Leon Kiefer
 */
public class JSGFGenerator {
	/**
	 * a list of all rules
	 */
	List<String> rules;

	private String name;
	private String multiCallStart;
	private String singleCallStart;
	private String multiCallStop;
	private String voiceOutputStopCommand;

	/**
	 * constructor, generates a JSGF file
	 * 
	 * @param name
	 *            the name of the grammar file
	 * @param multiCallStart
	 *            the wakeup command
	 * @param singleCallStart
	 *            wakeup command for a single command
	 * @param multiCallStop
	 *            the sleep command
	 * @param voiceOutputStopCommand
	 *            the shut up command
	 */
	public JSGFGenerator(String name, String multiCallStart, String singleCallStart, String multiCallStop,
			String voiceOutputStopCommand) {
		this.name = name;
		this.multiCallStart = multiCallStart;
		this.singleCallStart = singleCallStart;
		this.multiCallStop = multiCallStop;
		this.voiceOutputStopCommand = voiceOutputStopCommand;

		this.rules = new ArrayList<>();
	}

	/**
	 * generates a whole JSGF grammar file content including previously added rules via addRule(..)
	 * 
	 * just put the returning string in a new file
	 * 
	 * @return the JSGF grammar string, including headers
	 */
	public String generateGrammarFileString() {
		StringBuilder grammar = new StringBuilder();

		grammar.append("#JSGF V1.0;\n" + "\n" + "/**\n" + " * JSGF Grammar \n" + " */\n" + "\n");

		grammar.append("grammar " + this.name + ";\n");
		publicRule(grammar, "wakeup", this.multiCallStart);
		publicRule(grammar, "singlewakeup", this.singleCallStart);
		publicRule(grammar, "sleep", this.multiCallStop);
		publicRule(grammar, "shutdown", this.voiceOutputStopCommand);

		grammar.append("\n//pre defined rules \n");

		// pre defined rules
		grammar.append("<number> = (one | two | three | four | five | six | seven |"
				+ "nine | ten | eleven | twelve | thirteen | fourteen | fifteen | "
				+ "sixteen | seventeen | eighteen | nineteen | twenty | thirty | forty | "
				+ "fifty | sixty  | seventy | eighty | ninety )[one | two | three | four | five | six | seven | "
				+ "nine | ten | eleven | twelve | thirteen | fourteen | fifteen | "
				+ "sixteen | seventeen | eighteen | nineteen | twenty | thirty | forty | "
				+ "fifty | sixty  | seventy | eighty | ninety ]; \n");

		grammar.append("\n//custom rules \n");

		for (String rule : this.rules) {
			grammar.append(rule);
		}

		return grammar.toString();
	}

	private void publicRule(StringBuilder builder, String ruleName, String rule) {
		builder.append("public <").append(ruleName).append("> = ( ").append(rule).append(" );\n");
	}

	/**
	 * generates a valid public JSGF Rule from an AGFNode the generated rule is added to an internal list of rules, if
	 * generateGrammarFileString() is called added rules are included
	 * 
	 * @param node
	 *            root node
	 * @param ruleName
	 *            the name of the rule
	 * @return the rule as String
	 */
	public String addRule(AGFNode node, String ruleName) {
		StringBuilder b = new StringBuilder();
		StringBuilder rule = new StringBuilder();
		publicRule(rule, ruleName, handleNode(b, node));

		this.rules.add(rule.toString());
		return rule.toString();
	}

	/**
	 * traverses the tree
	 * 
	 * ------THIS IS BROKEN------
	 * this method does not support entities or any wildcards at the moment
	 * don't use it if the grammars are using any of these features.
	 * Wildcards are not supported by the jsgf standard, 
	 * but entities will be added in the near future
	 * 
	 * @param b
	 *            the string builder
	 * @param node
	 *            the current root node
	 * @return the current string representation of the rule
	 */
	private String handleNode(StringBuilder b, AGFNode node) {
		if (node != null) {
			switch (node.getType()) {
			case WORD:
				b.append(" " + node.getContent());
				break;
			case ENTITY:
				// nothing right now
				break;
			case ORG:
				b.append(" (");
				handleBraceContent(node, b);
				b.append(") ");
				break;
			case OPG:
				b.append(" [");
				handleBraceContent(node, b);
				b.append("] ");
				break;
			case MORPH:
				for (AGFNode child : node.getChilds()) {
					handleNode(b, child);
				}
				break;
			default:
				for (AGFNode child : node.getChilds()) {
					handleNode(b, child);
				}
				break;
			}
		}
		return b.toString();
	}

	/**
	 * convenience method to handle brace content
	 * 
	 * @param node
	 *            the AGFNode
	 * @param b
	 *            the StringBuilder
	 */
	private void handleBraceContent(AGFNode node, StringBuilder b) {
		for (int i = 0; i < node.getChilds().size(); i++) {
			handleNode(b, node.getChilds().get(i));
			if (i != node.getChilds().size() - 1) {
				b.append("|");
			}
		}
	}
}
