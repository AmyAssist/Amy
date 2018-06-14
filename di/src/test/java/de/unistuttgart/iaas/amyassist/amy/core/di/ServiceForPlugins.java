package de.unistuttgart.iaas.amyassist.amy.core.di;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.PluginProvider;

/**
 * A test service for DependencyInjectionScopeTest
 * 
 * @author Leon Kiefer
 */
@Service
public class ServiceForPlugins {

	@Context(PluginProvider.class)
	private IPlugin plugin;

	public int id;
}
