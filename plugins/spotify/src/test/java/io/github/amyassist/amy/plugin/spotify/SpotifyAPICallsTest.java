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

package io.github.amyassist.amy.plugin.spotify;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.miscellaneous.Device;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.plugin.api.IStorage;
import io.github.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
class SpotifyAPICallsTest {

	private SpotifyApi spotifyApi;

	private SpotifyAPICalls spotifyAPICalls;
	private Environment environment;

	private static final String CLIENT_ID = "A1B2cCD";
	private static final String CLIENT_SECRET = "E4F5E5";
	private static final String ACCESS_TOKEN = "K2K3M6";
	private static final String REFRESH_TOKEN = "Q8V55";
	@Mock
	private Properties configLoader;

	@Reference
	private IStorage storage;

	@Reference
	private TestFramework testFramework;

	@BeforeEach
	public void init() {
		this.configLoader = this.testFramework.mockService(Properties.class);
		this.testFramework.mockService(TaskScheduler.class);
		this.environment = this.testFramework.mockService(Environment.class);
		this.spotifyAPICalls = this.testFramework.setServiceUnderTest(SpotifyAPICalls.class);
		this.spotifyAPICalls = spy(this.spotifyAPICalls);
		try {
			this.spotifyApi = new SpotifyApi.Builder().setAccessToken(ACCESS_TOKEN).setClientId(CLIENT_ID)
					.setClientSecret(CLIENT_SECRET).setHost("j").setRedirectUri(new URI("s")).build();
		} catch (URISyntaxException e1) {

		}

	}

	@Test
	public void testAuthorizationCodeUri() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		URI uri = null;
		try {
			uri = new URI(
					"https://accounts.spotify.com:443/authorize?client_id=A1B2cCD&response_type=code&redirect_uri=s&"
							.concat("state=TEST&scope=user-modify-playback-state%2Cuser-read-playback-state%2Cplaylist-read-private&")
							.concat("show_dialog=true"));
		} catch (URISyntaxException e) {
		}
		assertThat(this.spotifyAPICalls.authorizationCodeUri(), equalTo(uri));
	}

	@Test
	public void testResume() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.resume(), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.resume(), equalTo(false));
	}

	@Test
	public void testPausePlaying() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		doReturn(true).when(this.spotifyAPICalls).getIsPlaying();
		assertThat(this.spotifyAPICalls.pause(), equalTo(false));
	}
	@Test
	public void testPausePaused() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		doReturn(false).when(this.spotifyAPICalls).getIsPlaying();
		assertThat(this.spotifyAPICalls.pause(), equalTo(false));
	}

	@Test
	public void testBack() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.back(), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.back(), equalTo(false));
	}

	@Test
	public void testSkip() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.skip(), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.skip(), equalTo(false));
	}

	@Test
	public void testplaySongFromUri() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.playSongFromUri("a"), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.playSongFromUri("b"), equalTo(false));
	}

	@Test
	public void testplayListFromUri() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.playListFromUri("c"), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.playListFromUri("d"), equalTo(false));
	}

	@Test
	public void testSetVolume() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.setVolume(1), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.setVolume(1), equalTo(false));
	}

	@Test
	public void testGetCurrentSong() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getCurrentPlayingContext(), equalTo(null));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getCurrentPlayingContext(), equalTo(null));
	}

	@Test
	public void testSearch() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.searchInSpotify("e", "track", 1), equalTo(null));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.searchInSpotify("g", "track", 1), equalTo(null));
	}

	@Test
	public void testFeaturedPlaylists() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getFeaturedPlaylists(1), equalTo(null));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getFeaturedPlaylists(1), equalTo(null));
	}

	private Device device1 = new Device.Builder().setId("A").setIs_active(true).setVolume_percent(10).build();
	private Device device2 = new Device.Builder().setId("B").setIs_active(false).build();
	private Device[] devices = { this.device1, this.device2 };

	@Test
	public void testgetDevices() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		assertThat(this.spotifyAPICalls.getDevices(), equalTo(new Device[0]));
	}

	@Test
	public void testSetDevice() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		assertThat(this.spotifyAPICalls.setCurrentDevice("d"), equalTo(false));
	}

	@Test
	public void testGetVolumeNoDevice() {
		assertThat(this.spotifyAPICalls.getVolume(), equalTo(-1));
	}

	@Test
	public void testGetActiveDevice() {
		doReturn(this.devices).when(this.spotifyAPICalls).getDevices();
		assertThat(this.spotifyAPICalls.getActiveDevice(), equalTo(this.device1));

		doReturn(new Device[0]).when(this.spotifyAPICalls).getDevices();
		assertThat(this.spotifyAPICalls.getActiveDevice(), equalTo(null));

	}

	@Test
	public void testCheckDeviceIsLoggedIn() {
		doReturn(this.devices).when(this.spotifyAPICalls).getDevices();
		assertThat(this.spotifyAPICalls.checkDeviceIsLoggedIn("A"), equalTo(true));
		assertThat(this.spotifyAPICalls.checkDeviceIsLoggedIn("Z"), equalTo(false));
	}

	@Test
	public void testCheckPlayerState() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		assertThat(this.spotifyAPICalls.checkPlayerState(), equalTo(false));
		doReturn(null).when(this.spotifyAPICalls).getSpotifyApi();
		assertThat(this.spotifyAPICalls.checkPlayerState(), equalTo(false));
	}

	@Test
	public void testGetSpotifyApiWithoutAccessTokenAndRefreshToken() {
		doReturn(CLIENT_ID).when(this.configLoader).getProperty(SpotifyAPICalls.SPOTIFY_CLIENTID_KEY, null);
		doReturn(CLIENT_SECRET).when(this.configLoader).getProperty(SpotifyAPICalls.SPOTIFY_CLIENTSECRET_KEY, null);
		this.spotifyAPICalls.init();
		SpotifyApi spotifyApi = this.spotifyAPICalls.getSpotifyApi();
		assertThat(spotifyApi.getClientId(), equalTo(CLIENT_ID));
		assertThat(spotifyApi.getClientSecret(), equalTo(CLIENT_SECRET));
		assertThat(spotifyApi.getAccessToken(), equalTo(null));
		assertThat(spotifyApi.getRefreshToken(), equalTo(null));
	}

	@Test
	public void testGetSpotifyApiWithoutAccessTokenAndRefreshTokenAndClientSecret() {
		doReturn(CLIENT_ID).when(this.configLoader).getProperty(SpotifyAPICalls.SPOTIFY_CLIENTID_KEY, null);
		doReturn(null).when(this.configLoader).getProperty(SpotifyAPICalls.SPOTIFY_CLIENTSECRET_KEY, null);
		this.spotifyAPICalls.init();
		SpotifyApi spotifyApi = this.spotifyAPICalls.getSpotifyApi();
		assertThat(spotifyApi, equalTo(null));
	}

	@Test
	public void testGetSpotifyApiWithoutAllData() {
		this.spotifyAPICalls.init();
		SpotifyApi spotifyApi = this.spotifyAPICalls.getSpotifyApi();
		assertThat(spotifyApi, equalTo(null));
	}

	@Test
	public void testCreateRefreshToken() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		assertThat(this.spotifyAPICalls.createRefreshToken("a"), equalTo(false));
	}

	@Test
	public void testOwnPlaylists() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getOwnPlaylists(1), equalTo(null));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getOwnPlaylists(1), equalTo(null));
	}
}
