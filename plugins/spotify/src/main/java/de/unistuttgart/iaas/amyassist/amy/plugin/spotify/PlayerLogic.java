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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * This class have methods to control a spotify client from a user. For examlpe
 * play, pause playback or search for music tracks etc.
 * 
 * @author Lars Buttgereit
 */
@Service(PlayerLogic.class)
public class PlayerLogic {
	private SpotifyAPICalls spotifyAPICalls;
	private Search search;
	private List<Map<String, String>> actualSearchResult = null;
	private StringGenerator stringGenerator = new StringGenerator();
	private Logger logger;

	private ArrayList<String> deviceNames = new ArrayList<>();
	private ArrayList<String> deviceIDs = new ArrayList<>();

	private static final int VOLUME_MUTE_VALUE = 0;
	private static final int VOLUME_MAX_VALUE = 100;
	private static final int VOLUME_UPDOWN_VALUE = 10;

	public PlayerLogic() {
		init();
	}

	public void init() {
		logger = LoggerFactory.getLogger(PlayerLogic.class);
		this.spotifyAPICalls = new SpotifyAPICalls();
		this.search = new Search(this.spotifyAPICalls);
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
		this.spotifyAPICalls.setClientID(clientID);
		this.spotifyAPICalls.setClientSecret(clientSecret);
		return this.spotifyAPICalls.authorizationCodeUri();
	}

	/**
	 * create the refresh token in he authorization object with the authCode
	 * 
	 * @param authCode
	 *            Callback from the login link
	 */
	public void inputAuthCode(String authCode) {
		this.spotifyAPICalls.createRefreshToken(authCode);
	}

	/**
	 * get all devices that logged in at the moment
	 * 
	 * @return empty ArrayList if no device available else the name of the devices
	 */
	public List<String> getDevices() {

		deviceNames = new ArrayList<>();
		deviceIDs = new ArrayList<>();
		Device[] devices = spotifyAPICalls.getDevices();
		if (devices != null) {
			for (Device device : devices) {
				deviceNames.add(device.getName());
				deviceIDs.add(device.getId());
			}
		}
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
		getDevices();
		if (this.deviceIDs.size() > deviceNumber && this.deviceNames.size() > deviceNumber) {
			spotifyAPICalls.setCurrentDevice(deviceIDs.get(deviceNumber));
			return deviceNames.get(deviceNumber);
		}
		logger.warn("No device with this number was found");
		return "No device found";
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
		this.actualSearchResult = this.search.searchList(searchText, type, limit);
		return this.actualSearchResult;
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
		List<Map<String, String>> playLists;
		playLists = this.search.getFeaturedPlaylists();
		if (!playLists.isEmpty() && 1 < playLists.size()
				&& spotifyAPICalls.playListFromUri(playLists.get(1).get(SpotifyConstants.ITEM_URI))) {
			return playLists.get(1);
		}
		this.logger.warn("no featured playlist found");
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
				if (spotifyAPICalls
						.playSongFromUri(this.actualSearchResult.get(songNumber).get(SpotifyConstants.ITEM_URI))) {
					return this.actualSearchResult.get(songNumber);
				}
			} else if (spotifyAPICalls
					.playListFromUri(this.actualSearchResult.get(songNumber).get(SpotifyConstants.ITEM_URI))) {
				return this.actualSearchResult.get(songNumber);
			}
		}
		return new HashMap<>();
	}

	/**
	 * resume the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean resume() {
		return spotifyAPICalls.resume();
	}

	/**
	 * pause the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean pause() {
		return spotifyAPICalls.pause();
	}

	/**
	 * goes one song forward in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean skip() {
		return spotifyAPICalls.skip();
	}

	/**
	 * goes one song back in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command
	 *         failed
	 */
	public boolean back() {
		return spotifyAPICalls.back();
	}

	/**
	 * gives the actual played song in the spotify client back
	 * 
	 * @return a hashMap with the keys name and artist
	 */
	public Map<String, String> getCurrentSong() {
		CurrentlyPlayingContext currentlyPlayingContext = spotifyAPICalls.getCurrentSong();
		if (currentlyPlayingContext != null) {
			Track[] track = { currentlyPlayingContext.getItem() };
			return search
					.createTrackOutput(new Paging.Builder<Track>().setItems(track).build(), SpotifyConstants.TYPE_TRACK)
					.get(0);
		}
		return new HashMap<>();

	}

	/**
	 * this method controls the volume of the player
	 * 
	 * @param volumeString
	 *            allowed strings: mute, max, up, down
	 * @return a int from 0-100. This represent the Volume in percent. if the volume
	 *         is unknown the return value is -1
	 */
	public int setVolume(String volumeString) {
		int volume = spotifyAPICalls.getVolume();
		if (volume != -1) {
			switch (volumeString) {
			case "mute":
				spotifyAPICalls.setVolume(VOLUME_MUTE_VALUE);
				return VOLUME_MUTE_VALUE;
			case "max":
				spotifyAPICalls.setVolume(VOLUME_MAX_VALUE);
				return VOLUME_MAX_VALUE;
			case "up":
				if (volume + VOLUME_UPDOWN_VALUE <= VOLUME_MAX_VALUE) {
					spotifyAPICalls.setVolume(volume + VOLUME_UPDOWN_VALUE);
					return volume + VOLUME_UPDOWN_VALUE;
				}
				return -1;
			case "down":
				if (volume - VOLUME_UPDOWN_VALUE >= VOLUME_MUTE_VALUE) {
					spotifyAPICalls.setVolume(volume - VOLUME_UPDOWN_VALUE);
					return volume - VOLUME_UPDOWN_VALUE;
				}
				return -1;
			default:
				this.logger.warn("Incorrect volume command");
				return -1;
			}
		}
		return volume;
	}

	public int setVolume(int volume) {
		if (volume >= VOLUME_MUTE_VALUE && volume <= VOLUME_MAX_VALUE) {
			spotifyAPICalls.setVolume(volume);
			return volume;
		}
		return -1;
	}
}
