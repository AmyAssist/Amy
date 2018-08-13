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

import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * Classes extending this interface should be able to handle 
 * different Intents.
 * 
 * This means it handles user queries
 * 
 * @author Felix Burk
 */
public interface IntentHandler {

	//void handle (Intent i);
	
	/**
	 * returns grammars for prompts of current intent
	 * @return all AGFNodes possible to match in prompts
	 */
	List<AGFNode> getGrammarsToHandle();
	
	/**
	 * is the Intent already handled?
	 * @return if handled 
	 */
	boolean isCurrentlyHandling();
	
}
