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

import java.lang.reflect.Method;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLAIMIntent;

/**
 * Manages all natural language interpreter of the plugins and can process the input of natural language
 * 
 * @author Felix Burk, Leon Kiefer
 */
public interface NLProcessingManager {

	/**
	 * registers the given class
	 * @param method to call
	 * @param intent to register
	*/
	void register(Method method, XMLAIMIntent intent);

	/**
	 * @param grammarName name of the grammar
	 * @return the grammar file string
	 */
	String getGrammarFileString(String grammarName);

	/**
	 * decides which intent matches 
	 * @param dialog containing intents
	 * @param naturalLanguageText text to match 
	 */
	void decideIntent(DialogImpl dialog, String naturalLanguageText);

	/**
	 * processes an intent 
	 * @param dialog containing current intent
	 * @param naturalLanguageText input text
	 */
	void processIntent(DialogImpl dialog, String naturalLanguageText);

}