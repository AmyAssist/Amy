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

	/**
	 * get all devices that logged in at the moment
	 * 
	 * @return empty ArrayList if no device available else Maps with the name, id and type of the device
	 */
	public List<DeviceEntity> getDevices() {
		List<DeviceEntity> devicesList = new ArrayList<>();
		Device[] devices = this.spotifyAPICalls.getDevices();
		if (devices != null) {
			for (int i = 0; i < devices.length; i++) {
				DeviceEntity deviceData;
				deviceData = new DeviceEntity(devices[i].getType(), devices[i].getName(), devices[i].getId());
				devicesList.add(deviceData);
			}
		}
		return devicesList;
	}

	/**
	 * set the given device as acutal active device for playing music
	 * 
	 * @param deviceNumber
	 *            index of the device array. Order is the same as in the output in getDevices
	 * @return selected device
	 */
	public String setDevice(int deviceNumber) {
		List<DeviceEntity> devices = getDevices();
		if (devices.size() > deviceNumber) {
			this.spotifyAPICalls.setCurrentDevice(devices.get(deviceNumber).getUri());
			return devices.get(deviceNumber).getName();
		}
		this.logger.warn("No device with this number was found");
		return "No device found";
	}

	/**
	 * set a device direct with the device id
	 * 
	 * @param deviceID
	 *            from a spotify device
	 * @return true if the device is available, else false
	 */
	public boolean setDevice(String deviceID) {
		return this.spotifyAPICalls.setCurrentDevice(deviceID);
	}
}