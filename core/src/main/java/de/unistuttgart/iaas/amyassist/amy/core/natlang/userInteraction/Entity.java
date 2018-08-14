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

import java.util.List;

/**
 * TODO: Description
 * 
 * @author Lars Buttgereit
 */
public class Entity {
	private final String type;
	private final String entityId;
	private final List<String> values;
	private final IMatcher matcher;
	private EntityData entityData;

	public Entity(String type, String entityId, List<String> values, IMatcher matcher) {
		this.type = type;
		this.entityId = entityId;
		this.values = values;
		this.matcher = matcher;
	}

	public boolean match(String toMatch) {
		if(this.matcher.match(toMatch)) {
			this.entityData = this.matcher.convert(toMatch);
			return true;
		}
		return false;
	}

	/**
	 * Get's {@link #matcher matcher}
	 * 
	 * @return matcher
	 */
	public IMatcher getMatcher() {
		return this.matcher;
	}
	
	/**
	 * Get's {@link #type type}
	 * 
	 * @return type
	 */
	public String getType() {
		return this.type;
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
	 * Get's {@link #values values}
	 * 
	 * @return values
	 */
	public List<String> getValues() {
		return this.values;
	}

	/**
	 * Get's {@link #entityData entityData}
	 * @return  entityData
	 */
	public EntityData getEntityData() {
		return this.entityData;
	}

	
	
}
