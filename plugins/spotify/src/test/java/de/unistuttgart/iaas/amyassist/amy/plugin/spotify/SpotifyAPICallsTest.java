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
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.miscellaneous.Device;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
class SpotifyAPICallsTest {

	private SpotifyApi spotifyApi;

	private SpotifyAPICalls spotifyAPICalls;

	private static final String CLIENT_ID = "A1B2cCD";
	private static final String CLIENT_SECRET = "E4F5E5";
	private static final String ACCESS_TOKEN = "K2K3M6";
	private static final String REFRESH_TOKEN = "Q8V55";
	@Mock
	private ConfigLoader configLoader;

	@Reference
	IStorage storage;

	@Reference
	private TestFramework testFramework;

	@BeforeEach
	public void init() {
		this.configLoader = this.testFramework.mockService(ConfigLoader.class);
		this.testFramework.mockService(TaskSchedulerAPI.class);
		this.spotifyAPICalls = testFramework.setServiceUnderTest(SpotifyAPICalls.class);
		this.spotifyAPICalls = spy(this.spotifyAPICalls);
		try {
			spotifyApi = new SpotifyApi.Builder().setAccessToken(ACCESS_TOKEN).setClientId(CLIENT_ID)
					.setClientSecret(CLIENT_SECRET).setHost("j").setRedirectUri(new URI("s")).build();
		} catch (URISyntaxException e1) {

		}

	}

