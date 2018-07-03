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

import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * This class generates JSGF grammars from AGF Syntax Trees
 * 
 * @author Felix Burk
 */
public class JSGFGenerator {
	
	/**
	 * a list of all rules
	 */
	List<String> rules;
	
	private String name;
	private String wakeup;
	private String sleep;
	private String shutdown;
	
	/**
	 * constructor, generates a JSGF file
	 * @param name the name of the grammar file
	 * @param wakeup the wakeup command
	 * @param sleep the sleep command
	 * @param shutdown the shutdown command
	 */
	public JSGFGenerator(String name, String wakeup, String sleep, String shutdown) {
		this.name = name;
		this.wakeup = wakeup;
		this.sleep = sleep;
		this.shutdown = shutdown;
		
		this.rules = new ArrayList<>();
	}
	
	/**
	 * generates a whole JSGF grammar file content
	 * including previously added rules via addRule(..)
	 * 
	 * just put the returning string in a new file
	 * 
	 * @return the JSGF grammar string, including headers
	 */
	public String generateGrammarFileString() {
		StringBuilder grammar = new StringBuilder();
		
		grammar.append("#JSGF V1.0;\n" + "\n" + "/**\n" + " * JSGF Grammar \n" + " */\n" + "\n");

		grammar.append("grammar " + this.name + ";\n");
		grammar.append("public <wakeup> = ( " + this.wakeup + " );\n");
		grammar.append("public <sleep> = ( " + this.sleep + " );\n");
		grammar.append("public <shutdown> = ( " + this.shutdown + " );\n");

		grammar.append("\n//pre defined rules \n");

		// pre defined rules
		grammar.append("<digit> = (one | two | three | four | five | six | seven |"
				+ "nine | ten | eleven | twelve | thirteen | fourteen | fifteen | "
				+ "sixteen | seventeen | eighteen | nineteen | twenty | thirty | forty | "
				+ "fifty | sixty  | seventy | eighty | ninety | and )+; \n");

		grammar.append("\n//custom rules \n");
		
		
		for(String rule : this.rules) {
			grammar.append(rule);
		}
		
		return grammar.toString();
	}
	
	/**
	 * generates a valid public JSGF Rule from an AGFNode
	 * the generated rule is added to an internal list of rules, if 
	 * generateGrammarFileString() is called added rules are included
	 * @param node root node
	 * @param ruleName the name of the rule
	 * @return the rule as String
	 */
	public String addRule(AGFNode node, String ruleName) {
		StringBuilder b = new StringBuilder();
		b.append("public <" + ruleName + "> = ");
		String rule = handleNode(b, node) + ";";
		
		this.rules.add(rule);
		return rule;
	}

	/**
	 * traverses the tree
	 * 
	 * @param b the string builder
	 * @param node the current root node
	 * @return the current string representation of the rule
	 */
	private String handleNode(StringBuilder b, AGFNode node) {
		if(node != null) {
			switch(node.getType()) {
			case WORD:
				b.append(" " + node.getContent());
				break;
			case RULE:
				b.append(" <digit>");
				break;
			case ORG:
				b.append(" (");
				for(int i = 0; i < node.getChilds().size(); i++) {
					handleNode(b, node.getChilds().get(i));
					if(i!=node.getChilds().size()-1) {
						b.append("|");
					}
				}
				b.append(") ");
				break;
			case OPG:
				b.append(" [");
				for(int i = 0; i < node.getChilds().size(); i++) {
					handleNode(b, node.getChilds().get(i));
					if(i!=node.getChilds().size()-1) {
						b.append("|");
					}
				}
				b.append("] ");
				break;
			case MORPH:
				for(AGFNode child : node.getChilds()) {
					handleNode(b, child);
				}
				break;
			default:
				for(AGFNode child : node.getChilds()) {
					handleNode(b, child);
				}
				break;
			}
		}
		return b.toString();
	}
	
	/**
	 * Adds a rule to the grammar
	 *
	 * @param ruleName
	 *            The name of the rule
	 * @param keyword
	 *            The keyword
	 */
	@Deprecated
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

		this.rules.add(rule);
	}

	/**
	 * replace keywords with corresponding pre defined rule
	 *
	 * @param keyword
	 *            The keyword
	 * @return the corresponding rule
	 */
	@Deprecated
	private String parseKeyword(String keyword) {
		return keyword.replace("#", "<digit>");
}

}
