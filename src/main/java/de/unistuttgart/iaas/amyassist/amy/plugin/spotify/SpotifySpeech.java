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
	public void resume(String... params) {
		this.playerLogic.resume();
	}

	@Grammar("pause")
	public void pause(String... params) {
		this.playerLogic.pausePlayback();
	}

	@Grammar("skip")
	public void skip(String... params) {
		this.playerLogic.skip();
	}

	@Grammar("back")
	public void back(String... params) {
		this.playerLogic.back();
	}

	@Grammar("volume (mute|max|up|down)")
	public void volume(String... params) {
		if (1 < params.length)
			this.playerLogic.setVolume(params[1]);
	}
	@Grammar("get currently played song")
	public String getCurrentlyPlayed() {
		return "track: " + playerLogic.getCurrentSong().get("name") + " by " + playerLogic.getCurrentSong().get("artist");
	}
}
