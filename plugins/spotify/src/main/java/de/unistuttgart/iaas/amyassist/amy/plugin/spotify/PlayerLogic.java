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

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * This class have methods to control a spotify client from a user. For examlpe play, pause playback or search for music
 * tracks etc.
 * 
 * @author Lars Buttgereit
 */
@Service(PlayerLogic.class)
public class PlayerLogic {
	@Reference
	private SpotifyAPICalls spotifyAPICalls;
	@Reference
	private Search search;
	@Reference
	private StringGenerator stringGenerator;
	@Reference
	private Logger logger;
	
	private List<Map<String, String>> actualSearchResult = null;

	private ArrayList<String> deviceNames = new ArrayList<>();
	private ArrayList<String> deviceIDs = new ArrayList<>();

	private static final int VOLUME_MUTE_VALUE = 0;
	private static final int VOLUME_MAX_VALUE = 100;
	private static final int VOLUME_UPDOWN_VALUE = 10;



	/**
	 * needed for the first init. need the clientID and the clientSecret form a spotify devloper account
	 * 
	 * @param clientID
	 *            from spotify developer account
	 * @param clientSecret
	 *            spotify developer account
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

		this.deviceNames = new ArrayList<>();
		this.deviceIDs = new ArrayList<>();
		Device[] devices = this.spotifyAPICalls.getDevices();
		if (devices != null) {
			for (Device device : devices) {
				this.deviceNames.add(device.getName());
				this.deviceIDs.add(device.getId());
			}
		}
		return this.deviceNames;
	}

	/**
	 * set the given device as acutal active device for playing music
	 * 
	 * @param deviceNumber
	 *            index of the device array. Order is the same as in the output in getDevices
	 * @return selected device
	 */
	public String setDevice(int deviceNumber) {
		getDevices();
		if (this.deviceIDs.size() > deviceNumber && this.deviceNames.size() > deviceNumber) {
			this.spotifyAPICalls.setCurrentDevice(this.deviceIDs.get(deviceNumber));
			return this.deviceNames.get(deviceNumber);
		}
		this.logger.warn("No device with this number was found");
		return "No device found";
	}

	/**
	 * this call the searchAnaything method in the Search class
	 * 
	 * @param searchText
	 *            the text you want ot search
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
	 * generate one String out of the search result map or other maps with track, album or playlist attributes. Useful
	 * for example for console or speech output
	 * 
	 * @param input
	 *            the map from a search or a map with attributes from track, album, playlist
	 * @return a single String with useful information for the user
	 */
	public String convertSearchOutputToSingleString(Map<String, String> input) {
		return this.stringGenerator.generateSearchOutputString(input);
	}

	/**
	 * generate one String out of the search result list. Useful for example for console or speech output
	 * 
	 * @param input
	 *            the list from a search or a list with maps with attributes from track, album, playlist
	 * @return a single String with useful information for the user from all elements from the list
	 */
	public String convertSearchOutputToSingleString(List<Map<String, String>> input) {
		return this.stringGenerator.generateSearchOutputString(input);
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
				&& this.spotifyAPICalls.playListFromUri(playLists.get(1).get(SpotifyConstants.ITEM_URI))) {
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
	 * @return a map with the song data
	 */
	public Map<String, String> play(int songNumber) {
		if (this.actualSearchResult != null && songNumber < this.actualSearchResult.size()) {
			if (this.actualSearchResult.get(songNumber).get(SpotifyConstants.ITEM_TYPE)
					.equals(SpotifyConstants.TYPE_TRACK)) {
				if (this.spotifyAPICalls
						.playSongFromUri(this.actualSearchResult.get(songNumber).get(SpotifyConstants.ITEM_URI))) {
					return this.actualSearchResult.get(songNumber);
				}
			} else if (this.spotifyAPICalls
					.playListFromUri(this.actualSearchResult.get(songNumber).get(SpotifyConstants.ITEM_URI))) {
				return this.actualSearchResult.get(songNumber);
			}
		}
		return new HashMap<>();
	}

	/**
	 * resume the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean resume() {
		return this.spotifyAPICalls.resume();
	}

	/**
	 * pause the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean pause() {
		return this.spotifyAPICalls.pause();
	}

	/**
	 * goes one song forward in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean skip() {
		return this.spotifyAPICalls.skip();
	}

	/**
	 * goes one song back in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean back() {
		return this.spotifyAPICalls.back();
	}

	/**
	 * gives the actual played song in the spotify client back
	 * 
	 * @return a hashMap with the keys name and artist
	 */
	public Map<String, String> getCurrentSong() {
		CurrentlyPlayingContext currentlyPlayingContext = this.spotifyAPICalls.getCurrentSong();
		if (currentlyPlayingContext != null) {
			Track[] track = { currentlyPlayingContext.getItem() };
			return this.search.createTrackOutput(new Paging.Builder<Track>().setItems(track).build()).get(0);
		}
		return new HashMap<>();

	}

	/**
	 * this method controls the volume of the player
	 * 
	 * @param volumeString
	 *            allowed strings: mute, max, up, down
	 * @return a int from 0-100. This represent the Volume in percent. if the volume is unknown the return value is -1
	 */
	public int setVolume(String volumeString) {
		int volume = this.spotifyAPICalls.getVolume();
		if (volume != -1) {
			switch (volumeString) {
			case "mute":
				this.spotifyAPICalls.setVolume(VOLUME_MUTE_VALUE);
				return VOLUME_MUTE_VALUE;
			case "max":
				this.spotifyAPICalls.setVolume(VOLUME_MAX_VALUE);
				return VOLUME_MAX_VALUE;
			case "up":
				volume = Math.min(VOLUME_MAX_VALUE, volume + VOLUME_UPDOWN_VALUE);
				this.spotifyAPICalls.setVolume(volume);
				return volume;
			case "down":
				volume = Math.max(VOLUME_MUTE_VALUE, volume - VOLUME_UPDOWN_VALUE);
				this.spotifyAPICalls.setVolume(volume);
				return volume;
			default:
				this.logger.warn("Incorrect volume command");
				return -1;
			}
		}
		return volume;
	}

	/**
	 * set the volume direct with an integer
	 * 
	 * @param volume
	 *            a interger between 0 and 100
	 * @return the new volume. -1 the volume was not between 0 and 100
	 */
	public int setVolume(int volume) {
		if (volume >= VOLUME_MUTE_VALUE && volume <= VOLUME_MAX_VALUE) {
			this.spotifyAPICalls.setVolume(volume);
			return volume;
		}
		return -1;
	}
}
