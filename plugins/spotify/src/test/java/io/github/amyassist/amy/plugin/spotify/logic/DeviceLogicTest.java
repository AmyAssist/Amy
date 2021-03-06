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

package io.github.amyassist.amy.plugin.spotify.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.plugin.spotify.SpotifyAPICalls;
import io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity;
import io.github.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import io.github.amyassist.amy.plugin.spotify.logic.DeviceLogic;
import io.github.amyassist.amy.plugin.spotify.logic.Search;
import io.github.amyassist.amy.plugin.spotify.registry.DeviceRegistry;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test class for {@link io.github.amyassist.amy.plugin.spotify.logic.DeviceLogic}
 * 
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class DeviceLogicTest {

	private static final String ID1 = "abc123";
	private static final String ID2 = "123abc";
	private static final String DEVICE_NAME1 = "Hello";
	private static final String DEVICE_NAME2 = "Godbye";

	private DeviceLogic deviceLogic;

	@Reference
	private TestFramework testFramework;

	private Device[] devices;
	private SpotifyAPICalls spotifyAPICalls;
	private DeviceRegistry registry;

	@BeforeEach
	public void init() {
		this.registry = this.testFramework.mockService(DeviceRegistry.class);
		this.spotifyAPICalls = this.testFramework.mockService(SpotifyAPICalls.class);
		this.deviceLogic = this.testFramework.setServiceUnderTest(DeviceLogic.class);
		initDevices();

	}

	public void initDevices() {
		devices = new Device[2];
		devices[0] = new Device.Builder().setId(ID1).setIs_active(true).setName(DEVICE_NAME1).setVolume_percent(50)
				.setType("Smartphone").build();
		devices[1] = new Device.Builder().setId(ID2).setIs_active(false).setName(DEVICE_NAME2).setType("Computer")
				.build();
	}

	@Test
	public void testGetDevicesWithNoDevices() {
		when(this.spotifyAPICalls.getDevices()).thenReturn(null);
		List<DeviceEntity> result = this.deviceLogic.getDevices();
		assertThat(result.isEmpty(), equalTo(true));
		verify(this.spotifyAPICalls).getDevices();
	}

	@Test
	public void testGetDevices() {
		when(this.spotifyAPICalls.getDevices()).thenReturn(devices);
		List<DeviceEntity> result = this.deviceLogic.getDevices();
		assertThat(result.get(0).getName(), equalTo(DEVICE_NAME1));
		assertThat(result.get(1).getName(), equalTo(DEVICE_NAME2));
		verify(this.spotifyAPICalls).getDevices();
	}

	@Test
	public void testGetDevicesOneInRegistry() {
		DeviceEntity entity = new DeviceEntity("Computer", DEVICE_NAME1, ID1);
		when(this.spotifyAPICalls.getDevices()).thenReturn(devices);
		when(this.registry.findDeviceWithUri(ID1)).thenReturn(entity);
		List<DeviceEntity> result = this.deviceLogic.getDevices();
		assertThat(result.get(0).getName(), equalTo(DEVICE_NAME1));
		assertThat(result.get(1).getName(), equalTo(DEVICE_NAME2));
		verify(this.spotifyAPICalls).getDevices();
	}

	@Test
	public void testSetDevice() {
		when(this.spotifyAPICalls.getDevices()).thenReturn(null);
		assertThat(this.deviceLogic.setDevice(0), equalTo("No device found"));

		when(this.spotifyAPICalls.getDevices()).thenReturn(devices);
		assertThat(this.deviceLogic.setDevice(0), equalTo(DEVICE_NAME1));
	}

	@Test
	public void testSetDeviceWithId() {
		assertThat(this.deviceLogic.setDevice("1"), equalTo(false));
		verify(this.spotifyAPICalls).setCurrentDevice("1");
	}
	
	@Test
	public void testSetNewName() {
		DeviceEntity entity = new DeviceEntity("Computer", DEVICE_NAME1, ID1);
		when(this.registry.findDeviceWithUri(ID1)).thenReturn(entity);
		assertThat(this.deviceLogic.setNewDeviceName(ID1, DEVICE_NAME2).getName(), equalTo(DEVICE_NAME2));
		verify(this.registry).save(entity);
	}
	@Test
	public void testSetNewNameNotinReg() {
		DeviceEntity entity = new DeviceEntity("Computer", DEVICE_NAME1, ID1);
		when(this.registry.findDeviceWithUri(ID1)).thenReturn(null);
		assertThat(this.deviceLogic.setNewDeviceName(ID1, DEVICE_NAME2), equalTo(null));
	}
}
