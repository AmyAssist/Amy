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

package io.github.amyassist.amy.utility.rest;

/**
 * POJO-mapping of JSON method object
 * 
 * @author Christian Bräuner
 */
public class Method {

	private String link;
	private String description;
	private String type;
	private Parameter[] parameters;

	/**
	 * gets the link
	 * 
	 * @return the link
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * sets the link
	 * 
	 * @param link the new link
	 */
	public void setLink(String link) {
		this.link = link;
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
	 * gets the type of the method
	 * 
	 * @return the type of the method
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * sets the type of the method
	 * 
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * gets the parameters
	 * 
	 * @return the parameters
	 */
	public Parameter[] getParameters() {
		return this.parameters;
	}

	/**
	 * sets the parameters
	 * 
	 * @param parameters the new parameters
	 */
	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}
}
