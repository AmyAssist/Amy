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

package de.unistuttgart.iaas.amyassist.amy.natlang;

import java.lang.reflect.Method;
import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.natlang.aim.XMLAIMIntent;

/**
 * Handles natural language processing from registered methods and corresponding intents
 * 
 * @author Felix Burk
 */
public interface NLProcessingManager {

	/**
	 * registers an intent
	 * @param method with corresponding method
	 * @param intent corresponding xml aim intent object
	 */
	void register(Method method, XMLAIMIntent intent);

	/**
	 * processes an intent
	 * @param dialog from this Dialog object
	 * @param naturalLanguageText input from user
	 */
	void processIntent(Dialog dialog, String naturalLanguageText);

	/**
	 * decides which intent the user meant
	 * @param dialog corresponding dialog object
	 * @param naturalLanguageText from user
	 * @return matching dialog containing matching intent
	 */
	Dialog decideIntent(Dialog dialog, String naturalLanguageText);

	/**
	 * returns a list of possible grammars that will be understood by amy
	 * @return list of agf nodes
	 */
	List<AGFNode> getPossibleGrammars();

}
