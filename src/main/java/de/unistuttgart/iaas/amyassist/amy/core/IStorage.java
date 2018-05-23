/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.NoSuchElementException;

/**
 * Interface that defines the storage provided by the core.
 * 
 * @author Leon Kiefer, Tim Neumann
 */
public interface IStorage {

	/**
	 * Stores a string value under a given key
	 * 
	 * @param key
	 *            The key for the data to store
	 * @param value
	 *            The value of the data to store
	 */
	public void put(String key, String value);

	/**
	 * Returns the value for the given key
	 * 
	 * @param key
	 *            The key of the data to return
	 * @return the value of the data
	 * @throws NoSuchElementException
	 *             when the given key is not set
	 */
	public String get(String key) throws NoSuchElementException;

	/**
	 * Check if a given key is set
	 * 
	 * @param key
	 *            The key to check
	 * @return Whether the key is set
	 */
	public boolean has(String key);

	/**
	 * delete value corresponding to key
	 * 
	 * @param key
	 */
	public void delete(String key);

}
