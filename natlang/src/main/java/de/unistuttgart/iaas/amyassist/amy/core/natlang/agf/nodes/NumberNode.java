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

/**
 * A node representing a number in some range and stepsize
 * 
 * @author Felix Burk
 */
public class NumberNode extends AGFNode {

	private int containedNumber;

	private int min;
	private int max;
	private int stepsize;

	/**
	 * A node containing any integer number example $(0,100,10) means any number between 0 and 100 with stepsize 10
	 * 
	 * @param min
	 *                     number
	 * @param max
	 *                     number
	 * @param stepsize
	 *                     of the node
	 * 
	 * @throws NumberFormatException
	 *                                   if Integer.parseInt(content) fails
	 */
	public NumberNode(int min, int max, int stepsize) {
		super("");
		this.min = min;
		this.max = max;
		this.stepsize = stepsize;
	}

	/**
	 * returns the node type
	 * 
	 * @return the type
	 */
	@Override
	public AGFNodeType getType() {
		return AGFNodeType.NUMBER;
	}

	/**
	 * special print self method, because this node my never have children
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode#printSelf(java.lang.String, int)
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

	/**
	 * Get's {@link #containedNumber containedNumber}
	 * 
	 * @return containedNumber
	 */
	public int getContainedNumber() {
		return this.containedNumber;
	}

	/**
	 * sets the internal saved number
	 * 
	 * @param number
	 *                   to set as String
	 * @throws NumberFormatException
	 *                                   if the string is not a number or not inside the specified range and stepsize
	 */
	public void setContainedNumber(String number) {
		int nmb = Integer.parseInt(number);

		if (!(this.min <= nmb && nmb <= this.max && nmb % this.stepsize == 0)) {
			throw new NumberFormatException("number " + number + " does not fit in range min " + this.min + " max "
					+ this.max + " stepsize " + this.stepsize);
		}
	}

}
