/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.Set;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;

/**
 * Implementation of {@link Configuration}
 * 
 * @author Leon Kiefer
 */
@Service
public class ConfigurationImpl implements Configuration {

	@Reference
	private PluginLoader loader;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.Configuration#getInstalledPlugins()
	 */
	@Override
	public String[] getInstalledPlugins() {
		Set<String> pluginNames = this.loader.getPluginNames();
		String[] array = pluginNames.toArray(new String[pluginNames.size()]);
		return array;
	}

}
