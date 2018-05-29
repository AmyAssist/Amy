/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.api.HelloWorldService;

/**
 * A example plugin
 * 
 * @author Leon Kiefer, Tim Neumann
 */
@Service
@SpeechCommand("Hello world")
public class HelloWorldSpeech {

	/**
	 * The logic class of this plugin.
	 */
	@Reference
	private HelloWorldService helloWorld;

	/**
	 * A method that says hello
	 * 
	 * @param params
	 *            [Not used] The parameters of the sentence.
	 * @return The response of the system
	 */
	@Grammar("say hello")
	public String say(String... params) {
		return this.helloWorld.helloWorld();
	}
}
