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

import java.util.List;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.TrackEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.DeviceLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.PlayerLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.Search;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.SearchTypes;

/**
 * this class handle the speech commands from the spotify plugin
 * 
 * @author Lars Buttgereit
 */
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
	 * @param enties
	 *            input. no input is expected
	 * @return the speech output string
	 */
	@Intent
	public String getDevices(Map<String, EntityData> enties) {
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
	 * @param enties
	 *            input. no input is expected
	 * @return the speech output string
	 */
	@Intent
	public String getCurrentSong(Map<String, EntityData> enties) {
		TrackEntity track = this.playerLogic.getCurrentSong();
		return (track != null) ? "track: " + track.toString() : "No song is playing";
	}

	/**
	 * speech command to get own or featured playlists
	 * 
	 * @param enties
	 *            input. to get the type featured or own
	 * @return the speech output string
	 */
	@Intent
	public String getPlaylists(Map<String, EntityData> enties) {
		StringBuilder builder = new StringBuilder();
		if (enties.get("type").getString() != null) {
			switch (enties.get("type").getString()) {
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
	 * @param enties
	 *            input. no input is expected
	 * @return the speech output string
	 */
	@Intent
	public String playSomething(Map<String, EntityData> enties) {
		PlaylistEntity list = this.playerLogic.play();
		return (list != null) ? list.toString() : "No playlist available";
	}

	/**
	 * speech command for back, skip, pause, resume
	 * 
	 * @param enties
	 *            input. contains the wich command should be used
	 * @return the speech output string
	 */
	@Intent
	public String control(Map<String, EntityData> enties) {
		switch (enties.get("type").getString()) {
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
	 * @param enties
	 *            input. contain one of the following strings: mute, max, up or down
	 * @return the speech output string
	 */
	@Intent
	public String volume(Map<String, EntityData> enties) {
		return "Volume is now on: "
				+ Integer.toString(this.playerLogic.setVolume(enties.get("volumeoption").getString())) + " percent";
	}

	/**
	 * speech command to set a spotify device with id
	 * 
	 * @param enties
	 *            input. contains the id from the device
	 * @return the speech output string
	 */
	@Intent
	public String setDeviceId(Map<String, EntityData> enties) {
		return this.deviceLogic.setDevice(enties.get("deviceid").getNumber());
	}

	/**
	 * speech command to play a playlist that was searched before
	 * 
	 * @param enties
	 *            input. type to play and id from the playlist
	 * @return the speech output string
	 */
	@Intent
	public String playPlaylistId(Map<String, EntityData> enties) {
		PlaylistEntity playlist = null;
		switch (enties.get("type").getString()) {
		case "own":
			playlist = this.playerLogic.playPlaylist(enties.get("songid").getNumber(), SearchTypes.USER_PLAYLISTS);
			break;
		case "featured":
			playlist = this.playerLogic.playPlaylist(enties.get("songid").getNumber(),
					SearchTypes.FEATURED_PLAYLISTS);
			break;
		default:
			break;
		}
		return (playlist != null) ? playlist.toString() : ERROR_MESSAGE_ELEMENT;
	}

}
