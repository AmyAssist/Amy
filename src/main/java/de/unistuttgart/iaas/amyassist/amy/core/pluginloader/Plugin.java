/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import java.io.File;
import java.util.ArrayList;

/**
 * A representation of loaded plugin
 * 
 * @author Tim Neumann
 */
public class Plugin {
	/**
	 * The file of the jar of the plugin.
	 */
	private File file;

	/**
	 * The class loader that loads this jar.
	 */
	private ClassLoader classLoader;

	/**
	 * The maven group id of the plugin
	 */
	private String mavenGroupId;

	/**
	 * The maven artifact id of the plugin
	 */
	private String mavenArtifactId;

	/**
	 * The version of the plugin
	 */
	private String mavenVersion;

	private ArrayList<Class<?>> classes = new ArrayList<>();

	/**
	 * Get's {@link #file file}
	 * 
	 * @return file
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Get's {@link #classLoader classLoader}
	 * 
	 * @return classLoader
	 */
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	/**
	 * Get's the unique name of the plugin
	 * 
	 * @return uniqueName
	 */
	public String getUniqueName() {
		return this.mavenGroupId + "." + this.mavenArtifactId;
	}

	/**
	 * Get's {@link #mavenGroupId mavenGroupId}
	 * 
	 * @return mavenGroupId
	 */
	public String getMavenGroupId() {
		return this.mavenGroupId;
	}

	/**
	 * Get's {@link #mavenArtifactId mavenArtifactId}
	 * 
	 * @return mavenArtifactId
	 */
	public String getMavenArtifactId() {
		return this.mavenArtifactId;
	}

	/**
	 * Get's {@link #mavenVersion mavenVersion}
	 * 
	 * @return mavenVersion
	 */
	public String getMavenVersion() {
		return this.mavenVersion;
	}

	/**
	 * Get's {@link #classes classes}
	 * 
	 * @return classes
	 */
	public ArrayList<Class<?>> getClasses() {
		return new ArrayList<>(this.classes);
	}

	/**
	 * Set's {@link #file file}
	 * 
	 * @param file
	 *            file
	 */
	protected void setFile(File file) {
		this.file = file;
	}

	/**
	 * Set's {@link #classLoader classLoader}
	 * 
	 * @param classLoader
	 *            classLoader
	 */
	protected void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Set's {@link #mavenGroupId mavenGroupId}
	 * 
	 * @param mavenGroupId
	 *            mavenGroupId
	 */
	protected void setMavenGroupId(String mavenGroupId) {
		this.mavenGroupId = mavenGroupId;
	}

	/**
	 * Set's {@link #mavenArtifactId mavenArtifactId}
	 * 
	 * @param mavenArtifactId
	 *            mavenArtifactId
	 */
	protected void setMavenArtifactId(String mavenArtifactId) {
		this.mavenArtifactId = mavenArtifactId;
	}

	/**
	 * Set's {@link #mavenVersion mavenVersion}
	 * 
	 * @param mavenVersion
	 *            mavenVersion
	 */
	protected void setMavenVersion(String mavenVersion) {
		this.mavenVersion = mavenVersion;
	}

	/**
	 * Set's {@link #classes classes}
	 * 
	 * @param classes
	 *            classes
	 */
	protected void setClasses(ArrayList<Class<?>> classes) {
		this.classes = classes;
	}

}
