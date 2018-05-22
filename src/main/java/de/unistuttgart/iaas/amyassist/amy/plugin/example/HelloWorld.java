/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import de.unistuttgart.iaas.amyassist.amy.core.Core;
import de.unistuttgart.iaas.amyassist.amy.core.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.Init;
import de.unistuttgart.iaas.amyassist.amy.core.SpeechCommand;

/**
 * A example plugin
 * 
 * @author Leon Kiefer, Tim Neumann
 */
@SpeechCommand("Hello world")
public class HelloWorld {
	private static final String KEY = "hellocount";

	/**
	 * A reference to the storage.
	 */
	protected IStorage storage;

	/**
	 * A method that says hello
	 * 
	 * @param params
	 *            [Not used] The parameters of the sentence.
	 * @return The response of the system
	 */
	@Grammar("say hello")
	public String say(String... params) {
		int count = Integer.parseInt(this.storage.get(HelloWorld.KEY));
		count++;

		String countString = String.valueOf(count);
		this.storage.put(HelloWorld.KEY, countString);

		return "hello" + countString;
	}

	/**
	 * The init method of this class
	 * 
	 * @param p_core
	 *            The core of the system, from which we get the storage.
	 */
	@Init
	public void init(Core p_core) {
		this.storage = p_core.getStorage();

		if (!this.storage.has(HelloWorld.KEY)) {
			this.storage.put(HelloWorld.KEY, "0");
		}
	}
}
