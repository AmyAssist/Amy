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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * this class handle the speech commands from the spotify plugin
 * 
 * @author Lars Buttgereit
 */
@Service(SpotifySpeech.class)
@SpeechCommand({ "music", "spotify" })
public class SpotifySpeech {

	private final int SEARCH_LIMIT = 4;

	@Reference
	PlayerLogic playerLogic;

	/**
	 * get a String of all name of all devices
	 * @param params not used here
	 * @return 
	 */
	@Grammar("get devices")
	public String getDevices(String... params) {
		ArrayList<String> devices = this.playerLogic.getDevices();
		String output = "";
		for (int i = 0; i < devices.size(); i++) {
			output = output + i + ". " + devices.get(i);
		}
		if (output.equals("")) {
			return "no deivce found";
		}
		return output;
	}

	/**
	 * set the device in the player logic # is a number between 0 and theoretically
	 * infinite
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
		return "Wrong request";
	}


	/**
	 * play a featured playlist
	 * 
	 * @return
	 */
	@Grammar("play")
	public String playFeaturedPlaylist(String... params) {
		return this.playerLogic.play();
	}

	@Grammar("resume")
	public String resume(String... params) {
		if(this.playerLogic.resume()) {
		return "resume";
		}
		return "An error occurred";
	}

	@Grammar("pause")
	public String pause(String... params) {
		if(this.playerLogic.pausePlayback()) {
		return "pause";
		}
		return "An error occurred";
	}

	@Grammar("skip")
	public String skip(String... params) {
		if(this.playerLogic.skip()) {
		return "skip";
		}
		return "An error occurred";
	}

	@Grammar("back")
	public String back(String... params) {
		if (this.playerLogic.back()) {
		return "back";
		}
		return "An error occurred";
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
		return "track: " + playerLogic.getCurrentSong().get("name") + " by " + playerLogic.getCurrentSong().get("artist");
	}
}
