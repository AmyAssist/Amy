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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Contains information of EntityTemplates used in aim xml files
 * 
 * @author Felix Burk
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLEntityTemplate {

	@XmlAttribute(name = "id")
	private String entityId;

	@XmlElement(name = "gram", required = false)
	private String grammar;

	/**
	 * @return string representation of this object
	 */
	public String printSelf() {
		StringBuilder builder = new StringBuilder();
		builder = builder.append("\n Entity id=" + this.entityId);
		if (this.grammar != null) {
			builder.append(" grammar=" + this.grammar);
		}
		return builder.toString();
	}

	/**
	 * Get's {@link #entityId entityId}
	 * 
	 * @return entityId
	 */
	public String getEntityId() {
		return this.entityId.toLowerCase().trim();
	}

	/**
	 * Get's {@link #grammar grammar}
	 * 
	 * @return grammar
	 */
	public String getGrammar() {
		return this.grammar.toLowerCase().trim();
	}
}
