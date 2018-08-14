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
 * MAtch String values from the XML and 'conver' these to a string
 * 
 * @author Lars Buttgereit
 */
public class StringMatcher implements IMatcher {

	private List<String> values;

	public StringMatcher(List<String> values) {
		this.values = values;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.userInteraction.IMatcher#match(java.lang.String)
	 */
	@Override
	public boolean match(String toMatch) {
		for (String value : this.values) {
			if (value.equals(toMatch)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.userInteraction.IMatcher#convert(de.unistuttgart.iaas.amyassist.amy.core.natlang.userInteraction.Entity)
	 */
	@Override
	public EntityData convert(String toConvert) {
		if (match(toConvert)) {
			return new EntityData(toConvert);
		}
		return null;
	}

}
