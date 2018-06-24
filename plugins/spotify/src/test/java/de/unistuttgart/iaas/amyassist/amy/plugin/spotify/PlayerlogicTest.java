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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
class PlayerLogicTest {

	private PlayerLogic playerLogic;

	@Reference
	private TestFramework testFramework;

	private Device[] devices;
	private CurrentlyPlayingContext currentlyPlayingContext;
	private List<Map<String, String>> featuredPlaylists;

	@Mock
	private SpotifyAPICalls spotifyAPICalls;

	@Mock
	private Search search;
	
	

	@BeforeEach
	public void init() {	
		this.spotifyAPICalls = this.testFramework.mockService(SpotifyAPICalls.class);
		this.search = this.testFramework.mockService(Search.class);
		this.testFramework.mockService(StringGenerator.class);
		this.playerLogic = this.testFramework.setServiceUnderTest(PlayerLogic.class);
		initDevices();
		initCurrentTrack();
		initFeaturedPlaylist();
		reset(this.spotifyAPICalls);

	}

	public void initDevices() {
		devices = new Device[2];
		devices[0] = new Device.Builder().setId("abc123").setIs_active(true).setName("Hello").setVolume_percent(50)
				.setType("Smartphone").build();
		devices[1] = new Device.Builder().setId("123abc").setIs_active(false).setName("Godbye").setType("Computer")
				.build();
	}

	public void initCurrentTrack() {

		Track track1 = new Track.Builder().setName("Flames").setUri("123")
				.setArtists(new ArtistSimplified.Builder().setName("David Guetta").build(),
						new ArtistSimplified.Builder().setName("Hans Dieter").build())
				.build();
		currentlyPlayingContext = new CurrentlyPlayingContext.Builder().setItem(track1).build();
	}

	public void initFeaturedPlaylist() {
		featuredPlaylists = new ArrayList<>();
		Map<String, String> entry1 = new HashMap<>();
		entry1.put(SpotifyConstants.ITEM_URI, "abc");
		featuredPlaylists.add(entry1);
		Map<String, String> entry2 = new HashMap<>();
		entry2.put(SpotifyConstants.ITEM_URI, "123");
		featuredPlaylists.add(entry2);

	}

