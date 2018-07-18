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

package de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest;

import java.time.ZonedDateTime;

import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity;

/**
 * Entity for navigation routes
 * 
 * @author Christian Bräuner
 */
@XmlRootElement
public class Route extends Entity {
	
	private String origin;
	private String destination;
	private String travelmode;
	private ZonedDateTime time;
	
	/**
	 * creates a new POJO
	 */
	public Route() {
		//needed for JSON
	}

	/**
	 * get the origin
	 * 
	 * @return the origin
	 */
	public String getOrigin() {
		return this.origin;
	}

	/**
	 * set the origin
	 * 
	 * @param origin the new origin
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * get the destination
	 * 
	 * @return the destination
	 */
	public String getDestination() {
		return this.destination;
	}

	/**
	 * set the destination
	 * 
	 * @param destination the new destination
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * get the travelmode
	 * 
	 * @return the travelmode
	 */
	public String getTravelmode() {
		return this.travelmode;
	}

	/**
	 * set the travelmode
	 * 
	 * @param travelmode the new travelmode
	 */
	public void setTravelmode(String travelmode) {
		this.travelmode = travelmode;
	}

	/**
	 * get the time
	 * 
	 * @return the time
	 */
	public ZonedDateTime getTime() {
		return this.time;
	}

	/**
	 * set the time
	 * 
	 * @param time the new time
	 */
	public void setTime(ZonedDateTime time) {
		this.time = time;
	}


}
