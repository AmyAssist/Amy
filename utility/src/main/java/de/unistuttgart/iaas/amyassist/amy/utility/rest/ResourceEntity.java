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

package de.unistuttgart.iaas.amyassist.amy.utility.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO for Resource description
 * 
 * @author Christian Bräuner
 */
@XmlRootElement
public class ResourceEntity extends Entity {
	
	private String name;
	private String description;



	/**
	 * base construktor for the ResourceEntity
	 */
	public ResourceEntity() {
		//needed for JSON
	}

	/**
	 * gets the name of the resource
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * sets the name of the resource
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * gets the description
	 * @return  the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * sets the description
	 * @param description  the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	

}
