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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.DeviceLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.Search;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.DeviceLogic}
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class DeviceLogicTest {
	
	private static final String ID1 = "abc123";
	private static final String ID2 = "123abc";
	private static final String DEVICE_NAME1 = "Hello";
	private static final String DEVICE_NAME2 = "Godbye";
	private static final String PLAYLIST_NAME1 = "New Hits";
	private static final String PLAYLIST_NAME2 = "must popular hits";
	private static final String ARTIST_NAME1 = "David Guetta";
	private static final String ARTIST_NAME2 = "Justin Timberlake";

	private DeviceLogic deviceLogic;

	@Reference
	private TestFramework testFramework;

	private Device[] devices;
	private CurrentlyPlayingContext currentlyPlayingContext;
	private Paging<PlaylistSimplified> playlistsSpotifyFormat;
	private List<PlaylistEntity> playlistsOwnFormat;

	@Mock
	private SpotifyAPICalls spotifyAPICalls;

	@Mock
	private Search search;

	@BeforeEach
	public void init() {
		this.spotifyAPICalls = this.testFramework.mockService(SpotifyAPICalls.class);
		this.deviceLogic = this.testFramework.setServiceUnderTest(DeviceLogic.class);
		initDevices();

	}
	
	@Test
	public void testGetDevices() {
		when(this.spotifyAPICalls.getDevices()).thenReturn(devices);
		List<DeviceEntity> result = this.deviceLogic.getDevices();
		assertThat(result.get(0).getName(), equalTo(DEVICE_NAME1));
		assertThat(result.get(1).getName(), equalTo(DEVICE_NAME2));
		verify(this.spotifyAPICalls).getDevices();
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
}