	@Test
	public void testAuthorizationCodeUri() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
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
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.resume(), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.resume(), equalTo(false));
	}

	@Test
	public void testPause() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.pause(), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.pause(), equalTo(false));
	}

	@Test
	public void testBack() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.back(), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.back(), equalTo(false));
	}

	@Test
	public void testSkip() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.skip(), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.skip(), equalTo(false));
	}

	@Test
	public void testplaySongFromUri() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.playSongFromUri("a"), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.playSongFromUri("b"), equalTo(false));
	}

	@Test
	public void testplayListFromUri() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.playListFromUri("c"), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.playListFromUri("d"), equalTo(false));
	}

	@Test
	public void testSetVolume() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.setVolume(1), equalTo(false));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.setVolume(1), equalTo(false));
	}

	@Test
	public void testGetCurrentSong() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getCurrentSong(), equalTo(null));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getCurrentSong(), equalTo(null));
	}

	@Test
	public void testSearch() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.searchInSpotify("e", "track", 1), equalTo(null));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.searchInSpotify("g", "track", 1), equalTo(null));
	}

	@Test
	public void testFeaturedPlaylists() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
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
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		assertThat(this.spotifyAPICalls.getDevices(), equalTo(new Device[0]));
	}

	@Test
	public void testSetDevice() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		assertThat(this.spotifyAPICalls.setCurrentDevice("d"), equalTo(false));
	}

	@Test
	public void testGetVolume() {
		doReturn(this.device1).when(this.spotifyAPICalls).getActiveDevice();
		assertThat(this.spotifyAPICalls.getVolume(), equalTo(10));
	}

	@Test
	public void testGetVolumeNoDevice() {
		doReturn(null).when(this.spotifyAPICalls).getActiveDevice();
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
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		assertThat(this.spotifyAPICalls.checkPlayerState(), equalTo(false));
		doReturn(null).when(this.spotifyAPICalls).getSpotifyApi();
		assertThat(this.spotifyAPICalls.checkPlayerState(), equalTo(false));
	}

	@Test
	public void testGetSpotifyApiAllData() {
		doReturn(CLIENT_ID).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_CLIENTID_KEY);
		doReturn(CLIENT_SECRET).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_CLIENTSECRET_KEY);
		doReturn(REFRESH_TOKEN).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_REFRSHTOKEN_KEY);
		this.storage.put(SpotifyAPICalls.SPOTIFY_ACCESSTOKEN, ACCESS_TOKEN);
		SpotifyApi spotifyApi = this.spotifyAPICalls.getSpotifyApi();
		assertThat(spotifyApi.getClientId(), equalTo(CLIENT_ID));
		assertThat(spotifyApi.getClientSecret(), equalTo(CLIENT_SECRET));
		assertThat(spotifyApi.getAccessToken(), equalTo(ACCESS_TOKEN));
		assertThat(spotifyApi.getRefreshToken(), equalTo(REFRESH_TOKEN));
	}

	@Test
	public void testGetSpotifyApiWithoutAccessToken() {
		doReturn(CLIENT_ID).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_CLIENTID_KEY);
		doReturn(CLIENT_SECRET).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_CLIENTSECRET_KEY);
		doReturn(REFRESH_TOKEN).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_REFRSHTOKEN_KEY);
		SpotifyApi spotifyApi = this.spotifyAPICalls.getSpotifyApi();
		assertThat(spotifyApi.getClientId(), equalTo(CLIENT_ID));
		assertThat(spotifyApi.getClientSecret(), equalTo(CLIENT_SECRET));
		assertThat(spotifyApi.getAccessToken(), equalTo(null));
		assertThat(spotifyApi.getRefreshToken(), equalTo(REFRESH_TOKEN));
	}

	@Test
	public void testGetSpotifyApiWithoutAccessTokenAndRefreshToken() {
		doReturn(CLIENT_ID).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_CLIENTID_KEY);
		doReturn(CLIENT_SECRET).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_CLIENTSECRET_KEY);
		doReturn(null).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_REFRSHTOKEN_KEY);
		SpotifyApi spotifyApi = this.spotifyAPICalls.getSpotifyApi();
		assertThat(spotifyApi.getClientId(), equalTo(CLIENT_ID));
		assertThat(spotifyApi.getClientSecret(), equalTo(CLIENT_SECRET));
		assertThat(spotifyApi.getAccessToken(), equalTo(null));
		assertThat(spotifyApi.getRefreshToken(), equalTo(null));
	}

	@Test
	public void testGetSpotifyApiWithoutAccessTokenAndRefreshTokenAndClientSecret() {
		doReturn(CLIENT_ID).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_CLIENTID_KEY);
		doReturn(null).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_CLIENTSECRET_KEY);
		SpotifyApi spotifyApi = this.spotifyAPICalls.getSpotifyApi();
		assertThat(spotifyApi, equalTo(null));
	}

	@Test
	public void testGetSpotifyApiWithoutAllData() {
		doReturn(null).when(this.configLoader).get(SpotifyAPICalls.SPOTIFY_CLIENTID_KEY);
		SpotifyApi spotifyApi = this.spotifyAPICalls.getSpotifyApi();
		assertThat(spotifyApi, equalTo(null));
	}

	@Test
	public void testCreateRefreshToken() {
		doReturn(this.spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		assertThat(this.spotifyAPICalls.createRefreshToken("a"), equalTo(false));
	}

	@Test
	public void testSetClientSecret() {
		this.spotifyAPICalls.setClientSecret(CLIENT_SECRET);
		verify(this.configLoader).set(SpotifyAPICalls.SPOTIFY_CLIENTSECRET_KEY, CLIENT_SECRET);
	}

	@Test
	public void testSetClientId() {
		this.spotifyAPICalls.setClientID(CLIENT_ID);
		verify(this.configLoader).set(SpotifyAPICalls.SPOTIFY_CLIENTID_KEY, CLIENT_ID);
	}

	@Test
	public void testOwnPlaylists() {
		doReturn(spotifyApi).when(this.spotifyAPICalls).getSpotifyApi();
		this.spotifyAPICalls.setCurrentDevice("w");
		doReturn(true).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getOwnPlaylists(1), equalTo(null));
		doReturn(false).when(this.spotifyAPICalls).checkPlayerState();
		assertThat(this.spotifyAPICalls.getOwnPlaylists(1), equalTo(null));
	}
}
