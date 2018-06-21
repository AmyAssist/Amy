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

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.special.FeaturedPlaylists;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.requests.IRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.browse.GetListOfFeaturedPlaylistsRequest;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.GetUsersAvailableDevicesRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SetVolumeForUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToNextTrackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToPreviousTrackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.search.SearchItemRequest;

public class SpotifyAPICalls {

	/**
	 * clientID from a spotify developer account
	 */
	private String clientID = null;
	/**
	 * clientSecret from a spotify developer account
	 */
	private String clientSecret = null;
	/**
	 * URI to redirect callbacks from the login screen
	 */
	private final URI redirectURI = SpotifyHttpManager.makeUri("http://localhost:8888");
	/**
	 * refreshToken is needed to generate new access tokens
	 */
	private String refreshToken = null;
	/**
	 * id from the current device
	 */
	/**
	 * Rules for the spotify user authentication e.g. access to the playcontrol
	 */
	private static final String SPOTIFY_RULES = "user-modify-playback-state,user-read-playback-state";
	private String deviceID = null;

	private AuthorizationCodeCredentials authorizationCodeCredentials;
	private AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest;

	private static final String SPOTIFY_CLIENTSECRET = "spotify_clientSecret";
	private static final String SPOTIFY_CLIENTID = "spotify_clientId";
	private static final String SPOTIFY_REFRSHTOKEN = "spotify_refreshToken";
	private static final String SPOTIFY_DEVICEID = "spotify_device_Id";
	private static final int TOKEN_EXPIRE_TIME = 120;

	private ConfigLoader configLoader = new ConfigLoader();

	private boolean firstTime = true;
	private Logger logger;

	private SpotifyApi spotifyApi = null;

	public SpotifyAPICalls() {
		init();
	}

	private final void init() {
		this.logger = LoggerFactory.getLogger(SpotifyAPICalls.class);
		if (configLoader.get(SPOTIFY_CLIENTID) != null && configLoader.get(SPOTIFY_CLIENTSECRET) != null) {
			this.clientID = configLoader.get(SPOTIFY_CLIENTID);
			this.clientSecret = configLoader.get(SPOTIFY_CLIENTSECRET);
			this.spotifyApi = new SpotifyApi.Builder().setClientId(this.clientID).setClientSecret(this.clientSecret)
					.setRedirectUri(this.redirectURI).build();
		} else {
			this.logger.warn("Client Secret and ID missing. Please insert the config file");
		}

		if (configLoader.get(SPOTIFY_REFRSHTOKEN) != null) {
			this.refreshToken = configLoader.get(SPOTIFY_REFRSHTOKEN);
		} else {
			this.logger.warn("Please exec the Authorization first");
		}
		if (configLoader.get(SPOTIFY_DEVICEID) != null && checkDeviceIsLoggedIn(configLoader.get(SPOTIFY_DEVICEID))) {
			deviceID = configLoader.get(SPOTIFY_DEVICEID);
		} else if (getDevices().length > 0) {
			deviceID = getDevices()[0].getId();
		} else {
			logger.warn("no device is logged in");
		}
	}

	/**
	 * create a authentication link for the User to authenticate his spotify
	 * account. The Link create a authentication code for the next step
	 * 
	 * @return
	 */
	public URI authorizationCodeUri() {
		AuthorizationCodeUriRequest authorizationCodeUriRequest = this.spotifyApi.authorizationCodeUri().state("TEST")
				.scope(SPOTIFY_RULES).show_dialog(true).build();
		return authorizationCodeUriRequest.execute();
	}

	/**
	 * create the refresh token in he authorization object with the authCode
	 * 
	 * @param authCode
	 *            Callback from the login link
	 */
	public void inputAuthCode(String authCode) {
		createRefreshToken(authCode);
	}

	/**
	 * create a persistent refresh token with the authentication Code
	 * 
	 * @param authCode
	 *            from authorizationCodeUri()
	 */
	public void createRefreshToken(String authCode) {
		AuthorizationCodeRequest authorizationCodeRequest = this.spotifyApi.authorizationCode(authCode).build();
		try {
			AuthorizationCodeCredentials authorizationCodeCredentials1 = authorizationCodeRequest.execute();
			this.refreshToken = authorizationCodeCredentials1.getRefreshToken();
			this.configLoader.set(SPOTIFY_REFRSHTOKEN, refreshToken);
		} catch (SpotifyWebApiException | IOException e) {
			this.logger.error(e.getMessage());
		}
	}

