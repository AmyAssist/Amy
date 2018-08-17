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

/**
 * Stores the data from one entity. Only one attribute is set at the same time
 * @author Lars Buttgereit
 */
public class EntityData {

	private int number = Integer.MIN_VALUE;
	private String string;
	private LocalDateTime time;
	
	public EntityData(int number) {
		this.number = number;
	}
	
	public EntityData(String string) {
		this.string = string;
	}
	
	public EntityData(LocalDateTime time) {
		this.time = time;
	}

	/**
	 * Get's {@link #number number}
	 * @return  number
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * Get's {@link #string string}
	 * @return  string
	 */
	public String getString() {
		return this.string;
	}

	/**
	 * Get's {@link #time time}
	 * @return  time
	 */
	public LocalDateTime getTime() {
		return this.time;
	}
	
	
}