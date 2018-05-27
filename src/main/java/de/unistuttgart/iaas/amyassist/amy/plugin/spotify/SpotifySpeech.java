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
 * @author Lars Buttgereit
 */
@Service(SpotifySpeech.class)
@SpeechCommand("music")
public class SpotifySpeech {

	@Reference
	PlayerLogic playerLogic;
	
	@Grammar("get devices")
	public String getDevices() {
		ArrayList<String> devices = this.playerLogic.getDevices();
		String output = "";
		for(int i = 0; i < devices.size(); i++) {
			output = output + i + ". " + devices.get(i);
		}
		if(output.equals("")) {
			return "no deivce found";
		}
		return output;
	}
	/**
	 * # is a number between 0 and theoretically infinite
	 * @return
	 */
	@Grammar("set device #")
	public String setDevice(int deviceNumber) {
		return this.playerLogic.setDevice(deviceNumber);
	}
}
