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

import java.lang.reflect.Field;
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

import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtention;

@ExtendWith({ MockitoExtension.class, FrameworkExtention.class })
class PlayerlogicTest {

	private PlayerLogic playerLogic;
	private Device[] devices;
	private CurrentlyPlayingContext currentlyPlayingContext;
	private List<Map<String, String>> featuredPlaylists;

	@Mock
	private SpotifyAPICalls spotifyAPICalls;

	@Mock
	private Search search;
	

	@BeforeEach
	public void init() {
		playerLogic = new PlayerLogic();
		Field apiCallField;
		Field searchField;
		initDevices();
		initCurrentTrack();
		initFeaturedPlaylist();

		try {
			apiCallField = PlayerLogic.class.getDeclaredField("spotifyAPICalls");
			apiCallField.setAccessible(true);
			apiCallField.set(playerLogic, spotifyAPICalls);
			searchField = PlayerLogic.class.getDeclaredField("search");
			searchField.setAccessible(true);
			searchField.set(playerLogic, search);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
		}
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
		playerLogic.firstTimeInit("abc", "abc");
		verify(spotifyAPICalls).setClientID("abc");
		verify(spotifyAPICalls).setClientSecret("abc");
		verify(spotifyAPICalls).authorizationCodeUri();
		verifyNoMoreInteractions(spotifyAPICalls);
	}

	@Test
	public void testInputAuthCode() {
		playerLogic.inputAuthCode("abc");
		verify(spotifyAPICalls).createRefreshToken("abc");
		verifyNoMoreInteractions(spotifyAPICalls);
	}

	@Test
	public void testGetDevices() {
		when(spotifyAPICalls.getDevices()).thenReturn(devices);
		List<String> result = playerLogic.getDevices();
		assertThat(result.get(0), equalTo("Hello"));
		assertThat(result.get(1), equalTo("Godbye"));
		verify(spotifyAPICalls).getDevices();
		reset(spotifyAPICalls);

		when(spotifyAPICalls.getDevices()).thenReturn(null);
		List<String> result2 = playerLogic.getDevices();
		assertThat(result2.isEmpty(), equalTo(true));
		verify(spotifyAPICalls).getDevices();
		reset(spotifyAPICalls);
	}

	@Test
	public void testSetDevice() {
		when(spotifyAPICalls.getDevices()).thenReturn(null);
		assertThat(playerLogic.setDevice(0), equalTo("No device found"));

		when(spotifyAPICalls.getDevices()).thenReturn(devices);
		assertThat(playerLogic.setDevice(0), equalTo("Hello"));
		reset(spotifyAPICalls);
	}

	@Test
	public void testSearch() {
		playerLogic.search("Hello", "track", 1);
		verify(search).searchList("Hello", "track", 1);
	}

	@Test
	public void testConvertSearchOutputString() {

	}

	@Test
	public void testPlayEmptyList() {
		when(search.getFeaturedPlaylists()).thenReturn(new ArrayList<>());
		assertThat(playerLogic.play().isEmpty(), equalTo(true));
	
	}
	
	@Test
	public void testPlayNotEmptyList() {
		when(search.getFeaturedPlaylists()).thenReturn(featuredPlaylists);
		when(spotifyAPICalls.playListFromUri(any())).thenReturn(true);
		assertThat(playerLogic.play().get(SpotifyConstants.ITEM_URI), equalTo("123"));
		verify(spotifyAPICalls).playListFromUri("123");
	}
	

	@Test
	public void testResume() {
		playerLogic.resume();
		verify(spotifyAPICalls).resume();
		verifyNoMoreInteractions(spotifyAPICalls);
	}

	@Test
	public void testPause() {
		playerLogic.pause();
		verify(spotifyAPICalls).pause();
		verifyNoMoreInteractions(spotifyAPICalls);
	}

	@Test
	public void testSkip() {
		playerLogic.skip();
		verify(spotifyAPICalls).skip();
		verifyNoMoreInteractions(spotifyAPICalls);
	}

	@Test
	public void testBack() {
		playerLogic.back();
		verify(spotifyAPICalls).back();
		verifyNoMoreInteractions(spotifyAPICalls);
	}

	@Test
	public void testGetCurrentSong() {
		when(spotifyAPICalls.getCurrentSong()).thenReturn(currentlyPlayingContext);
		when(search.createTrackOutput(any(), any())).thenCallRealMethod();
		Map<String, String> result1 = playerLogic.getCurrentSong();
		assertThat(result1.get(SpotifyConstants.ITEM_NAME), equalTo("Flames"));
		assertThat(result1.get(SpotifyConstants.ITEM_TYPE), equalTo("track"));
		assertThat(result1.get(SpotifyConstants.ITEM_URI), equalTo("123"));
		assertThat(result1.get(SpotifyConstants.ARTIST_NAME), equalTo("David Guetta, Hans Dieter"));
		
		when(spotifyAPICalls.getCurrentSong()).thenReturn(null);
		Map<String, String> result2 = playerLogic.getCurrentSong();
		assertThat(result2.isEmpty(), equalTo(true));
	}

	@Test
	public void testSetVolumeString() {
		when(spotifyAPICalls.getVolume()).thenReturn(50);
		assertThat(playerLogic.setVolume("mute"), equalTo(0));
		verify(spotifyAPICalls).setVolume(0);
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);

		when(spotifyAPICalls.getVolume()).thenReturn(50);
		assertThat(playerLogic.setVolume("max"), equalTo(100));
		verify(spotifyAPICalls).setVolume(100);
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);

		when(spotifyAPICalls.getVolume()).thenReturn(90);
		assertThat(playerLogic.setVolume("up"), equalTo(100));
		verify(spotifyAPICalls).setVolume(100);
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);

		when(spotifyAPICalls.getVolume()).thenReturn(10);
		assertThat(playerLogic.setVolume("down"), equalTo(0));
		verify(spotifyAPICalls).setVolume(0);
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);

		when(spotifyAPICalls.getVolume()).thenReturn(91);
		assertThat(playerLogic.setVolume("up"), equalTo(-1));
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);

		when(spotifyAPICalls.getVolume()).thenReturn(9);
		assertThat(playerLogic.setVolume("down"), equalTo(-1));
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);

		when(spotifyAPICalls.getVolume()).thenReturn(50);
		assertThat(playerLogic.setVolume("downM"), equalTo(-1));
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);
	}

	@Test
	public void testVolumeInt() {
		assertThat(playerLogic.setVolume(-2), equalTo(-1));
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);

		assertThat(playerLogic.setVolume(101), equalTo(-1));
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);

		assertThat(playerLogic.setVolume(1), equalTo(1));
		verify(spotifyAPICalls).setVolume(1);
		verifyNoMoreInteractions(spotifyAPICalls);
		reset(spotifyAPICalls);
	}

}
