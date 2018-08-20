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

import java.time.LocalDateTime;
import java.util.logging.Logger;

import javax.persistence.Entity;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData;

/**
 * Stores the data from one entity. Only one attribute is set at the same time
 * 
 * @author Lars Buttgereit
 */
public class EntityDataImpl implements EntityData {

	private String string;
	private LocalDateTime time;

	public EntityDataImpl(String string) {
		this.string = string;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData#getNumber()
	 */
	@Override
	public int getNumber() {
		try {
			return Integer.parseInt(this.string);
		} catch (NumberFormatException e) {

			return Integer.MIN_VALUE;
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData#getString()
	 */
	@Override
	public String getString() {
		return this.string;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData#getTime()
	 */
	@Override
	public LocalDateTime getTime() {
		return this.time;
	}

}
