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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.userInteraction;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData;

/**
 * TODO: Description
 * 
 * @author Lars Buttgereit
 */
public class Entity {
	private final String entityId;
	private EntityData entityData;
	private AGFNode grammar;
	private Prompt prompt;

	public Entity(String entityId, AGFNode grammar) {
		this.entityId = entityId;
		this.grammar = grammar;
	}
	
	public Entity(String entityId, AGFNode grammar, Prompt prompt) {
		this.entityId = entityId;
		this.grammar = grammar;
		this.prompt = prompt;
	}

	public void insertEntityData(EntityData entData) {
		this.entityData = entData;
	}

	/**
	 * Get's {@link #entityId entityId}
	 * 
	 * @return entityId
	 */
	public String getEntityId() {
		return this.entityId;
	}

	/**
	 * Get's {@link #entityData entityData}
	 * @return  entityData
	 */
	public EntityData getEntityData() {
		return this.entityData;
	}

	/**
	 * @return
	 */
	public AGFNode getGrammar() {
		return this.grammar;
	}

	/**
	 * Get's {@link #prompt prompt}
	 * @return  prompt
	 */
	public Prompt getPrompt() {
		return this.prompt;
	}

	/**
	 * Set's {@link #prompt prompt}
	 * @param prompt  prompt
	 */
	public void setPrompt(Prompt prompt) {
		this.prompt = prompt;
	}
	
	
}
