/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import de.unistuttgart.iaas.amyassist.amy.core.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.IStorage;

/**
 * Does the logic of the Hello World plugin
 * 
 * @author Tim Neumann
 */
public class HelloWorldLogic {
	private static final String KEY = "hellocount";

	/**
	 * A reference to the storage.
	 */
	protected IStorage storage;

	/**
	 * The method that does all the logic.
	 * 
	 * @return the response
	 */
	protected String helloWorld() {
		int count = Integer.parseInt(this.storage.get(HelloWorldLogic.KEY));
		count++;

		String countString = String.valueOf(count);
		this.storage.put(HelloWorldLogic.KEY, countString);

		return "hello" + countString;
	}

	/**
	 * Init method for the logic class
	 * 
	 * @param p_core
	 *            The core.
	 */
	protected void init(ICore p_core) {
		this.storage = p_core.getStorage();

		if (!this.storage.has(HelloWorldLogic.KEY)) {
			this.storage.put(HelloWorldLogic.KEY, "0");
		}
	}

}
