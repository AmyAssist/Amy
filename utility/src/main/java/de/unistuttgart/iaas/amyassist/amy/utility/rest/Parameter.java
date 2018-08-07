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

/**
 * POJO mapping of a parameter
 * 
 * @author Tim Neumann
 */
public class Parameter {

	private String name;
	private String valueType;
	private String paramType;
	private String description;
	private boolean required;

	/**
	 * gets the name
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * sets the name
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * gets the type of the parameter value
	 * 
	 * @return the type
	 */
	public String getValueType() {
		return this.valueType;
	}

	/**
	 * sets the type of the parameter value
	 * 
	 * @param type the new type of the value
	 */
	public void setValueType(String type) {
		this.valueType = type;
	}

	/**
	 * gets the type of the parameter
	 * 
	 * @return the type of the parameter
	 */
	public String getParamType() {
		return this.paramType;
	}

	/**
	 * sets the type of the parameter
	 * 
	 * @param paramType the new type
	 */
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	/**
	 * gets the description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * sets the description
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * returns if this parameter is required
	 * 
	 * @return true if the parameter is required, else false
	 */
	public boolean isRequired() {
		return this.required;
	}

	/**
	 * set if this parameter is required
	 * 
	 * @param required if the parameter is required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}
}
