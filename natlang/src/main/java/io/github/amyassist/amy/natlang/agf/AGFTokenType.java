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

package io.github.amyassist.amy.natlang.agf;

/**
 * all possible TokenTypes used in AGFLexer
 * 
 * @author Felix Burk
 */
public enum AGFTokenType {
	/**
	 * a word
	 */
	WORD,
	/**
	 * open bracket (
	 */
	OPENBR,
	/**
	 * close bracket )
	 */
	CLOSEBR,
	/**
	 * open square bracket [
	 */
	OPENSBR,
	/**
	 * close square bracket ]
	 */
	CLOSESBR,
	/**
	 * open curly bracket {
	 */
	OPENCBR,
	/**
	 * close curly bracket }
	 */
	CLOSECBR,
	/**
	 * or |
	 */
	OR,
	/**
	 * comma ,
	 */
	COMMA,
	/**
	 * $ sign for $(0,100,10)
	 */
	DOLLAR,
	/**
	 * + sign als wildcard for 0 to 5 words
	 */
	PLUS,
	/**
	 * * asterisc used as a wildcard at the end of a sentence
	 */
	ASTERISC
}
