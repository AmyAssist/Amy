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

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * This class generates JSGF grammars from AGF Syntax Trees
 * 
 * @author Felix Burk
 */
public class JSGFRuleGenerator {
	
	/**
	 * generates a valid public JSGF Rule from an AGFNode
	 * @param node root node
	 * @param ruleName the name of the rule
	 * @return the rule as String
	 */
	public String generateRule(AGFNode node, String ruleName) {
		StringBuilder b = new StringBuilder();
		b.append("public <" + ruleName + "> = ");
		return handleNodePreorder(b, node) + ";";
	}

	/**
	 * traverses the three preorder style 
	 * 
	 * @param b the string builder
	 * @param node the current root node
	 * @return the current string representation of the rule
	 */
	private String handleNodePreorder(StringBuilder b, AGFNode node) {
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
					handleNodePreorder(b, node.getChilds().get(i));
					if(i!=node.getChilds().size()-1) {
						b.append("|");
					}
				}
				b.append(") ");
				break;
			case OPG:
				b.append(" [");
				for(int i = 0; i < node.getChilds().size(); i++) {
					handleNodePreorder(b, node.getChilds().get(i));
					if(i!=node.getChilds().size()-1) {
						b.append("|");
					}
				}
				b.append("] ");
				break;
			case MORPH:
				for(AGFNode child : node.getChilds()) {
					handleNodePreorder(b, child);
				}
				break;
			default:
				for(AGFNode child : node.getChilds()) {
					handleNodePreorder(b, child);
				}
				break;
			}
		}
		return b.toString();
	}

}