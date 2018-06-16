/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.GetUsersAvailableDevicesRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SetVolumeForUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToNextTrackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToPreviousTrackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * This class have methods to control a spotify client from a user. For examlpe
 * play, pause playback or search for music tracks etc.
 * 
 * @author Lars Buttgereit
 */
@Service(PlayerLogic.class)
public class PlayerLogic {
	private Authorization auth;
	private String deviceID = null;
	private Search search;
	private int volume = 50;
	private String deviceName = null;
	private List<Map<String, String>> actualSearchResult = null;
	private StringGenerator stringGenerator = new StringGenerator();
	private Logger logger;

	public PlayerLogic() {
		init();
	}

	public void init() {
		logger = LoggerFactory.getLogger(PlayerLogic.class);
		this.auth = new Authorization();
		this.auth.init();
		setDevice(0);
		this.search = new Search(this.auth);
	}

	/**
	 * needed for the first init. need the clientID and the clientSecret form a
	 * spotify devloper account
	 * 
	 * @param clientID
	 * @param clientSecret
	 * @return login link to a personal spotify account
	 */
	public URI firstTimeInit(String clientID, String clientSecret) {
		this.auth.setClientID(clientID);
		this.auth.setClientSecret(clientSecret);
		return this.auth.authorizationCodeUri();
	}

	/**
	 * create the refresh token in he authorization object with the authCode
	 * 
	 * @param authCode
	 *            Callback from the login link
	 */
	public void inputAuthCode(String authCode) {
		this.auth.createRefreshToken(authCode);
		this.auth.init();
	}

	/**
	 * get all devices that logged in at the moment
	 * 
	 * @return empty ArrayList if no device available else the name of the devices
	 */
	public List<String> getDevices() {

		ArrayList<String> deviceNames = new ArrayList<>();
		if (this.auth.getSpotifyApi() != null) {
			GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = this.auth.getSpotifyApi()
					.getUsersAvailableDevices().build();

			try {
				Device[] devices = getUsersAvailableDevicesRequest.execute();
				for (Device device : devices) {
					deviceNames.add(device.getName());
				}
			} catch (SpotifyWebApiException | IOException e) {
				this.logger.error(e.getMessage());
			}

			return deviceNames;
		}
		this.logger.warn("please init the authorization object");
		return deviceNames;
	}

	/**
	 * set the given device as acutal active device for playing music
	 * 
	 * @param deviceNumber
	 *            index of the device array. Order is the same as in the output in
	 *            getDevices
	 * @return selected device
	 */
	public String setDevice(int deviceNumber) {
		if (this.auth.getSpotifyApi() != null) {
			GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = this.auth.getSpotifyApi()
					.getUsersAvailableDevices().build();
			try {
				Device[] devices = getUsersAvailableDevicesRequest.execute();
				if (deviceNumber < devices.length) {
					this.deviceID = devices[deviceNumber].getId();
					this.deviceName = devices[deviceNumber].getName();
					return devices[deviceNumber].getName();
				}
				return "This device was not found";
			} catch (SpotifyWebApiException | IOException e) {
				logger.error(e.getMessage());
				return "A problem has occurred";
			}

		}
		return "Please init the spotify API";
	}

