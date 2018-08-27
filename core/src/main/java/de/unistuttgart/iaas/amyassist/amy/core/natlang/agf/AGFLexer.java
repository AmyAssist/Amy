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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * agf lexer class
 * 
 * this class tokenizes the content of @Grammar annotations this means it categorizes the content in different kinds of
 * AGF tokens
 * 
 * @author Felix Burk
 */
public class AGFLexer implements Iterator<AGFToken> {

	private final String mToLex;
	private int mIndex = 0;

	/**
	 * constructor
	 * 
	 * @param toLex
	 *                  the string to lex
	 */
	public AGFLexer(String toLex) {
		this.mIndex = 0;
		this.mToLex = toLex.toLowerCase().trim();
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.mIndex < this.mToLex.length();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public AGFToken next() {
		if (hasNext()) {
			StringBuilder currentWord = new StringBuilder();

			char c = this.mToLex.charAt(this.mIndex++);

			switch (c) {
			case '(':
				return new AGFToken(AGFTokenType.OPENBR);
			case ')':
				return new AGFToken(AGFTokenType.CLOSEBR);
			case '|':
				return new AGFToken(AGFTokenType.OR);
			case '[':
				return new AGFToken(AGFTokenType.OPENSBR);
			case ']':
				return new AGFToken(AGFTokenType.CLOSESBR);
			case '{':
				return new AGFToken(AGFTokenType.OPENCBR);
			case '}':
				return new AGFToken(AGFTokenType.CLOSECBR);
			case ',':
				return new AGFToken(AGFTokenType.COMMA);
			case '$':
				return new AGFToken(AGFTokenType.DOLLAR);
			case '+':
				return new AGFToken(AGFTokenType.PLUS);
			case '*':
				return new AGFToken(AGFTokenType.ASTERISC);
			// only allow normal SPACE codepoint 32, U+0020
			case 32:
				return this.next();
			default:
				if (Character.isLetterOrDigit(c)) {
					currentWord.append(c);
					while (hasNext() && Character.isLetterOrDigit(this.mToLex.charAt(this.mIndex))) {
						currentWord.append(this.mToLex.charAt(this.mIndex));
						this.mIndex++;
					}
				}
				if (!currentWord.toString().isEmpty()) {
					return new AGFToken(AGFTokenType.WORD, currentWord.toString());
				}
				throw new AGFLexerException("wrong character " + c);

			}
		}
		throw new NoSuchElementException();

	}

}
