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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.SearchTypes;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.SpotifyAPICalls;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;

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
	private Logger logger;
	@Reference
	private MessageHub messageHub;

	private boolean srListening = false;
	private int currentVolume = 0;

	private static final int VOLUME_MUTE_VALUE = 0;
	private static final int VOLUME_MAX_VALUE = 100;
	private static final int VOLUME_UPDOWN_VALUE = 10;

	@PostConstruct
	private void init() {
		this.messageHub.subscribe("home/all/music/mute", message -> {
			switch (message) {
			case "true":
				this.setSRListening(true);
				break;
			case "false":
				this.setSRListening(false);
				break;
			default:
				this.logger.warn("unkown message {}", message);
				break;
			}
		});
	}

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
	 * needed for the first init. this method need the properties file in apikeys
	 * 
	 * @return login link to a personal spotify account
	 */
	public URI firstTimeInit() {
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
	 * this call the searchAnaything method in the Search class
	 * 
	 * @param searchText
	 *            the text you want to search
	 * @param type
	 *            artist, track, playlist, album
	 * @param limit
	 *            how many results maximal searched for
	 * @return one output String with all results
	 */
	public List<Map<String, String>> search(String searchText, String type, int limit) {
		return this.search.searchList(searchText, type, limit);
	}

	/**
	 * this play method play a featured playlist from spotify
	 * 
	 * @return a Playlist object. can be null
	 */
	public PlaylistEntity play() {
		List<PlaylistEntity> playLists;
		playLists = this.search.getFeaturedPlaylists(5);
		if (!playLists.isEmpty() && 1 < playLists.size()
				&& this.spotifyAPICalls.playListFromUri(playLists.get(1).getUri())) {
			return playLists.get(1);
		}
		this.logger.warn("no featured playlist found");
		return null;
	}

	/**
	 * this method play the item that searched before. Use only after a search
	 * 
	 * @param songNumber
	 *            number of the item form the search before
	 * @param type
	 *            to find the right search Results
	 * @return a map with the song data
	 */
	public Map<String, String> play(int songNumber, SearchTypes type) {
		if (songNumber < this.search.restoreUris(type).size()) {
			String uriToPlay = this.search.restoreUris(type).get(songNumber);
			if (uriToPlay.contains("track")) {
				this.spotifyAPICalls.playSongFromUri(uriToPlay);
			} else {
				this.spotifyAPICalls.playListFromUri(uriToPlay);
			}

		} else {
			this.logger.warn("Item not found");
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
	 * get a list from user created or followed playlists
	 * 
	 * @param limit
	 *            limit of returned playlists
	 * @return a list from Playlists
	 */
	public List<PlaylistEntity> getOwnPlaylists(int limit) {
		return this.search.getOwnPlaylists(limit);
	}

	/**
	 * get a list from featured playlists
	 * 
	 * @param limit
	 *            limit of returned playlists
	 * @return a list from Playlists
	 */
	public List<PlaylistEntity> getFeaturedPlaylists(int limit) {
		return this.search.getFeaturedPlaylists(limit);
	}

	/**
	 * this method controls the volume of the player
	 * 
	 * @param volumeString
	 *            allowed strings: mute, max, up, down
	 * @return a int from 0-100. This represent the Volume in percent. if the volume is unknown the return value is -1
	 */
	public int setVolume(String volumeString) {
		int volume = getVolume();
		if (volume != -1) {
			switch (volumeString) {
			case "mute":
				changeVolume(VOLUME_MUTE_VALUE);
				return VOLUME_MUTE_VALUE;
			case "max":
				changeVolume(VOLUME_MAX_VALUE);
				return VOLUME_MAX_VALUE;
			case "up":
				volume = Math.min(VOLUME_MAX_VALUE, volume + VOLUME_UPDOWN_VALUE);
				changeVolume(volume);
				return volume;
			case "down":
				volume = Math.max(VOLUME_MUTE_VALUE, volume - VOLUME_UPDOWN_VALUE);
				changeVolume(volume);
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
			changeVolume(volume);
			return volume;
		}
		return -1;
	}

	/**
	 * get actual volume from 0-100, -1 if no volume available
	 * 
	 * @return the volume
	 */
	public int getVolume() {
		if (this.srListening) {
			return this.currentVolume;
		}
		return this.spotifyAPICalls.getVolume();
	}

	/**
	 * called when volume has to be changed
	 * 
	 * @param volume
	 *            volume to be changed to
	 */
	private void changeVolume(int volume) {
		this.currentVolume = volume;
		if (!this.srListening) {
			this.spotifyAPICalls.setVolume(volume);
		}
	}

	/**
	 * activate/Deactivate that the sr is currently active -> volume changes will be queued until sr is no longer
	 * listening by using the srListening variabel
	 * 
	 * @param isSRListening
	 *            State to change to
	 */
	public void setSRListening(boolean isSRListening) {
		if (isSRListening) {
			this.spotifyAPICalls.setVolume(0);
			this.srListening = isSRListening;
		} else {
			this.srListening = isSRListening;
			changeVolume(Math.max(0, Math.min(100, this.currentVolume)));
		}
	}

}
