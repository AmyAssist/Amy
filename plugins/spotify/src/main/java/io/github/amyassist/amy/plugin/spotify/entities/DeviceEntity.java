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

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.iaas.amyassist.amy.registry.RegistryEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity;

/**
 * Device Entity for JSON and Registry
 * 
 * @author Muhammed Kaya, Lars Buttgereit
 */
@XmlRootElement
@javax.persistence.Entity
@PersistenceUnit(unitName = "SpotifyDeviceRegistry")
public class DeviceEntity extends Entity implements RegistryEntity {

	@Id
	@GeneratedValue
	private int persistentId;

	/**
	 * constructor for a Device
	 */
	public DeviceEntity() {

	}

	/**
	 * constructor for a Device with set values
	 */
	public DeviceEntity(String type, String name, String uri) {
		this.type = type;
		this.name = name;
		this.id = uri;
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
	 * @param type
	 *            to set
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
	 * @param name
	 *            to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return uri
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * @param uri
	 *            to set
	 */
	public void setID(String uri) {
		this.id = uri;
	}

	/**
	 * Get's {@link #persistentId id}
	 * 
	 * @return id
	 */
	@Override
	public int getPersistentId() {
		return this.persistentId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DeviceEntity that = (DeviceEntity) o;
		return this.persistentId == that.persistentId && this.name.equals(that.name) && this.type.equals(that.type)
				&& this.id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.persistentId, this.name, this.type, this.id);
	}

}
