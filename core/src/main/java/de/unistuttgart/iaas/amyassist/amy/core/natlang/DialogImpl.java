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

import java.util.function.Consumer;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.userInteraction.UserIntent;

/**
 * TODO: Description
 * @author Felix Burk
 */
class DialogImpl implements Dialog {
	
	private Consumer<String> consumer;
	
	/**
	 * @param cons
	 */
	DialogImpl(Consumer<String> cons){
		this.consumer = cons;
	}

	/**
	 * current intent - if null no intent is started
	 */
	private UserIntent intent = null;
	
	/**
	 * 
	 * @param answerOutput
	 */
	public void output(String answerOutput) {
		this.consumer.accept(answerOutput);
	}

	/**
	 * Get's {@link #intent intent}
	 * @return  intent
	 */
	public UserIntent getIntent() {
		return this.intent;
	}

	/**
	 * Set's {@link #intent intent}
	 * @param intent  intent
	 */
	public void setIntent(UserIntent intent) {
		this.intent = intent;
	}
	
	
}