	/**
	 * sets the ClientID from the spotify Web devloper account
	 * 
	 * @param clientID
	 */
	public void setClientID(String clientID) {
		this.configLoader.set(SPOTIFY_CLIENTID, clientID);
		this.clientID = clientID;
	}

	/**
	 * sets the ClientSecret from the spotify Web devloper account
	 * 
	 * @param clientSecret
	 */
	public void setClientSecret(String clientSecret) {
		this.configLoader.set(SPOTIFY_CLIENTSECRET, clientSecret);
		this.clientSecret = clientSecret;
	}

	public void setCurrentDevice(String deviceID) {
		this.deviceID = deviceID;
		this.configLoader.set(SPOTIFY_DEVICEID, deviceID);
	}

	/**
	 * in the first execution create this method a spotifyApi object and get these
	 * back, after the first execution the method checks the access token valid and
	 * refresh the access token if invalid
	 * 
	 * @return a spotifyAPI object for queries to the Spotify Web API
	 */
	public SpotifyApi getSpotifyApi() {
		if (this.firstTime) {
			try {
				if (this.clientID != null && this.clientSecret != null && this.refreshToken != null) {
					this.spotifyApi = new SpotifyApi.Builder().setClientId(this.clientID)
							.setClientSecret(this.clientSecret).setRefreshToken(this.refreshToken).build();
					this.authorizationCodeRefreshRequest = this.spotifyApi.authorizationCodeRefresh().build();
					this.authorizationCodeCredentials = this.authorizationCodeRefreshRequest.execute();
					this.spotifyApi.setAccessToken(this.authorizationCodeCredentials.getAccessToken());
				} else {
					return null;
				}
			} catch (SpotifyWebApiException | IOException e) {
				this.logger.error(e.getCause().getMessage());
				return null;
			}
			this.firstTime = false;
			return this.spotifyApi;
		} else if (this.authorizationCodeCredentials.getExpiresIn().intValue() > TOKEN_EXPIRE_TIME) {
			return this.spotifyApi;
		} else {
			try {

				this.authorizationCodeCredentials = this.authorizationCodeRefreshRequest.execute();
				this.spotifyApi.setAccessToken(this.authorizationCodeCredentials.getAccessToken());

			} catch (SpotifyWebApiException | IOException e) {
				this.logger.error(e.getCause().getMessage());
				return null;
			}
			return this.spotifyApi;
		}
	}

	/**
	 * check all possible problems from the player
	 * 
	 * @return true if all checks passed else false
	 */
	public boolean checkPlayerState() {
		if (getSpotifyApi() == null) {
			this.logger.warn("Please initialize the spotify api with the client id and client secret");
			return false;
		}
		if (!checkDeviceIsLoggedIn(deviceID)) {
			this.logger.warn("Device has been disconnected or is not the active device");
			return false;
		}
		return true;
	}

	/**
	 * get all devices that are current logged in the spotify client
	 * 
	 * @return
	 */
	public Device[] getDevices() {
		Device[] devices;
		if (getSpotifyApi() != null) {
			GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = getSpotifyApi().getUsersAvailableDevices()
					.build();
			devices = (Device[]) exceptionHandlingWithResults(getUsersAvailableDevicesRequest);
			if (devices != null) {
				return devices;
			}
		}
		return new Device[0];
	}

	/**
	 * get the currently active device (this client is able to play music etc.)
	 * 
	 * @return the active device or null if no active device is found
	 */
	public Device getActiveDevice() {
		for (Device device : getDevices()) {
			if (device.getIs_active()) {
				return device;
			}
		}
		return null;
	}

