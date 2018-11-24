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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.wrapper.spotify.model_objects.miscellaneous.Device;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.SpotifyAPICalls;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.registry.DeviceRegistry;

/**
 * In this class the devices from spotify are managed
 * 
 * @author Lars Buttgereit
 */
@Service(DeviceLogic.class)
public class DeviceLogic {

	@Reference
	private SpotifyAPICalls spotifyAPICalls;
	@Reference
	private Logger logger;
	@Reference
	private DeviceRegistry deviceRegistry;

	/**
	 * get all devices that logged in at the moment with possible changed name from the registry
	 * 
	 * @return empty ArrayList if no device available else Maps with the name, id and type of the device
	 */
	public List<DeviceEntity> getDevices() {
		List<DeviceEntity> devicesList = new ArrayList<>();
		Device[] devices = this.spotifyAPICalls.getDevices();
		if (devices != null) {
			for (Device device : devices) {
				DeviceEntity deviceData;
				deviceData = new DeviceEntity(device.getType(), device.getName(), device.getId());
				if (this.deviceRegistry.findDeviceWithUri(device.getId()) != null) {
					deviceData = this.deviceRegistry.findDeviceWithUri(device.getId());
				} else {
					this.deviceRegistry.save(deviceData);
				}
				devicesList.add(deviceData);
			}
		}
		return devicesList;
	}

	/**
	 * set the given device as actual active device for playing music
	 * 
	 * @param deviceNumber
	 *            index of the device array. Order is the same as in the output in getDevices
	 * @return selected device
	 */
	public String setDevice(int deviceNumber) {
		List<DeviceEntity> devices = getDevices();
		if (devices.size() > deviceNumber) {
			this.spotifyAPICalls.setCurrentDevice(devices.get(deviceNumber).getID());
			return devices.get(deviceNumber).getName();
		}
		this.logger.warn("No device with this number was found");
		return "No device found";
	}

	/**
	 * set a device direct with the device id
	 * 
	 * @param deviceUri
	 *            from a spotify device
	 * @return true if the device is available, else false
	 */
	public boolean setDevice(String deviceUri) {
		return this.spotifyAPICalls.setCurrentDevice(deviceUri);
	}

	/**
	 * set the new name of the given device
	 * 
	 * @param deviceUri
	 *            the Uri from the device to change
	 * @param newName of the selected device
	 * @return the deviceEntity with the new name or null if the Uri is not found in the registry
	 */
	public DeviceEntity setNewDeviceName(String deviceUri, String newName) {
		DeviceEntity deviceToChange = this.deviceRegistry.findDeviceWithUri(deviceUri);
		if (deviceToChange != null) {
			deviceToChange.setName(newName);
			this.deviceRegistry.save(deviceToChange);
			return deviceToChange;
		}
		return null;
	}

}
