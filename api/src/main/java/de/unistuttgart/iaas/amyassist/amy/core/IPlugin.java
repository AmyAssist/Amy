/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.io.File;
import java.util.List;
import java.util.jar.Manifest;

/**
 * A representation of a plugin
 * 
 * @author Tim Neumann
 */
public interface IPlugin {

	/**
	 * Get's the file of this plugin
	 * 
	 * @return file
	 */
	File getFile();

	/**
	 * Get's the classloader which was used to load this plugin
	 * 
	 * @return classLoader
	 */
	ClassLoader getClassLoader();

	/**
	 * Get's the unique name of the plugin
	 * 
	 * @return uniqueName
	 */
	String getUniqueName();

	/**
	 * Get's the version of this plugin
	 * 
	 * @return mavenVersion
	 */
	String getVersion();

	/**
	 * Get's a list of all classes of this plugin
	 * 
	 * @return classes
	 */
	List<Class<?>> getClasses();

	/**
	 * Get's the manifest of this plugin
	 * 
	 * @return manifest
	 */
	Manifest getManifest();

}