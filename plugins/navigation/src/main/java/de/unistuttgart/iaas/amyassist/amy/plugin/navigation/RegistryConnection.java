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

package de.unistuttgart.iaas.amyassist.amy.plugin.navigation;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.LocationRegistry;

/**
 * get and set the data to the registry. At the moment with hardcoded data while registry is not avaiable
 * 
 * @author Lars Buttgereit
 */
@Service
public class RegistryConnection {

	@Reference
	private LocationRegistry locationRegistry;

	/**
	 * get the address from the registry with the given name
	 * 
	 * @param name
	 *            name of the location
	 * @return one String with all adress data, to use for e.g. google maps queries
	 */
	public String getAdress(String name) {
		switch (name.toLowerCase()) {
		case "home":
			return this.locationRegistry.getHome().getAddressString();
		case "work":
			return this.locationRegistry.getWork().getAddressString();
		default:
			return findOtherLocations(name);
		}

	}

	private String findOtherLocations(String loc) {
		for (Location location : this.locationRegistry.getAll()) {
			if (location.getName().equals(loc)) {
				return location.getAddressString();
			}
		}
		return null;
	}
}
