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

import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Properties;

import org.slf4j.Logger;

import com.google.gson.JsonArray;
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
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.requests.IRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.browse.GetListOfFeaturedPlaylistsRequest;
import com.wrapper.spotify.requests.data.player.*;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.search.SearchItemRequest;

import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.plugin.api.IStorage;

/**
 * 
 * This class handle every call that goes to the spotify library
 * 
 * @author Lars Buttgereit
 */
@Service(SpotifyAPICalls.class)
public class SpotifyAPICalls {

	/**
	 * URI to redirect callbacks from the login screen
	 */
	private final URI redirectURI = SpotifyHttpManager.makeUri("http://localhost:8888");
	/**
	 * id from the current device
	 */
	/**
	 * Rules for the spotify user authentication e.g. access to the playercontrol
	 */
	private static final String SPOTIFY_RULES = "user-modify-playback-state,user-read-playback-state,"
			.concat("playlist-read-private");

	public static final String SPOTIFY_CLIENTSECRET_KEY = "spotify_clientSecret";
	public static final String SPOTIFY_CLIENTID_KEY = "spotify_clientId";
	public static final String SPOTIFY_REFRSHTOKEN_KEY = "spotify_refreshToken";
	public static final int TOKEN_EXPIRE_TIME_OFFSET = 120;
	public static final String SPOTIFY_ERROR_TAG = "Spotify Exception:";

	private String clientSecret;
	private String clientId;

	private User userData;

	@Reference
	private Properties configLoader;

	@Reference
	private Logger logger;

	@Reference
	private IStorage storage;

	@Reference
	private Environment environment;

	/**
	 * init the api calls
	 */
	@PostConstruct
	protected final void init() {
		this.userData = new User();
		this.clientSecret = this.configLoader.getProperty(SPOTIFY_CLIENTSECRET_KEY, null);
		this.clientId = this.configLoader.getProperty(SPOTIFY_CLIENTID_KEY, null);
		if (this.storage.has(SPOTIFY_REFRSHTOKEN_KEY)) {
			this.userData.setRefreshToken(this.storage.get(SPOTIFY_REFRSHTOKEN_KEY));
			if (getDevices().length > 0) {
				this.userData.setCurrentDeviceId(getDevices()[0].getId());
			} else {
				this.logger.warn("no device is logged in");
			}
		}

	}

	/**
	 * this method a spotifyApi object and get these back, the method checks if all tokens correct
	 * 
	 * @return a spotifyAPI object for queries to the Spotify Web API
	 */
	public SpotifyApi getSpotifyApi() {
		SpotifyApi spotifyApi = getSpotifyApiWithoutAcToken();
		if (spotifyApi != null && spotifyApi.getRefreshToken() != null) {
			if (this.userData.getAccessToken() != null && this.userData.getAccessTokenExpireTime() != Long.MIN_VALUE
					&& this.userData.getAccessTokenExpireTime() > this.environment.getCurrentDateTime()
							.toEpochSecond()) {
				spotifyApi.setAccessToken(this.userData.getAccessToken());
			} else {
				String accessToken = createAccessToken(spotifyApi);
				if (accessToken != null) {
					spotifyApi.setAccessToken(accessToken);
				} else {
					this.logger.warn("Accsess Token can not generated");
					return spotifyApi;
				}
			}
		}
		return spotifyApi;
	}

	private SpotifyApi getSpotifyApiWithoutAcToken() {
		SpotifyApi spotifyApi = null;
		if (this.clientSecret != null && this.clientId != null) {
			spotifyApi = new SpotifyApi.Builder().setClientId(this.clientId).setClientSecret(this.clientSecret)
					.setRedirectUri(this.redirectURI).build();
		} else {
			this.logger.warn("Client Secret and ID missing. Please insert the config file");
			return null;
		}
		if (this.userData.getRefreshToken() != null) {
			spotifyApi.setRefreshToken(this.userData.getRefreshToken());
		} else {
			this.logger.warn("Please exec the Authorization first");
			return spotifyApi;
		}
		return spotifyApi;
	}

