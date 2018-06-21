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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.miscellaneous.Device;

import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtention;

@ExtendWith({ MockitoExtension.class, FrameworkExtention.class })
class SpotifyAPICallsTest {

	private SpotifyApi spotifyApi;

	private SpotifyAPICalls spotifyAPICalls;

	@BeforeEach
	public void init() {
		spotifyAPICalls = new SpotifyAPICalls();
		spotifyAPICalls = spy(spotifyAPICalls);
		try {
			spotifyApi = new SpotifyApi.Builder().setAccessToken("s").setClientId("c").setClientSecret("s").setHost("j")
					.setRedirectUri(new URI("s")).build();
		} catch (URISyntaxException e1) {

		}
		doReturn(spotifyApi).when(spotifyAPICalls).getSpotifyApi();
		spotifyAPICalls.setCurrentDevice("w");

	}

	@Test
	public void testResume() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.resume(), equalTo(false));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.resume(), equalTo(false));
	}

	@Test
	public void testPause() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.pause(), equalTo(false));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.pause(), equalTo(false));
	}

	@Test
	public void testBack() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.back(), equalTo(false));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.back(), equalTo(false));
	}

	@Test
	public void testSkip() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.skip(), equalTo(false));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.skip(), equalTo(false));
	}

	@Test
	public void testplaySongFromUri() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.playSongFromUri("a"), equalTo(false));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.playSongFromUri("b"), equalTo(false));
	}

	@Test
	public void testplayListFromUri() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.playListFromUri("c"), equalTo(false));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.playListFromUri("d"), equalTo(false));
	}

	@Test
	public void testSetVolume() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.setVolume(1), equalTo(false));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.setVolume(1), equalTo(false));
	}

	@Test
	public void testGetCurrentSong() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.getCurrentSong(), equalTo(null));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.getCurrentSong(), equalTo(null));
	}

	@Test
	public void testSearch() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.searchInSpotify("e", "track", 1), equalTo(null));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.searchInSpotify("g", "track", 1), equalTo(null));
	}

	@Test
	public void testFeaturedPlaylists() {
		doReturn(true).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.getFeaturedPlaylists(1), equalTo(null));
		doReturn(false).when(spotifyAPICalls).checkPlayerState();
		assertThat(spotifyAPICalls.getFeaturedPlaylists(1), equalTo(null));
	}

	@Test
	public void testgetDevices() {
		assertThat(spotifyAPICalls.getDevices(), equalTo(new Device[0]));
	}

	@Test
	public void testCheckPlayerState() {
		assertThat(spotifyAPICalls.checkPlayerState(), equalTo(false));
		doReturn(null).when(spotifyAPICalls).getSpotifyApi();
		assertThat(spotifyAPICalls.checkPlayerState(), equalTo(false));
	}

}
