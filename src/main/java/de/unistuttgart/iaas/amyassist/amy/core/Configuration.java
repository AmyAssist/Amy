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
 * Configuration of the application
 * 
 * @author Leon Kiefer
 */
public interface Configuration {
	/**
	 * Get all plugin names
	 * 
	 * @return array of all names
	 */
	String[] getInstalledPlugins();
}
