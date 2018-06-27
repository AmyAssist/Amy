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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes;

import java.util.ArrayList;
import java.util.List;

/**
 * normal AGF node
 * 
 * @author Felix Burk
 */
public class AGFNode implements IAGFNode {

	/**
	 * all child nodes
	 */
	List<IAGFNode> childs;

	/**
	 * content of node
	 */
	public final String content;

	/**
	 * constructor
	 * 
	 * @param content
	 *            of node
	 */
	public AGFNode(String content) {
		this.childs = new ArrayList<>();
		this.content = content;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.IAGFNode#addChild(de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.IAGFNode)
	 */
	@Override
	public void addChild(IAGFNode node) {
		this.childs.add(node);
	}

	/**
	 * @param name
	 *            of class
	 * @return self string
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.IAGFNode#printSelf()
	 */
	@Override
	public String printSelf(String name, int indent) {
		StringBuilder b = new StringBuilder();
		b.append("+" + name);
		if (!this.childs.isEmpty()) {
			b.append("\n");
			for (IAGFNode n : this.childs) {
				for (int i = 0; i < indent; i++) {
					b.append("|");
					b.append("   ");
				}
				String[] s = n.getClass().getName().split("\\.");
				b.append("|---" + n.printSelf(s[s.length - 1], indent + 1));
			}
			b.append("\n");
		}

		return b.toString();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.IAGFNode#printSelf()
	 */
	@Override
	public String printSelf() {
		return printSelf("AGFNode", 0);
	}

}
