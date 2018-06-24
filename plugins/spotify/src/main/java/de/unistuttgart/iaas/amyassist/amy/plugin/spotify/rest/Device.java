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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Device Entity for JSON
 * 
 * @author Muhammed Kaya
 */
@XmlRootElement
public class Device {

	/**
	 * constructor for a Device
	 */
	public Device() {

	}

	/**
	 * constructor for a Device with set values
	 */
	public Device(String type, String name, String id) {
		this.type = type;
		this.name = name;
		this.id = id;
	}

	/**
	 * the type of the device
	 */
	private String type;

	/**
	 * the name of the device
	 */
	private String name;

	/**
	 * the ID of the device
	 */
	private String id;

	/**
	 * @return type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return id
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * @param id to set
	 */
	public void setID(String id) {
		this.id = id;
	}

}
