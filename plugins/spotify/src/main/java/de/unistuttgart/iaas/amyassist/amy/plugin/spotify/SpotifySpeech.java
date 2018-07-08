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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.PlaylistEntity;

/**
 * this class handle the speech commands from the spotify plugin
 * 
 * @author Lars Buttgereit
 */
@Service(SpotifySpeech.class)
@SpeechCommand({ "music", "spotify" })
public class SpotifySpeech {

	private static final String ERROR_MESSAGE = "An error occurred";
	private static final int LIMIT_FOR_SEARCH = 5;

	@Reference
	private PlayerLogic playerLogic;

	@Reference
	private StringGenerator stringGenerator;

	/**
	 * get a String of all name of all devices
	 * 
	 * @param params
	 *            not used here
	 * @return
	 */
	@Grammar("get devices")
	public String getDevices(String... params) {
		List<DeviceEntity> devices = this.playerLogic.getDevices();
		String output = "";
		for (int i = 0; i < devices.size(); i++) {
			output = output.concat(String.valueOf(i)).concat(". ").concat(devices.get(i).getName().concat("\n"));
		}
		if (output.equals("")) {
			return "no device found";
		}
		return output;
	}

	/**
	 * set the device in the player logic # is a number between 0 and theoretically infinite
	 * 
	 * @param params
	 * 
	 * @return
	 */
	@Grammar("set device #")
	public String setDevice(String... params) {
		if (2 < params.length) {
			try {
				return this.playerLogic.setDevice(Integer.parseInt(params[2]));
			} catch (NumberFormatException e) {
				return "Input please as Integer";
			}
		}
		return ERROR_MESSAGE;
	}

	/**
	 * play a featured playlist
	 * 
	 * @return
	 */
	@Grammar("play")
	public String playASong(String... params) {
		return (this.playerLogic.play().toString());
	}

	@Grammar("play featured playlist #")
	public String playFeaturedPlaylist(String... params) {
		return this.stringGenerator.generateSearchOutputString(
				this.playerLogic.play(Integer.parseInt(params[3]), SearchTypes.FEATURED_PLAYLISTS));
	}

	@Grammar("play own playlist #")
	public String play(String... params) {
		return this.stringGenerator.generateSearchOutputString(
				this.playerLogic.play(Integer.parseInt(params[3]), SearchTypes.USER_PLAYLISTS));
	}

	@Grammar("resume")
	public String resume(String... params) {
		if (this.playerLogic.resume()) {
			return "resume";
		}
		return ERROR_MESSAGE;
	}

	@Grammar("pause")
	public String pause(String... params) {
		if (this.playerLogic.pause()) {
			return "pause";
		}
		return ERROR_MESSAGE;
	}

	@Grammar("skip")
	public String skip(String... params) {
		if (this.playerLogic.skip()) {
			return "skip";
		}
		return ERROR_MESSAGE;
	}

	@Grammar("back")
	public String back(String... params) {
		if (this.playerLogic.back()) {
			return "back";
		}
		return ERROR_MESSAGE;
	}

	@Grammar("volume (mute|max|up|down)")
	public String volume(String... params) {
		if (1 < params.length) {
			return Integer.toString(this.playerLogic.setVolume(params[1]));
		}
		return "not enough arguments";
	}

	@Grammar("get currently played song")
	public String getCurrentlyPlayed(String... params) {
		return "track: " + playerLogic.getCurrentSong().get("name") + " by "
				+ playerLogic.getCurrentSong().get("artist");
	}

	@Grammar("get own playlists")
	public String getUserplaylists(String... params) {
		String output = "";
		for (PlaylistEntity playlist : this.playerLogic.getOwnPlaylists(LIMIT_FOR_SEARCH)) {
			output = output.concat(playlist.toString()).concat("\n");
		}
		return output;
	}

	@Grammar("get featured playlists")
	public String getFeaturedPlaylists(String... params) {
		String output = "";
		for (PlaylistEntity playlist : this.playerLogic.getFeaturedPlaylists(LIMIT_FOR_SEARCH)) {
			output = output.concat(playlist.toString()).concat("\n");
		}
		return output;
	}

	@Grammar("create login link")
	public String createLoginLink(String... params) {
		return this.playerLogic.firstTimeInit().toString();
	}

}
