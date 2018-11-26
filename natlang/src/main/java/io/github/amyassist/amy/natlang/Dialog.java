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

package io.github.amyassist.amy.natlang;

import java.util.function.Consumer;

import io.github.amyassist.amy.core.natlang.Response;
import io.github.amyassist.amy.natlang.userinteraction.Prompt;
import io.github.amyassist.amy.natlang.userinteraction.UserIntent;

/**
 * internal implementation of dialog interface
 * 
 * @author Felix Burk
 */
public class Dialog {

	private Consumer<Response> consumer;

	/**
	 * constructor
	 * 
	 * @param cons
	 *            consumer for callback
	 */
	public Dialog(Consumer<Response> cons) {
		this.consumer = cons;
	}

	/**
	 * current intent - if null no intent is started
	 */
	private UserIntent intent = null;

	/**
	 * next prompt to process
	 */
	private Prompt nextPrompt = null;

	/**
	 * outputs the string to the user in some way
	 * 
	 * @param answerOutput
	 *            the string to output
	 */
	public void output(Response answerOutput) {
		this.consumer.accept(answerOutput);
	}

	/**
	 * Get's {@link #intent intent}
	 * 
	 * @return intent
	 */
	public UserIntent getIntent() {
		return this.intent;
	}

	/**
	 * Set's {@link #intent intent}
	 * 
	 * @param intent
	 *            intent
	 */
	public void setIntent(UserIntent intent) {
		this.intent = intent;
	}

	/**
	 * Get's {@link #nextPrompt nextPrompt}
	 * 
	 * @return nextPrompt
	 */
	public Prompt getNextPrompt() {
		return this.nextPrompt;
	}

	/**
	 * Set's {@link #nextPrompt nextPrompt}
	 * 
	 * @param nextPrompt
	 *            nextPrompt
	 */
	public void setNextPrompt(Prompt nextPrompt) {
		this.nextPrompt = nextPrompt;
	}

}
