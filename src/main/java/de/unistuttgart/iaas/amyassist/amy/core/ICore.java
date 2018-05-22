package de.unistuttgart.iaas.amyassist.amy.core;

public interface ICore {
	/**
	 * Store a value for later use in the plugin
	 * 
	 * @param key
	 * @param value
	 */
	void store(String key, String value);

	/**
	 * read the value for the given key or throw an Exception
	 * 
	 * @param key
	 * @return the value
	 */
	String read(String key);

	/**
	 * Check if a value exists for the given key
	 * 
	 * @param key
	 * @return
	 */
	boolean has(String key);
}
