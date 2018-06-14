package de.unistuttgart.iaas.amyassist.amy.core.di.context.provider;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;

/**
 * A ContextProvider for Plugin informations
 * 
 * @author Leon Kiefer
 */
public class PluginProvider implements StaticProvider<IPlugin> {

	private final Logger logger = LoggerFactory.getLogger(PluginProvider.class);

	private Collection<IPlugin> plugins;

	public PluginProvider(Collection<IPlugin> plugins) {
		this.plugins = plugins;
	}

	@Override
	public IPlugin getContext(Class<?> consumer) {
		for (IPlugin p : this.plugins) {
			if (p.getClasses().contains(consumer)) {
				return p;
			}
		}
		this.logger.error("The class {} does not seem to belong to any plugin.", consumer.getName());
		return null;
	}

}