	/**
	 * create a refresh Token and start a timer to refresh it
	 * 
	 * @param spotifyApi
	 *            a instance of SpotifyApi
	 * @return a String that is a access token
	 */
	private String createAccessToken(SpotifyApi spotifyApi) {
		AuthorizationCodeRefreshRequest authCodeRefreshReq = spotifyApi.authorizationCodeRefresh().build();
		AuthorizationCodeCredentials authCredentials = exceptionHandlingWithResults(authCodeRefreshReq::execute);
		if (authCredentials != null) {
			ZonedDateTime time = this.environment.getCurrentDateTime()
					.plusSeconds(authCredentials.getExpiresIn().longValue() - TOKEN_EXPIRE_TIME_OFFSET);
			this.userData.setAccessTokenExpireTime(time.toEpochSecond());
			this.userData.setAccessToken(authCredentials.getAccessToken());
			return authCredentials.getAccessToken();
		}
		return null;
	}

	/**
	 * create a authentication link for the User to authenticate his spotify account. The Link create a authentication
	 * code for the next step
	 * 
	 * @return a URI to authorize a spotify account
	 */
	public URI authorizationCodeUri() {
		AuthorizationCodeUriRequest authorizationCodeUriRequest = getSpotifyApi().authorizationCodeUri().state("TEST")
				.scope(SPOTIFY_RULES).show_dialog(true).build();
		return authorizationCodeUriRequest.execute();
	}

	/**
	 * create a persistent refresh token with the authentication Code
	 * 
	 * @param authCode
	 *            from authorizationCodeUri()
	 * @return true if succeed else false
	 */
	public boolean createRefreshToken(String authCode) {
		AuthorizationCodeRequest authorizationCodeRequest = getSpotifyApi().authorizationCode(authCode).build();
		AuthorizationCodeCredentials authCodeCredentials = exceptionHandlingWithResults(
				authorizationCodeRequest::execute);
		if (authCodeCredentials != null) {
			this.userData = new User();
			this.storage.put(SPOTIFY_REFRSHTOKEN_KEY, authCodeCredentials.getRefreshToken());
			this.userData.setRefreshToken(authCodeCredentials.getRefreshToken());
			if (getDevices().length > 0) {
				this.userData.setCurrentDeviceId(getDevices()[0].getId());
			}
			return true;
		}
		return false;
	}

	/**
	 * sets the ClientID from the spotify Web developer account
	 * 
	 * @param clientID
	 *            from spotify developer account
	 */
	public void setClientID(String clientID) {
		this.clientId = clientID;
	}

	/**
	 * sets the ClientSecret from the spotify Web developer account
	 * 
	 * @param clientSecret
	 *            from the spotify developer account
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * set the current device to the given deviceID
	 * 
	 * @param deviceID
	 *            deviceID from actual active device
	 * @return true if succeed else false
	 */
	public boolean setCurrentDevice(String deviceID) {
		JsonArray deviceIds = new JsonParser().parse("[\"".concat(deviceID).concat("\"]")).getAsJsonArray();
		this.userData.setCurrentDeviceId(deviceID);
		TransferUsersPlaybackRequest transferUsersPlaybackRequest = getSpotifyApi().transferUsersPlayback(deviceIds)
				.build();
		return exceptionHandlingWithBoolean(transferUsersPlaybackRequest);
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
		if (!checkDeviceIsLoggedIn(this.userData.getCurrentDeviceId())) {
			this.logger.warn("Device has been disconnected or is not the active device");
			return false;
		}
		return true;
	}

	/**
	 * get all devices that are current logged in the spotify client
	 * 
	 * @return a array of devices
	 */
	public Device[] getDevices() {
		Device[] devices;
		if (getSpotifyApi() != null && this.userData.getRefreshToken() != null) {
			GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = getSpotifyApi().getUsersAvailableDevices()
					.build();
			devices = exceptionHandlingWithResults(getUsersAvailableDevicesRequest::execute);
			if (devices != null)
				return devices;
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
			if (device.getIs_active().booleanValue())
				return device;
		}
		return null;
	}