	/**
	 * check if the given device logged in an account
	 * 
	 * @param deviceID
	 * @return
	 */
	public boolean checkDeviceIsLoggedIn(String deviceID) {
		for (Device device : getDevices()) {
			if (device.getId().equals(deviceID)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * this method play the Song from the uri on spotify
	 * 
	 * @param uri
	 * @return true if no problem occur else false
	 */
	protected boolean playSongFromUri(String uri) {
		if (checkPlayerState()) {
			StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = getSpotifyApi().startResumeUsersPlayback()
					.device_id(this.deviceID).uris(new JsonParser().parse("[\"" + uri + "\"]").getAsJsonArray())
					.build();
			return exceptionHandlingWihtBoolean(startResumeUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * play a list of tracks for example a playlists and albums
	 * 
	 * @param uri
	 * @return true if no problem occur else false
	 */
	protected boolean playListFromUri(String uri) {
		if (checkPlayerState()) {
			StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = getSpotifyApi().startResumeUsersPlayback()
					.context_uri(uri).device_id(this.deviceID).build();
			return exceptionHandlingWihtBoolean(startResumeUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * resume the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean resume() {
		if (checkPlayerState()) {
			StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = getSpotifyApi().startResumeUsersPlayback()
					.device_id(this.deviceID).build();
			return exceptionHandlingWihtBoolean(startResumeUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * pause the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean pause() {
		if (checkPlayerState()) {
			PauseUsersPlaybackRequest pauseUsersPlaybackRequest = getSpotifyApi().pauseUsersPlayback()
					.device_id(this.deviceID).build();
			return exceptionHandlingWihtBoolean(pauseUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * goes one song forward in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean skip() {
		if (checkPlayerState()) {
			SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = getSpotifyApi()
					.skipUsersPlaybackToNextTrack().device_id(this.deviceID).build();
			return exceptionHandlingWihtBoolean(skipUsersPlaybackToNextTrackRequest);
		}
		return false;
	}

	/**
	 * goes one song back in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean back() {
		if (checkPlayerState()) {
			SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = getSpotifyApi()
					.skipUsersPlaybackToPreviousTrack().device_id(this.deviceID).build();
			return exceptionHandlingWihtBoolean(skipUsersPlaybackToPreviousTrackRequest);
		}
		return false;
	}

	/**
	 * set the volume from the remote spotify player
	 * 
	 * @param volume
	 *            int between 0 and 100
	 */
	protected boolean setVolume(int volume) {
		if (checkPlayerState()) {
			SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = getSpotifyApi()
					.setVolumeForUsersPlayback(volume).device_id(this.deviceID).build();
			return exceptionHandlingWihtBoolean(setVolumeForUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * get the volume of the current selected spotify client in percent
	 * 
	 * @return int between 0-100 for the volume and -1 if the volume is unknown
	 */
	public int getVolume() {
		if (getActiveDevice() != null && getActiveDevice().getVolume_percent() != null) {
			return getActiveDevice().getVolume_percent().intValue();
		}
		return -1;
	}

	/**
	 * get the current song from the active spotify client
	 * 
	 * @return a CurrentlyPlayingContext object from the spotify library, null if a
	 *         problem occur
	 */
	public CurrentlyPlayingContext getCurrentSong() {
		if (checkPlayerState()) {
			GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest = getSpotifyApi()
					.getInformationAboutUsersCurrentPlayback().build();
			return (CurrentlyPlayingContext) exceptionHandlingWithResults(
					getInformationAboutUsersCurrentPlaybackRequest);
		}
		return null;
	}

	/**
	 * create a search query for spotify.
	 * 
	 * @param searchItem
	 * @param type
	 *            type of the search (artist, track, album, playlist)
	 * @param limit
	 *            how many entry the result maximal have
	 * @return a object of the type SearchResult from the spotify library, null if a
	 *         problem occur
	 */
	public SearchResult searchInSpotify(String searchItem, String type, int limit) {
		if (checkPlayerState()) {
			SearchItemRequest searchItemRequest = getSpotifyApi().searchItem(searchItem, type.toLowerCase())
					.limit(Integer.valueOf(limit)).offset(Integer.valueOf(0)).build();
			return (SearchResult) exceptionHandlingWithResults(searchItemRequest);
		}
		return null;
	}

	/**
	 * get a FeaturedPlaylists object with palylists with the amount of limit
	 * 
	 * @param limit
	 * @return a FeaturedPlaylists object from the Spotify library, null if a
	 *         problem occur
	 */
	public FeaturedPlaylists getFeaturedPlaylists(int limit) {
		if (checkPlayerState()) {
			GetListOfFeaturedPlaylistsRequest getListOfFeaturedPlaylistsRequest = getSpotifyApi()
					.getListOfFeaturedPlaylists().country(CountryCode.DE).limit(Integer.valueOf(limit))
					.offset(Integer.valueOf(0)).build();
			return (FeaturedPlaylists) exceptionHandlingWithResults(getListOfFeaturedPlaylistsRequest);
		}
		return null;
	}

	/**
	 * handle the exception from the request created by the .excute() method
	 * 
	 * @param request
	 * @return
	 */
	private Object exceptionHandlingWithResults(IRequest request) {
		try {
			return request.execute();
		} catch (SpotifyWebApiException | IOException e) {
			this.logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * handle the exception from the request created by the .excute() method
	 * 
	 * @param request
	 * @return if no exception is occurred then true, else false
	 */
	private boolean exceptionHandlingWihtBoolean(IRequest request) {
		try {
			request.execute();
			return true;
		} catch (SpotifyWebApiException | IOException e) {
			this.logger.error(e.getMessage());
			return false;
		}
	}

}
