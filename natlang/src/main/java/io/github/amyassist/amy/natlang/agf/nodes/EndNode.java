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

package io.github.amyassist.amy.natlang.agf.nodes;

/**
 * Helper class acting as a parent for all 
 * nodes that are end nodes - e.g. word nodes or number nodes
 * @author Felix Burk
 */
public class EndNode extends AGFNode {

	/**
	 * @param content
	 */
	public EndNode(String content) {
		super(content);
	}
	
	/**
	 * @see io.github.amyassist.amy.natlang.agf.nodes.AGFNode#getType()
	 */
	@Override
	public AGFNodeType getType() {
		//this is only a helper class and does not have an own agf node type
		return null;
	}
	
	/**
	 * special print self method, because this node my never have children
	 * 
	 * @see io.github.amyassist.amy.natlang.agf.nodes.AGFNode#printSelf(java.lang.String, int)
	 */
	@Override
	public String printSelf(String name, int indent) {
		StringBuilder b = new StringBuilder();
		b.append("+" + name);
		b.append("\n");

		for (int i = 0; i < indent; i++) {
			b.append("|");
			b.append("   ");
		}
		b.append("|---" + "<content> " + this.getContent());
		b.append("\n");
		return b.toString();
	}

}