	/**
	 * check if the given device logged in an account
	 * 
	 * @param deviceId
	 *            device to check
	 * @return if device logged in true, else false
	 */
	public boolean checkDeviceIsLoggedIn(String deviceId) {
		for (Device device : getDevices()) {
			if (device.getId().equals(deviceId))
				return true;
		}
		return false;
	}

	/**
	 * this method play the Song from the uri on spotify
	 * 
	 * @param uri
	 *            from a song
	 * @return true if no problem occur else false
	 */
	public boolean playSongFromUri(String uri) {
		if (checkPlayerState()) {
			StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = getSpotifyApi().startResumeUsersPlayback()
					.device_id(this.userData.getCurrentDeviceId())
					.uris(new JsonParser().parse("[\"" + uri + "\"]").getAsJsonArray()).build();
			return exceptionHandlingWithBoolean(startResumeUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * play a list of tracks for example a playlists and albums
	 * 
	 * @param uri
	 *            uro from a playlist or album
	 * @return true if no problem occur else false
	 */
	public boolean playListFromUri(String uri) {
		if (checkPlayerState()) {
			StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = getSpotifyApi().startResumeUsersPlayback()
					.context_uri(uri).device_id(this.userData.getCurrentDeviceId()).build();
			return exceptionHandlingWithBoolean(startResumeUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * resume the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean resume() {
		if (checkPlayerState() && !getIsPlaying()) {
			StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = getSpotifyApi().startResumeUsersPlayback()
					.device_id(this.userData.getCurrentDeviceId()).build();
			return exceptionHandlingWithBoolean(startResumeUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * pause the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean pause() {
		if (checkPlayerState() && getIsPlaying()) {
			PauseUsersPlaybackRequest pauseUsersPlaybackRequest = getSpotifyApi().pauseUsersPlayback()
					.device_id(this.userData.getCurrentDeviceId()).build();
			return exceptionHandlingWithBoolean(pauseUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * goes one song forward in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean skip() {
		if (checkPlayerState()) {
			SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = getSpotifyApi()
					.skipUsersPlaybackToNextTrack().device_id(this.userData.getCurrentDeviceId()).build();
			return exceptionHandlingWithBoolean(skipUsersPlaybackToNextTrackRequest);
		}
		return false;
	}

	/**
	 * goes one song back in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean back() {
		if (checkPlayerState()) {
			SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = getSpotifyApi()
					.skipUsersPlaybackToPreviousTrack().device_id(this.userData.getCurrentDeviceId()).build();
			return exceptionHandlingWithBoolean(skipUsersPlaybackToPreviousTrackRequest);
		}
		return false;
	}

	/**
	 * set the volume from the remote spotify player
	 * 
	 * @param volume
	 *            int between 0 and 100
	 * @return if setVolume success then return true, else false
	 */
	public boolean setVolume(int volume) {
		if (checkPlayerState()) {
			SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = getSpotifyApi()
					.setVolumeForUsersPlayback(volume).device_id(this.userData.getCurrentDeviceId()).build();
			return exceptionHandlingWithBoolean(setVolumeForUsersPlaybackRequest);
		}
		return false;
	}

	/**
	 * get the volume of the current selected spotify client in percent
	 * 
	 * @return int between 0-100 for the volume and -1 if the volume is unknown
	 */
	public int getVolume() {
		if (getSpotifyApi() != null && getActiveDevice() != null && getActiveDevice().getVolume_percent() != null) {
			return getActiveDevice().getVolume_percent().intValue();
		}
		return -1;
	}

	/**
	 * get the current song from the active spotify client
	 * 
	 * @return a CurrentlyPlayingContext object from the spotify library, null if a problem occurs
	 */
	public CurrentlyPlayingContext getCurrentPlayingContext() {
		if (checkPlayerState()) {
			GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest = getSpotifyApi()
					.getInformationAboutUsersCurrentPlayback().build();
			return exceptionHandlingWithResults(getInformationAboutUsersCurrentPlaybackRequest::execute);
		}
		return null;
	}

	/**
	 * get the current playback state
	 * 
	 * @return true if currently playing, false otherwise
	 */
	public boolean getIsPlaying() {
		CurrentlyPlayingContext context = getCurrentPlayingContext();
		if (context != null) {
			return getCurrentPlayingContext().getIs_playing();
		}
		return false;
	}

	/**
	 * create a search query for spotify.
	 * 
	 * @param searchItem
	 *            search text for the search
	 * @param type
	 *            type of the search (artist, track, album, playlist)
	 * @param limit
	 *            how many entry the result maximal have
	 * @return a object of the type SearchResult from the spotify library, null if a problem occur
	 */
	public SearchResult searchInSpotify(String searchItem, String type, int limit) {
		if (checkPlayerState()) {
			SearchItemRequest searchItemRequest = getSpotifyApi().searchItem(searchItem, type.toLowerCase())
					.limit(Integer.valueOf(limit)).offset(Integer.valueOf(0)).build();
			return exceptionHandlingWithResults(searchItemRequest::execute);
		}
		return null;
	}

	/**
	 * get a FeaturedPlaylists object with palylists with the amount of limit
	 * 
	 * @param limit
	 *            maximum of search results
	 * @return a FeaturedPlaylists object from the Spotify library, null if a problem occur
	 */
	public FeaturedPlaylists getFeaturedPlaylists(int limit) {
		if (checkPlayerState()) {
			GetListOfFeaturedPlaylistsRequest getListOfFeaturedPlaylistsRequest = getSpotifyApi()
					.getListOfFeaturedPlaylists().country(CountryCode.DE).limit(Integer.valueOf(limit))
					.offset(Integer.valueOf(0)).build();

			return exceptionHandlingWithResults(getListOfFeaturedPlaylistsRequest::execute);
		}
		return null;
	}

	/**
	 * Functional interface for Spotify API calls SonarLint detects some warnings that cannot be fixed, therefore
	 * suppressed
	 * 
	 * @param <T>
	 *            the return type of the API call
	 */
	@FunctionalInterface
	public interface SpotifyCallLambda<T> {
		T execute() throws SpotifyWebApiException, IOException;
	}

	/**
	 * get a paging with playlists that the user follow or created
	 * 
	 * @param limit
	 *            of returned playlists
	 * @return a paging with playlists
	 */
	public Paging<PlaylistSimplified> getOwnPlaylists(int limit) {
		if (checkPlayerState()) {
			GetListOfCurrentUsersPlaylistsRequest getListOfCurrentUsersPlaylistsRequest = getSpotifyApi()
					.getListOfCurrentUsersPlaylists().limit(Integer.valueOf(limit)).offset(Integer.valueOf(0)).build();
			try {
				return getListOfCurrentUsersPlaylistsRequest.execute();
			} catch (SpotifyWebApiException | IOException e) {
				this.logger.warn(SPOTIFY_ERROR_TAG, e);
			}
		}
		return null;
	}

	/**
	 * handle the exception from the request created by the .execute() method
	 * 
	 * @param f
	 *            lambda calling spotify api request
	 * @return if no exception is occurred then true, else false
	 */
	private <T> T exceptionHandlingWithResults(SpotifyCallLambda<T> f) {
		try {
			return f.execute();
		} catch (SpotifyWebApiException | IOException e) {
			this.logger.warn(SPOTIFY_ERROR_TAG, e);
			return null;
		}
	}

	/**
	 * handle the exception from the request created by the .execute() method
	 * 
	 * @param request
	 *            spotify api request
	 * @return if no exception is occurred then true, else false
	 */
	private boolean exceptionHandlingWithBoolean(IRequest request) {
		try {
			request.execute();
			return true;
		} catch (SpotifyWebApiException | IOException e) {
			this.logger.warn(SPOTIFY_ERROR_TAG, e);
			return false;
		}
	}

}
