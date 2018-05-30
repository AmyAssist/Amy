/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.Map;
import java.util.NoSuchElementException;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * The class used to store information of the plugins.
 * 
 * @author Felix Burk
 */
public class Storage implements IStorage {
	private String prefix;
	private Map<String, String> globStorage;

	/**
	 * Creates a new Storage for the given prefix using the given global storage
	 * 
	 * @param prefix
	 *            The prefix to use
	 * @param globalStorage
	 *            The global storage to use
	 */
	protected Storage(String prefix, GlobalStorage globalStorage) {
		//TODO: : is used for debugging purposes
		this.prefix = prefix + ":";
		this.globStorage = globalStorage.getStore();
	}

	/**
	 * puts a new value with corresponding plugin key
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#put(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void put(String key, String value) {
		this.globStorage.put(this.prefix + key, value);
	}

	/**
	 * gets value of plugin key
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#get(java.lang.String)
	 */
	@Override
	public String get(String key) throws NoSuchElementException {
		return this.globStorage.get(this.prefix + key);
	}

	/**
	 * checks if plugin key has a value
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#has(java.lang.String)
	 */
	@Override
	public boolean has(String key) {
		return this.globStorage.containsKey(this.prefix + key);
	}

	/**
	 * removes entry
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#delete(java.lang.String)
	 */
	@Override
	public void delete(String key) {
		this.globStorage.remove(this.prefix + key);

	}

}
