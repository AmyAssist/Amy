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

package de.unistuttgart.iaas.amyassist.amy.natlang.nl;

/**
 * Word Token class used in the Lexer and parser
 * 
 * @author Felix Burk
 */
public class EndToken {

	private final String content;

	private EndTokenType type;

	/**
	 * constructor for word token
	 * 
	 * @param content
	 *                    the content
	 */
	public EndToken(String content) {
		this.content = content;
	}

	/**
	 * getter for string content
	 * 
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * setter for type
	 * 
	 * @param type
	 *                 the type to set
	 */
	public void setType(EndTokenType type) {
		this.type = type;
	}

	/**
	 * returns the type
	 * 
	 * @return the type
	 */
	public EndTokenType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return this.content;
	}
}
