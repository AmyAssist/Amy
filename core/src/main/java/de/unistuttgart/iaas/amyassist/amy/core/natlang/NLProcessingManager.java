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


/**
 * Manages all natural language interpreter of the plugins and can process the input of natural language
 * 
 * @author Leon Kiefer
 */
public interface NLProcessingManager {
	/**
	 * registers the given class
	 * 
	 * @param natuaralLanguageInterpreter
	 *            a class annotated with {@link de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand}
	 * @param aimContents content of all amy interaction model xml files
	 */
	void register(Class<?> natuaralLanguageInterpreter);

	/**
	 * Process the input by searching and calling a responsible interpreter for that natural language text
	 * 
	 * @param naturalLanguageText
	 *            a natural language text string that only contains alphanumeric character and white space character
	 * @return a answer as natural language text
	 * @throws IllegalArgumentException
	 *             if the string illegal character
	 */
	String process(String naturalLanguageText);

	/**
	 * @param grammarName name of the grammar
	 * @return the grammar file string
	 */
	String getGrammarFileString(String grammarName);
}
