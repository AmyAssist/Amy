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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityProvider;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.*;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.DeviceLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.PlayerLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.Search;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.SearchTypes;

/**
 * this class handle the speech commands from the spotify plugin
 * 
 * @author Lars Buttgereit
 */
@Service
@SpeechCommand
public class SpotifySpeech {
	private static final String ERROR_MESSAGE = "An error occurred";
	private static final String ERROR_MESSAGE_ELEMENT = "Element is not available";
	private static final int LIMIT_FOR_SEARCH = 5;

	@Reference
	private PlayerLogic playerLogic;

	@Reference
	private DeviceLogic deviceLogic;

	@Reference
	private Search search;

	/**
	 * speech command to get all online devices from spotify
	 * 
	 * @param entites
	 *            input. no input is expected
	 * @return the speech output string
	 */
	@Intent()
	public String getDevices(Map<String, EntityData> entites) {
		List<DeviceEntity> devices = this.deviceLogic.getDevices();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < devices.size(); i++) {
			builder = builder.append(String.valueOf(i)).append(". ").append(devices.get(i).getName().concat("\n"));
		}
		if (builder.length() > 0) {
			builder.insert(0, "Following Devices are online: \n");
		} else {
			builder.append("No devices are online");
		}
		return builder.toString();
	}

	/**
	 * speech command to get the currently song that is playing
	 * 
	 * @param entites
	 *            input. no input is expected
	 * @return the speech output string
	 */
	@Intent()
	public String getCurrentSong(Map<String, EntityData> entites) {
		TrackEntity track = this.playerLogic.getCurrentSong();
		return (track != null) ? "track: " + track.toString() : "No song is playing";
	}

	/**
	 * speech command to get own or featured playlists
	 * 
	 * @param entites
	 *            input. to get the type featured or own
	 * @return the speech output string
	 */
	@Intent()
	public String getPlaylists(Map<String, EntityData> entites) {
		StringBuilder builder = new StringBuilder();
		if (entites.get("type").getString() != null) {
			switch (entites.get("type").getString()) {
			case "featured":
				for (PlaylistEntity playlist : this.search.searchFeaturedPlaylists(LIMIT_FOR_SEARCH)) {
					builder = builder.append(playlist.toString()).append("\n");
				}
				break;
			case "own":
				for (PlaylistEntity playlist : this.search.searchOwnPlaylists(LIMIT_FOR_SEARCH)) {
					builder = builder.append(playlist.toString()).append("\n");
				}
				break;
			default:
				break;
			}
		}
		if (builder.length() > 0) {
			builder.insert(0, "Following Playlists found: \n");
		} else {
			builder.append("No playlists found");
		}
		return builder.toString();

	}

	/**
	 * speech command for play a 'random' playlist
	 * 
	 * @param entites
	 *            input. no input is expected
	 * @return the speech output string
	 */
	@Intent()
	public String playSomething(Map<String, EntityData> entites) {
		PlaylistEntity list = this.playerLogic.play();
		return (list != null) ? list.toString() : "No playlist available";
	}

	/**
	 * speech command for back, skip, pause, resume
	 * 
	 * @param entites
	 *            input. contains the wich command should be used
	 * @return the speech output string
	 */
	@Intent()
	public String control(Map<String, EntityData> entites) {
		switch (entites.get("type").getString()) {
		case "back":
			if (this.playerLogic.back()) {
				return "back";
			}
			return ERROR_MESSAGE;
		case "skip":
			if (this.playerLogic.skip()) {
				return "skip";
			}
			return ERROR_MESSAGE;
		case "pause":
			if (this.playerLogic.pause()) {
				return "pause";
			}
			return ERROR_MESSAGE;
		case "resume":
			if (this.playerLogic.resume()) {
				return "resume";
			}
			return ERROR_MESSAGE;
		default:
			return ERROR_MESSAGE;
		}
	}

	/**
	 * speech command for the volume control
	 * 
	 * @param entites
	 *            input. contain one of the following strings: mute, max, up or down
	 * @return the speech output string
	 */
	@Intent()
	public String volume(Map<String, EntityData> entites) {
		int volume = this.playerLogic.setVolume(entites.get("volumeoption").getString());
		return (volume != -1) ? "Volume is now on: " + volume + " percent" : "Wrong volume range";
	}

	/**
	 * speech command for the volume control to set volume by percent
	 * 
	 * @param entites
	 *            input. the new volume in percent
	 * @return the speech output string
	 */
	@Intent()
	public String volumePercent(Map<String, EntityData> entites) {
		int volume = this.playerLogic.setVolume(entites.get("volume").getNumber());
		return (volume != -1) ? "Volume is now on: " + volume + " percent" : "Wrong volume range";
	}

	/**
	 * speech command to set a spotify device with id
	 * 
	 * @param entites
	 *            input. contains the id from the device
	 * @return the speech output string
	 */
	@Intent()
	public String setDeviceId(Map<String, EntityData> entites) {
		return this.deviceLogic.setDevice(entites.get("deviceid").getNumber());
	}

	/**
	 * speech command to set a spotify device with the name of the device
	 * 
	 * @param entites
	 *            input. contains the name from the device
	 * @return the speech output string
	 */
	@Intent()
	public String setDeviceName(Map<String, EntityData> entites) {
		for (DeviceEntity device : this.deviceLogic.getDevices()) {
			if (device.getName().equalsIgnoreCase(entites.get("devicename").getString())) {
				this.deviceLogic.setDevice(device.getID());
				return device.getName();
			}
		}
		return "Device not found";
	}

	/**
	 * speech command to play a playlist that was searched before
	 * 
	 * @param entites
	 *            input. type to play and id from the playlist
	 * @return the speech output string
	 */

	@Intent()
	public String playPlaylistId(Map<String, EntityData> entites) {
		PlaylistEntity playlist = null;
		switch (entites.get("type").getString()) {
		case "own":
			playlist = this.playerLogic.playPlaylist(entites.get("songid").getNumber(), SearchTypes.USER_PLAYLISTS);
			break;
		case "featured":
			playlist = this.playerLogic.playPlaylist(entites.get("songid").getNumber(), SearchTypes.FEATURED_PLAYLISTS);
			break;
		default:
			break;
		}
		return (playlist != null) ? playlist.toString() : ERROR_MESSAGE_ELEMENT;
	}

	/**
	 * speech command to search for a track, album or palyist and play this directly
	 * 
	 * @param entites
	 *            input. name
	 * @return the track, album, playist that is now playing
	 */
	@Intent()
	public String searchASong(Map<String, EntityData> entites) {
		switch (entites.get("mode").getString()) {
		case "track":
		case "song":
			List<TrackEntity> tracks = this.search.searchforTracks(entites.get("name").getString(), 1);
			if (!tracks.isEmpty() && tracks.get(0) != null) {
				return this.playerLogic.playTrack(0).toString();
			}
			break;
		case "playlist":
			List<PlaylistEntity> playlists = this.search.searchforPlaylists(entites.get("name").getString(), 1);
			if (!playlists.isEmpty() && playlists.get(0) != null) {
				return this.playerLogic.playPlaylist(0, SearchTypes.SEARCH_PLAYLISTS).toString();
			}
			break;
		case "album":
			List<AlbumEntity> albums = this.search.searchforAlbums(entites.get("name").getString(), 1);
			if (!albums.isEmpty() && albums.get(0) != null) {
				return this.playerLogic.playAlbum(0).toString();
			}
			break;
		case "artist":
		case "artists":
			List<ArtistEntity> artists = this.search.searchforArtists(entites.get("name").getString(), 1);
			if (!artists.isEmpty() && artists.get(0) != null) {
				return this.playerLogic.playArtist(0).toString();
			}
			break;
		default:
			break;
		}

		return ERROR_MESSAGE_ELEMENT;
	}

	/**
	 * provide the device names to the speech
	 * 
	 * @return a list with all device names
	 */
	@EntityProvider("devicename")
	public List<String> getDeviceNames() {
		List<String> deviceNames = new ArrayList<>();
		for (DeviceEntity device : this.deviceLogic.getDevices()) {
			deviceNames.add(device.getName());
		}
		return deviceNames;
	}
}
