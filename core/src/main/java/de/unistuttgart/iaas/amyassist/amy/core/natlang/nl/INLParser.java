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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.nl;

import java.util.Iterator;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * Interface definition of an NLParser
 * 
 * @author Felix Burk
 */
public interface INLParser {
	
	/**
	 * adds a grammar to it's internal list 
	 * of AGFNodes representing AGFGrammars
	 * 
	 * @param node to add
	 */
	public void addAGFNode(AGFNode node);
	
	/**
	 * removes an AGFNode from it's 
	 * pool of AGFNodes
	 * 
	 * @param node to remove
	 */
	public void removeAGFNode(AGFNode node);
	
	/**
	 * returns matching AGFNode
	 * 
	 * @param nl the natural languge represented by an 
	 * 		  Iterator containing all necessary WordTokens
	 * 
	 * @return the matching AGFNode
	 */
	public AGFNode parseNL(Iterator<WordToken> nl);

}
