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
import de.unistuttgart.iaas.amyassist.amy.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationRegistry;
import de.unistuttgart.iaas.amyassist.amy.registry.Taggable;

import java.util.List;

/**
 * get and set the data to the registry. At the moment with hardcoded data while registry is not avaiable
 * 
 * @author Lars Buttgereit
 * @author Benno Krauß
 */
@Service
public class RegistryConnection {

	@Reference
	private LocationRegistry locationRegistry;

	/**
	 * get the address from the registry with the given name. if now adress in registry found the input string is
	 * returned
	 * 
	 * @param name
	 *            name of the location
	 * @return one String with all address data, to use for e.g. google maps queries
	 */
	public String getAddress(String name) {

		Location address = null;

		List<Location> locations = this.locationRegistry.getEntitiesWithTag(name);
		if (!locations.isEmpty()) {
			address = locations.get(0);
		} else {
			// Try to find by name or tag ignoring case
			for (Location location : this.locationRegistry.getAll()) {
				if (name.equalsIgnoreCase(location.getName()) || name.equalsIgnoreCase(location.getTag())) {
					address = location;
					break;
				}
			}
		}

		if (address == null) {
			return name;
		}

		return address.getAddressString();
	}

	/**
	 * Get all available location tags
	 * @return all location tags
	 */
	String[] getAllLocationTags() {
		return this.locationRegistry.getAll().stream().filter(l -> l.getTag() != null && !l.getTag().isEmpty())
				.map(Taggable::getTag).distinct().toArray(String[]::new);
	}
}
