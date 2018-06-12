/*
 * amy-di
 *
 * TODO: Project Beschreibung
 *
 * @author Tim Neumann
 * @version 1.0.0
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

import java.io.File;
import java.util.ArrayList;
import java.util.jar.Manifest;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;

/**
 * For testing DI.
 * 
 * @author Tim Neumann
 */
public class TestPlugin implements IPlugin {

	private ArrayList<Class<?>> classes;

	/**
	 * New Test Plugin
	 * 
	 * @param p_classes
	 *            classes
	 */
	public TestPlugin(ArrayList<Class<?>> p_classes) {
		this.classes = p_classes;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getFile()
	 */
	@Override
	public File getFile() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getUniqueName()
	 */
	@Override
	public String getUniqueName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getVersion()
	 */
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getClasses()
	 */
	@Override
	public ArrayList<Class<?>> getClasses() {
		return this.classes;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getManifest()
	 */
	@Override
	public Manifest getManifest() {
		// TODO Auto-generated method stub
		return null;
	}

}
