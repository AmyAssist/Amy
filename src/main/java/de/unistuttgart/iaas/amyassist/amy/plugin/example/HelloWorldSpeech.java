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
import de.unistuttgart.iaas.amyassist.amy.core.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.Init;
import de.unistuttgart.iaas.amyassist.amy.core.SpeechCommand;

/**
 * A example plugin
 * 
 * @author Leon Kiefer, Tim Neumann
 */
@SpeechCommand("Hello world")
public class HelloWorldSpeech {

	/**
	 * The logic class of this plugin.
	 */
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

	/**
	 * The init method of this class
	 * 
	 * @param p_core
	 *            The core of the system, from which we get the storage.
	 */
	@Init
	public void init(ICore p_core) {
		this.logic = new HelloWorldLogic();
		this.logic.init(p_core);
	}

}
