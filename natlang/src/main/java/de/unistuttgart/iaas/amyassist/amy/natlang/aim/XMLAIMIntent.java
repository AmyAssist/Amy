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

package de.unistuttgart.iaas.amyassist.amy.natlang.aim;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * Intent object, used to parse aim xml data
 * 
 * @author Felix Burk
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class XMLAIMIntent {

	@XmlAttribute(name = "ref", required = true)
	private String reference = "";

	private String gram = "";

	@XmlElementWrapper(name = "EntityTemplates", required = false)
	@XmlElement(name = "EntityTemplate", required = false)
	private List<XMLEntityTemplate> templates = new ArrayList<>();

	@XmlElement(name = "Prompt", required = false)
	private List<XMLPrompt> prompts = new ArrayList<>();

	/**
	 * prints information of itself and its content
	 * 
	 * @return string to print
	 */
	public String printSelf() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n Intent ref=" + this.reference + " gram=" + this.gram);
		for (XMLEntityTemplate temp : this.templates) {
			builder.append(temp.printSelf());
		}
		for (XMLPrompt p : this.prompts) {
			builder.append(p.printSelf());
		}
		return builder.toString();
	}

	/**
	 * Get's {@link #reference reference}
	 * 
	 * @return reference
	 */
	public String getReference() {
		return this.reference;
	}

	/**
	 * Get's {@link #gram gram}
	 * 
	 * @return gram
	 */
	public String getGram() {
		return this.gram;
	}

	/**
	 * Get's {@link #templates templates}
	 * 
	 * @return templates
	 */
	public List<XMLEntityTemplate> getTemplates() {
		return this.templates;
	}

	/**
	 * Get's {@link #prompts prompts}
	 * 
	 * @return prompts
	 */
	public List<XMLPrompt> getPrompts() {
		return this.prompts;
	}

}
