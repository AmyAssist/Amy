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

package io.github.amyassist.amy.natlang.userinteraction;

import io.github.amyassist.amy.natlang.agf.nodes.AGFNode;

/**
 * Prompt hold the grammar and output for a query
 * 
 * @author Lars Buttgereit
 */
public class Prompt {
	private final String outputText;
	private final AGFNode grammar;

	/**
	 * constructor for a prompt
	 * 
	 * @param grammar
	 *                       AGF Grammar for the prompt
	 * @param outputText
	 *                       output text for the prompt
	 */
	public Prompt(AGFNode grammar, String outputText) {
		this.grammar = grammar;
		this.outputText = outputText;
	}

	/**
	 * Get's {@link #outputText outputText}
	 * 
	 * @return outputText
	 */
	public String getOutputText() {
		return this.outputText;
	}

	/**
	 * Get's {@link #grammar grammar}
	 * 
	 * @return grammar
	 */
	public AGFNode getGrammar() {
		return this.grammar;
	}

}
