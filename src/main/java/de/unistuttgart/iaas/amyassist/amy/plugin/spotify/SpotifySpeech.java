/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.util.ArrayList;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * TODO: Description
 * 
 * @author Lars Buttgereit
 */
@Service(SpotifySpeech.class)
@SpeechCommand({ "music", "spotify" })
public class SpotifySpeech {

	private final int SEARCH_LIMIT = 4;

	@Reference
	PlayerLogic playerLogic;

	@Grammar("get devices")
	public String getDevices() {
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
	 * transmit the search query to the player logic # any String
	 * 
	 * @param params
	 * @return
	 */
	@Grammar("search # in (artist|track|playlist|album)")
	public String search(String... params) {
		if (3 < params.length) {
			return this.playerLogic.search(params[1], params[3], this.SEARCH_LIMIT);
		}
		return "wrong request";
	}

	/**
	 * play a featured playlist
	 * 
	 * @return
	 */
	@Grammar("play")
	public String playFeaturedPlaylist() {
		return this.playerLogic.play();
	}

	/**
	 * this play method please use only when a search is executed before # Number of
	 * the Search query
	 * 
	 * @param params
	 * @return
	 */
	@Grammar("play #")
	public String play(String... params) {
		if (1 < params.length) {
			try {
				return this.playerLogic.play(new Integer(params[1]).intValue());
			} catch (NumberFormatException e) {
				return "Input please as a integer";
			}
		}
		return "Wrong request";
	}

	@Grammar("resume")
	public void resume() {
		this.playerLogic.resume();
	}

	@Grammar("pause")
	public void pause() {
		this.playerLogic.pausePlayback();
	}

	@Grammar("skip")
	public void skip() {
		this.playerLogic.skip();
	}

	@Grammar("back")
	public void back() {
		this.playerLogic.back();
	}

	@Grammar("volume (mute|max|up|down)")
	public void volume(String... params) {
		if (1 < params.length)
			this.playerLogic.setVolume(params[1]);
	}
}
