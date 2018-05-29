/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Global storage placeholder until a database or similar mechanisms are
 * implemented
 * 
 * @author Felix Burk
 */
public class GlobalStorage {
	/**
	 * The map in which everything is stored.
	 */
	protected Map<String, String> store;

	/**
	 * Creates a new Global Storage.
	 * This should only be called once.
	 */
	public GlobalStorage() {
		this.store = new HashMap<>();
	}

	/**
	 * returns global storage HashMap
	 * 
	 * @return store
	 */
	public Map<String, String> getStore() {
		return this.store;
	}

}