	/**
	 * this call the searchAnaything method in the Search class
	 * 
	 * @param searchText
	 * @param type
	 *            artist, track, playlist, album
	 * @param limit
	 *            how many results maximal searched for
	 * @return one output String with all results
	 */
	public List<Map<String, String>> search(String searchText, String type, int limit) {
		if (checkPlayerState()) {
			try {
				this.actualSearchResult = this.search.searchList(searchText, type, limit);
			} catch (SpotifyWebApiException | IOException e) {
				this.logger.error(e.getMessage());
			}
			return this.actualSearchResult;
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * generate one String out of the search result map or other maps with track,
	 * album or playlist attributes. Useful for example for console or speech output
	 * 
	 * @param input
	 *            the map from a search or a map with attributes from track, album,
	 *            playlist
	 * @return a single String with useful information for the user
	 */
	public String convertSearchOutputToSingleString(Map<String, String> input) {
		return stringGenerator.generateSearchOutputString(input);
	}

	/**
	 * generate one String out of the search result list. Useful for example for
	 * console or speech output
	 * 
	 * @param input
	 *            the list from a search or a list with maps with attributes from
	 *            track, album, playlist
	 * @return a single String with useful information for the user from all
	 *         elements from the list
	 */
	public String convertSearchOutputToSingleString(List<Map<String, String>> input) {
		return stringGenerator.generateSearchOutputString(input);
	}

	/**
	 * this play method play a featured playlist from spotify
	 * 
	 * @return a string with the playlist name
	 */
	public Map<String, String> play() {
		if (checkPlayerState()) {
			List<Map<String, String>> playLists;
			try {
				playLists = this.search.getFeaturedPlaylists();
				if (!playLists.isEmpty() && 1 < playLists.size()) {
					playListFromUri(playLists.get(1).get(SpotifyConstants.ITEM_URI));
					return playLists.get(1);
				}
			} catch (SpotifyWebApiException | IOException e) {
				this.logger.error(e.getMessage());
			}

			this.logger.warn("no featured playlist found");

		}
		return new HashMap<>();
	}

	/**
	 * this method play the item that searched before. Use only after a search
	 * 
	 * @param songNumber
	 *            number of the item form the search before
	 * @return
	 */
	public Map<String, String> play(int songNumber) {
		if (this.actualSearchResult != null && songNumber < this.actualSearchResult.size()) {
			if (this.actualSearchResult.get(songNumber).get(SpotifyConstants.ITEM_TYPE)
					.equals(SpotifyConstants.TYPE_TRACK)) {
				playSongFromUri(this.actualSearchResult.get(songNumber).get(SpotifyConstants.ITEM_URI));
				return this.actualSearchResult.get(songNumber);
			} else {
				playListFromUri(this.actualSearchResult.get(songNumber).get(SpotifyConstants.ITEM_URI));
				return this.actualSearchResult.get(songNumber);
			}
		} else {
			return new HashMap<>();
		}
	}

	/**
	 * resume the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean resume() {
		if (checkPlayerState()) {
			StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.auth.getSpotifyApi()
					.startResumeUsersPlayback().device_id(this.deviceID).build();
			try {
				startResumeUsersPlaybackRequest.execute();
				return true;
			} catch (SpotifyWebApiException | IOException e) {
				this.logger.error(e.getMessage());
				return false;
			}
		}
		return false;

	}

	/**
	 * pause the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean pausePlayback() {
		if (checkPlayerState()) {
			PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.auth.getSpotifyApi().pauseUsersPlayback()
					.device_id(this.deviceID).build();
			try {
				pauseUsersPlaybackRequest.execute();
				return true;
			} catch (SpotifyWebApiException | IOException e) {
				this.logger.error(e.getMessage());
				return false;
			}
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
			SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = this.auth.getSpotifyApi()
					.skipUsersPlaybackToNextTrack().device_id(this.deviceID).build();
			try {
				skipUsersPlaybackToNextTrackRequest.execute();
				return true;
			} catch (IOException | SpotifyWebApiException e) {
				this.logger.error(e.getMessage());
				return false;
			}
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

			SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = this.auth.getSpotifyApi()
					.skipUsersPlaybackToPreviousTrack().device_id(this.deviceID).build();
			try {
				skipUsersPlaybackToPreviousTrackRequest.execute();
				return true;
			} catch (IOException | SpotifyWebApiException e) {
				this.logger.error(e.getMessage());
				return false;
			}
		}
		return false;
	}

	/**
	 * gives the actual played song in the spotify client back
	 * 
	 * @return a hashMap with the keys name and artist
	 */
	public Map<String, String> getCurrentSong() {
		HashMap<String, String> result = new HashMap<>();
		if (checkPlayerState()) {
			GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest = this.auth
					.getSpotifyApi().getInformationAboutUsersCurrentPlayback().build();
			try {
				CurrentlyPlayingContext currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest
						.execute();
				result.put("name", currentlyPlayingContext.getItem().getName());
				String artists = "";
				for (ArtistSimplified artist : currentlyPlayingContext.getItem().getArtists()) {
					artists = artists.concat(artist.getName()).concat(" ");

				}
				result.put("artist", artists);
				return result;
			} catch (SpotifyWebApiException | IOException e) {
				this.logger.error(e.getMessage());
				return new HashMap<>();
			}
		}
		return new HashMap<>();
	}

	/**
	 * this method controls the volume of the player
	 * 
	 * @param volumeString
	 *            allowed strings: mute, max, up, down
	 * @return a int from 0-100. This represent the Volume in percent. if the
	 *         Playerstate incorrect return -1
	 */
	public int setVolume(String volumeString) {
		if (checkPlayerState()) {
			switch (volumeString) {
			case "mute":
				setVolume(0);
				this.volume = 0;
				break;
			case "max":
				setVolume(100);
				break;
			case "up":
				if (this.volume + 10 <= 100) {
					this.volume += 10;
					setVolume(this.volume);

				}
				break;
			case "down":
				if (this.volume - 10 >= 0) {
					this.volume -= 10;
					setVolume(this.volume);

				}
				break;
			default:
				this.logger.warn("Incorrect volume command");
				break;
			}
			return this.volume;
		}
		return -1;
	}

	/**
	 * set the volume from the remote spotify player
	 * 
	 * @param volume
	 */
	protected void setVolume(int volume) {
		this.volume = volume;
		SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = this.auth.getSpotifyApi()
				.setVolumeForUsersPlayback(volume).device_id(this.deviceID).build();

		try {
			setVolumeForUsersPlaybackRequest.execute();
		} catch (SpotifyWebApiException | IOException e) {
			this.logger.error(e.getMessage());
		}

	}

	/**
	 * this method play the Song from the uri on spotify
	 * 
	 * @param uri
	 * @return true if no problem occur else false
	 */
	private boolean playSongFromUri(String uri) {
		StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.auth.getSpotifyApi()
				.startResumeUsersPlayback().device_id(this.deviceID)
				.uris(new JsonParser().parse("[\"" + uri + "\"]").getAsJsonArray()).build();
		try {
			startResumeUsersPlaybackRequest.execute();
			return true;
		} catch (SpotifyWebApiException | IOException e) {
			this.logger.error(e.getCause().getMessage());
			return false;
		}
	}

	/**
	 * play a list of tracks for example a playlists and albums
	 * 
	 * @param uri
	 * @return true if no problem occur else false
	 */
	private boolean playListFromUri(String uri) {
		StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.auth.getSpotifyApi()
				.startResumeUsersPlayback().context_uri(uri).device_id(this.deviceID).build();
		try {
			startResumeUsersPlaybackRequest.execute();
			return true;
		} catch (SpotifyWebApiException | IOException e) {
			this.logger.error(e.getCause().getMessage());
			return false;
		}
	}

	/**
	 * check all possible problems from the player
	 * 
	 * @return true if all checks passed else false
	 */
	private boolean checkPlayerState() {
		if (this.auth == null) {
			this.logger.warn("Please initialize the Authorization.");
			return false;
		} else if (this.auth.getSpotifyApi() == null) {
			this.logger.warn("Please initialize the spotify api with the client id and client secret");
			return false;
		} else if (!getDevices().contains(this.deviceName)) {
			this.logger.warn("the current device has been disconnected. Please select a new device.");
			return false;
		}
		return true;
	}

}
