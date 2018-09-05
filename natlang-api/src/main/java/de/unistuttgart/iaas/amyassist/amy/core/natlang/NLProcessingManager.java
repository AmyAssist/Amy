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

/**
 * Manages all natural language interpreter of the plugins and can process the input of natural language
 *
 * @author Felix Burk, Leon Kiefer
 */
public interface NLProcessingManager {

	/**
	 * not supported right now!
	 * 
	 * @param grammarName name of grammar
	 * @param multiStart multi startup command
	 * @param singleStart single startup command
	 * @param multiStop multi stop command
	 * @param shutup command
	 * @return grammar file as string
	 */
	String getGrammarFileString(String grammarName, String multiStart, String singleStart, String multiStop,
			String shutup);

}
