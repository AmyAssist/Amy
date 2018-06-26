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

package de.unistuttgart.iaas.amyassist.amy.httpserver.rest.home;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A plugin, that contains some basic information about itself
 * 
 * @author Christian Bräuner
 */
@XmlRootElement
public class SimplePluginEntity {

	private String name;
	private String link;
	private String description;
	
	/**
	 * creates a new SimplePluginEntity
	 */
	public SimplePluginEntity() {
		// needed for JSON
	}
	
	/**
	 * Get's {@link #name name}
	 * @return  name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * Set's {@link #name name}
	 * @param name  name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Get's {@link #link link}
	 * @return  link
	 */
	public String getLink() {
		return this.link;
	}
	/**
	 * Set's {@link #link link}
	 * @param link  link
	 */
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * Get's {@link #description description}
	 * @return  description
	 */
	public String getDescription() {
		return this.description;
	}
	/**
	 * Set's {@link #description description}
	 * @param description  description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
