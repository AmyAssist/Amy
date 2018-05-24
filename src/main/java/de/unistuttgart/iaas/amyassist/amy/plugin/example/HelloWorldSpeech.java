/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import de.unistuttgart.iaas.amyassist.amy.core.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * A example plugin
 * 
 * @author Leon Kiefer, Tim Neumann
 */
@Service(HelloWorldSpeech.class)
@SpeechCommand("Hello world")
public class HelloWorldSpeech {

	/**
	 * The logic class of this plugin.
	 */
	@Reference
	HelloWorldLogic logic;

	/**
	 * A method that says hello
	 * 
	 * @param params
	 *            [Not used] The parameters of the sentence.
	 * @return The response of the system
	 */
	@Grammar("say hello")
	public String say(String... params) {
		return this.logic.helloWorld();
	}
}
