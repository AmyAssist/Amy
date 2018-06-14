package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;

/**
 * A ServiceProvider which provides only a single existing instance
 * 
 * @author Leon Kiefer
 */
public class SingeltonServiceProvider<T> implements ServiceFunction<T> {

	private final T instance;

	public SingeltonServiceProvider(@Nonnull T instance) {
		this.instance = instance;
	}

	@Override
	public T getService(Map<Class<?>, ServiceFactory<?>> resolvedDependencies,
			@Nullable Map<Class<?>, StaticProvider<?>> contextProviders, @Nullable ServiceConsumer consumer) {
		return this.instance;
	}

	@Override
	public Collection<Class<?>> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Collection<Class<?>> getRequiredContextProviderTypes() {
		return Collections.emptySet();
	}

}
