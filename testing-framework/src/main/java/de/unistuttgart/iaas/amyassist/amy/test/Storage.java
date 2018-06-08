/*
 * amy-testing-framework
 *
 * TODO: Project Beschreibung
 *
 * @author Tim Neumann
 * @version 1.0.0
 *
 */
package de.unistuttgart.iaas.amyassist.amy.test;

import java.util.HashMap;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * A basic implementation of IStorage for tests.
 */
public class Storage implements IStorage {

	private HashMap<String, String> map = new HashMap<>();

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#put(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void put(String key, String value) {
		this.map.put(key, value);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#get(java.lang.String)
	 */
	@Override
	public String get(String key) {
		return this.map.get(key);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#has(java.lang.String)
	 */
	@Override
	public boolean has(String key) {
		return this.map.containsKey(key);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#delete(java.lang.String)
	 */
	@Override
	public void delete(String key) {
		this.map.remove(key);
	}

}