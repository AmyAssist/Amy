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
	public Device(String type, String name, String ID) {
		this.type = type;
		this.name = name;
		this.ID = ID;
	}
	
	/**
	 * the type of the device
	 */
	public String type;
	
	/**
	 * the name of the device
	 */
	public String name;
	
	/**
	 * the ID of the device
	 */
	public String ID;
	
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + " " + this.ID;
	}

}
