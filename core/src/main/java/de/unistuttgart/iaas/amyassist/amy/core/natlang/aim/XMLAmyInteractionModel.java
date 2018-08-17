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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.aim;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * TODO: Description
 * @author Felix Burk
 */
@XmlRootElement(name="AmyInteractionModel")
public class XMLAmyInteractionModel {

	@XmlElement(name = "Intent", required = false)
	private List<XMLAIMIntent> intents;

	/**
	 * Get's {@link #intents intents}
	 * @return  intents
	 */
	public List<XMLAIMIntent> getIntents() {
		return this.intents;
	}
	
	/**
	 * convenience method
	 * @return string representation of this object 
	 */
	public String printSelf() {
		StringBuilder b = new StringBuilder();
		for(XMLAIMIntent intent : this.intents) {
			b.append(intent.printSelf());
		}
		return b.toString();
	}
	
}
