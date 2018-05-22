/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

/**
 * The central core of the application
 * 
 * @author Tim Neumann
 */
public class Core {
	private IStorage storage;

	/**
	 * The method executed by the main method
	 */
	protected void run() {

	}

	/**
	 * Get's {@link #storage storage}
	 * 
	 * @return storage
	 */
	public IStorage getStorage() {
		return this.storage;
	}

}
