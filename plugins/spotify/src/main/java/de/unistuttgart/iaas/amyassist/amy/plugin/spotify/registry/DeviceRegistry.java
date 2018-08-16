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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.registry;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.registry.AbstractRegistry;

import javax.annotation.Nonnull;

/**
 * Custom registry for spotify devices
 *
 * @author Lars Buttgereit
 */
@Service(DeviceRegistry.class)
public class DeviceRegistry extends AbstractRegistry<DeviceEntity> {

	@Override
	protected String getPersistenceUnitName() {
		return "SpotifyDeviceRegistry";
	}

	@Nonnull
	@Override
	protected Class<? extends DeviceEntity> getEntityClass() {
		return DeviceEntity.class;
	}

	/**
	 * find a device in the registry with the uri
	 * 
	 * @param uri of the device
	 * @return the device or null if the device not in the registry
	 */
	public DeviceEntity findDeviceWithUri(String uri) {
		for (DeviceEntity device : getAll()) {
			if (device.getID() != null && device.getID().equals(uri)) {
				return device;
			}
		}
		return null;
	}
}
