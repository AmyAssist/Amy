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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity;

/**
 * in this abstract class are all attributes from Data object that are the same
 * in all four different types. For example after a search
 * 
 * @author Lars Buttgereit
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Item extends Entity{
	private String uri;
	private String name;

	/**
	 * default constructor. no data is set
	 */
	public Item() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name of the item
	 * @param uri of the item
	 */
	public Item(String name, String uri) {
		this.uri = uri;
		this.name = name;
	}

	/**
	 * get the name from the item
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * set the name form the item
	 * 
	 * @param name from the item
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * get the uri from the item
	 * 
	 * @return uri
	 */
	public String getUri() {
		return this.uri;
	}

	/**
	 * set the uri form the item
	 * 
	 * @param uri from the item
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
}
