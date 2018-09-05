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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.agf;

/**
 * Token used by the agf lexer
 * 
 * @author Felix Burk
 */
public class AGFToken {
	/**
	 * the type of token
	 */
	public final AGFTokenType type;
	/**
	 * content
	 */
	public final String content;

	/**
	 * constructor
	 * 
	 * @param type
	 *                    agf token type
	 * @param content
	 *                    the content
	 */
	public AGFToken(AGFTokenType type, String content) {
		this.type = type;
		this.content = content;
	}

	/**
	 * constructor
	 * 
	 * @param type
	 *                 agf token type
	 */
	public AGFToken(AGFTokenType type) {
		this.type = type;

		switch (type) {
		case OPENBR:
			this.content = "(";
			break;
		case CLOSEBR:
			this.content = ")";
			break;
		case OPENSBR:
			this.content = "[";
			break;
		case CLOSESBR:
			this.content = "]";
			break;
		case OPENCBR:
			this.content = "{";
			break;
		case CLOSECBR:
			this.content = "}";
			break;
		case OR:
			this.content = "|";
			break;
		case DOLLAR:
			this.content = "$";
			break;
		case COMMA:
			this.content = ",";
			break;
		case PLUS:
			this.content = "+";
			break;
		case ASTERISC:
			this.content = "*";
			break;
		default:
			throw new AGFLexerException("type could not be set because there is no matching content");
		}
	}

	/**
	 * convenience method prints type as string
	 * 
	 * @return the type as string
	 */
	public String getType() {
		return this.type.toString();
	}

}
