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

package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import javax.persistence.*;

import de.unistuttgart.iaas.amyassist.amy.registry.Location;

/**
 * A Persistence Entity for the current location used for weather speech
 * 
 * @author Tim Neumann
 */
@Entity
@PersistenceUnit(unitName = WeatherSpeechCommand.PERSISTENCE_NAME)
public class CurrentLocationPersistence {
	/** The primary key of the data */
	@Id
	@GeneratedValue
	@Column(updatable = false, nullable = false)
	private int primrayId;
	/** The current location */
	private Location currentLocation;

	/**
	 * Create a new current location persistence
	 * 
	 * @param pCurrentLocation
	 *            Set's {@link #currentLocation}
	 * @param pPrimrayId
	 *            Set's {@link #primrayId}
	 */
	public CurrentLocationPersistence(Location pCurrentLocation, int pPrimrayId) {
		this.currentLocation = pCurrentLocation;
		this.primrayId = pPrimrayId;
	}

	/**
	 * Get's {@link #currentLocation currentLocation}
	 * 
	 * @return currentLocation
	 */
	public Location getCurrentLocation() {
		return this.currentLocation;
	}

	/**
	 * Get's {@link #primrayId primrayId}
	 * 
	 * @return primrayId
	 */
	public int getPrimrayId() {
		return this.primrayId;
	}

}