	@Test
	public void testFirstTimeInit() {
		this.playerLogic.firstTimeInit("abc", "abc");
		verify(this.spotifyAPICalls).setClientID("abc");
		verify(this.spotifyAPICalls).setClientSecret("abc");
		verify(this.spotifyAPICalls).authorizationCodeUri();
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testInputAuthCode() {
		this.playerLogic.inputAuthCode("abc");
		verify(this.spotifyAPICalls).createRefreshToken("abc");
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testGetDevices() {
		when(this.spotifyAPICalls.getDevices()).thenReturn(devices);
		List<Map<String, String>> result = this.playerLogic.getDevices();
		assertThat(result.get(0).get(SpotifyConstants.DEVICE_NAME), equalTo("Hello"));
		assertThat(result.get(1).get(SpotifyConstants.DEVICE_NAME), equalTo("Godbye"));
		verify(this.spotifyAPICalls).getDevices();
	}

	@Test
	public void testGetDevicesWithNoDevices() {
		when(this.spotifyAPICalls.getDevices()).thenReturn(null);
		List<Map<String, String>> result = this.playerLogic.getDevices();
		assertThat(result.isEmpty(), equalTo(true));
		verify(this.spotifyAPICalls).getDevices();
		reset(this.spotifyAPICalls);
	}

	@Test
	public void testSetDevice() {
		when(this.spotifyAPICalls.getDevices()).thenReturn(null);
		assertThat(this.playerLogic.setDevice(0), equalTo("No device found"));

		when(this.spotifyAPICalls.getDevices()).thenReturn(devices);
		assertThat(this.playerLogic.setDevice(0), equalTo("Hello"));
		reset(this.spotifyAPICalls);
	}

	@Test
	public void testSearch() {
		this.playerLogic.search("Hello", "track", 1);
		verify(search).searchList("Hello", "track", 1);
	}

	@Test
	public void testConvertSearchOutputString() {

	}

	@Test
	public void testPlayEmptyList() {
		when(search.getFeaturedPlaylists()).thenReturn(new ArrayList<>());
		assertThat(this.playerLogic.play().isEmpty(), equalTo(true));

	}

	@Test
	public void testPlayNotEmptyList() {
		when(search.getFeaturedPlaylists()).thenReturn(featuredPlaylists);
		when(this.spotifyAPICalls.playListFromUri(any())).thenReturn(true);
		assertThat(this.playerLogic.play().get(SpotifyConstants.ITEM_URI), equalTo("123"));
		verify(this.spotifyAPICalls).playListFromUri("123");
	}

	@Test
	public void testResume() {
		this.playerLogic.resume();
		verify(this.spotifyAPICalls).resume();
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testPause() {
		this.playerLogic.pause();
		verify(this.spotifyAPICalls).pause();
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testSkip() {
		this.playerLogic.skip();
		verify(this.spotifyAPICalls).skip();
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testBack() {
		this.playerLogic.back();
		verify(this.spotifyAPICalls).back();
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testGetCurrentSong() {
		when(this.spotifyAPICalls.getCurrentSong()).thenReturn(currentlyPlayingContext);
		when(search.createTrackOutput(any())).thenCallRealMethod();
		Map<String, String> result1 = this.playerLogic.getCurrentSong();
		assertThat(result1.get(SpotifyConstants.ITEM_NAME), equalTo("Flames"));
		assertThat(result1.get(SpotifyConstants.ITEM_TYPE), equalTo("track"));
		assertThat(result1.get(SpotifyConstants.ITEM_URI), equalTo("123"));
		assertThat(result1.get(SpotifyConstants.ARTIST_NAME), equalTo("David Guetta, Hans Dieter"));

		when(this.spotifyAPICalls.getCurrentSong()).thenReturn(null);
		Map<String, String> result2 = this.playerLogic.getCurrentSong();
		assertThat(result2.isEmpty(), equalTo(true));
	}

	@Test
	public void testSetVolumeStringMute() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(50);
		assertThat(this.playerLogic.setVolume("mute"), equalTo(0));
		verify(this.spotifyAPICalls).setVolume(0);
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testSetVolumeStringMax() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(50);
		assertThat(this.playerLogic.setVolume("max"), equalTo(100));
		verify(this.spotifyAPICalls).setVolume(100);
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testSetVolumeStringUpInRange() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(90);
		assertThat(this.playerLogic.setVolume("up"), equalTo(100));
		verify(this.spotifyAPICalls).setVolume(100);
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testSetVolumeStringDownInRange() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(10);
		assertThat(this.playerLogic.setVolume("down"), equalTo(0));
		verify(this.spotifyAPICalls).setVolume(0);
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testSetVolumeStringUpOurOfRange() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(91);
		assertThat(this.playerLogic.setVolume("up"), equalTo(100));
		verify(this.spotifyAPICalls).setVolume(100);
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testSetVolumeStringDownOutOfRange() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(9);
		assertThat(this.playerLogic.setVolume("down"), equalTo(0));
		verify(this.spotifyAPICalls).setVolume(0);
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testSetVolumeStringWrongString() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(50);
		assertThat(this.playerLogic.setVolume("downM"), equalTo(-1));
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testVolumeIntOutOfRangeNegative() {
		assertThat(this.playerLogic.setVolume(-2), equalTo(-1));
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testVolumeIntOutOfRangePositve() {
		assertThat(this.playerLogic.setVolume(101), equalTo(-1));
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

	@Test
	public void testVolumeInt() {
		assertThat(this.playerLogic.setVolume(1), equalTo(1));
		verify(this.spotifyAPICalls).setVolume(1);
		verifyNoMoreInteractions(this.spotifyAPICalls);
	}

}
