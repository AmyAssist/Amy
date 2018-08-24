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

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.TrackEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.DeviceLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.PlayerLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.Search;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.SearchTypes;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * test class for spotify speech
 * 
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
class SpotifySpeechTest {

	@Reference
	private TestFramework testFramework;

	private PlayerLogic playerlogic;
	private DeviceLogic deviceLogic;
	private Search searchLogic;
	private SpotifySpeech speech;

	@Mock
	private TrackEntity track;
	@Mock
	private EntityData entityData;
	@Mock
	private EntityData songId;
	@Mock
	private PlaylistEntity playlist;

	@BeforeEach
	void init() {
		this.playerlogic = this.testFramework.mockService(PlayerLogic.class);
		this.deviceLogic = this.testFramework.mockService(DeviceLogic.class);
		this.searchLogic = this.testFramework.mockService(Search.class);
		this.speech = this.testFramework.setServiceUnderTest(SpotifySpeech.class);
	}

	@Test
	void testGetDevices() {
		DeviceEntity deviceEntity = new DeviceEntity();
		deviceEntity.setName("test");
		List<DeviceEntity> list = new ArrayList<>();
		list.add(deviceEntity);
		when(this.deviceLogic.getDevices()).thenReturn(list);
		assertThat(this.speech.getDevices(new HashMap<>()), equalTo("Following Devices are online: \n0. test\n"));
		when(this.deviceLogic.getDevices()).thenReturn(new ArrayList<>());
		assertThat(this.speech.getDevices(new HashMap<>()), equalTo("No devices are online"));
	}

	@Test
	void testGetCurrentSong() {
		when(this.track.toString()).thenReturn("test");
		when(this.playerlogic.getCurrentSong()).thenReturn(this.track);
		assertThat(this.speech.getCurrentSong(new HashMap<>()), equalTo("track: test"));
		when(this.playerlogic.getCurrentSong()).thenReturn(null);
		assertThat(this.speech.getCurrentSong(new HashMap<>()), equalTo("No song is playing"));
	}

	@Test
	void testGetPlaylist() {
		Map<String, EntityData> map = new HashMap<>();
		List<PlaylistEntity> list = new ArrayList<>();
		when(this.playlist.toString()).thenReturn("test");
		list.add(this.playlist);
		when(this.entityData.getString()).thenReturn("featured");
		when(this.searchLogic.searchFeaturedPlaylists(5)).thenReturn(list);
		map.put("type", this.entityData);
		this.speech.getPlaylists(map);
		when(this.entityData.getString()).thenReturn("own");
		when(this.searchLogic.searchOwnPlaylists(5)).thenReturn(list);
		this.speech.getPlaylists(map);
		when(this.entityData.getString()).thenReturn("test");
		this.speech.getPlaylists(map);
	}

	@Test
	void testPlaySomething() {
		when(this.playlist.toString()).thenReturn("test");
		when(this.playerlogic.play()).thenReturn(this.playlist);
		assertThat(this.speech.playSomething(new HashMap<>()), equalTo("test"));
		when(this.playerlogic.play()).thenReturn(null);
		assertThat(this.speech.playSomething(new HashMap<>()), equalTo("No playlist available"));
	}

	@Test
	void testControlErr() {
		Map<String, EntityData> map = new HashMap<>();
		map.put("type", this.entityData);
		when(this.entityData.getString()).thenReturn("back");
		this.speech.control(map);
		verify(this.playerlogic).back();

		when(this.entityData.getString()).thenReturn("skip");
		this.speech.control(map);
		verify(this.playerlogic).skip();

		when(this.entityData.getString()).thenReturn("pause");
		this.speech.control(map);
		verify(this.playerlogic).pause();

		when(this.entityData.getString()).thenReturn("resume");
		this.speech.control(map);
		verify(this.playerlogic).resume();
		when(this.entityData.getString()).thenReturn("bla");
		assertThat(this.speech.control(map), equalTo("An error occurred"));
	}

	@Test
	void testControl() {
		Map<String, EntityData> map = new HashMap<>();
		map.put("type", this.entityData);
		when(this.playerlogic.back()).thenReturn(true);
		when(this.entityData.getString()).thenReturn("back");
		this.speech.control(map);

		when(this.entityData.getString()).thenReturn("skip");
		when(this.playerlogic.skip()).thenReturn(true);
		this.speech.control(map);

		when(this.entityData.getString()).thenReturn("pause");
		when(this.playerlogic.pause()).thenReturn(true);
		this.speech.control(map);

		when(this.entityData.getString()).thenReturn("resume");
		when(this.playerlogic.resume()).thenReturn(true);
		this.speech.control(map);
		when(this.entityData.getString()).thenReturn("bla");
		assertThat(this.speech.control(map), equalTo("An error occurred"));
	}

	@Test
	void testVolume() {
		when(this.entityData.getString()).thenReturn("up");
		Map<String, EntityData> map = new HashMap<>();
		map.put("volumeoption", this.entityData);
		this.speech.volume(map);
		verify(this.playerlogic).setVolume("up");
	}

	@Test
	void testSetDeviceId() {
		when(this.entityData.getNumber()).thenReturn(1);
		Map<String, EntityData> map = new HashMap<>();
		map.put("deviceid", this.entityData);
		this.speech.setDeviceId(map);
		verify(this.deviceLogic).setDevice(1);
	}

	@Test
	void testSetDeviceName() {
		DeviceEntity device = new DeviceEntity();
		device.setName("test");
		device.setID("uri");
		List<DeviceEntity> list = new ArrayList<>();
		list.add(device);
		when(this.deviceLogic.getDevices()).thenReturn(list);
		when(this.entityData.getString()).thenReturn("test");
		Map<String, EntityData> map = new HashMap<>();
		map.put("devicename", this.entityData);
		assertThat(this.speech.setDeviceName(map), equalTo("test"));
		verify(this.deviceLogic).setDevice("uri");

		when(this.deviceLogic.getDevices()).thenReturn(new ArrayList<>());
		assertThat(this.speech.setDeviceName(map), equalTo("Device not found"));
	}

	@Test
	void testPlayPlaylist() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.entityData.getString()).thenReturn("own");
		map.put("type", this.entityData);
		when(this.songId.getNumber()).thenReturn(1);
		map.put("songid", this.songId);
		assertThat(this.speech.playPlaylistId(map), equalTo("Element is not available"));
		verify(this.playerlogic).playPlaylist(1, SearchTypes.USER_PLAYLISTS);
		
		when(this.playlist.toString()).thenReturn("test");
		List<PlaylistEntity> list = new ArrayList<>();
		list.add(this.playlist);
		when(this.playerlogic.playPlaylist(1, SearchTypes.FEATURED_PLAYLISTS)).thenReturn(this.playlist);
		when(this.entityData.getString()).thenReturn("featured");
		assertThat(this.speech.playPlaylistId(map), equalTo("test"));
	}
	
	@Test
	void testSearchASong() {
		Map<String, EntityData> map = new HashMap<>();
		when(this.entityData.getString()).thenReturn("test");
		map.put("songname", this.entityData);
		assertThat(this.speech.searchASong(map), equalTo("Element is not available"));
		verify(this.searchLogic).searchforTracks("test", 1);
	}
	
	@Test
	void testGetDeviceNames() {
		List<DeviceEntity> list = new ArrayList<>();
		DeviceEntity device = new DeviceEntity();
		device.setName("test");
		list.add(device);
		when(this.deviceLogic.getDevices()).thenReturn(list);
		assertThat(this.speech.getDeviceNames().get(0), equalTo("test"));
	}
}
