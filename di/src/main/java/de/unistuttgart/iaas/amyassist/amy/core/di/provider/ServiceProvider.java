package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;

/**
 * 
 * @author Leon Kiefer
 *
 * @param <T>
 *            service type
 */
public interface ServiceProvider<T> {
	@Nonnull
	default T getService(Map<Class<?>, ServiceFactory<?>> resolvedDependencies) {
		return this.getService(resolvedDependencies, null, null);
	}

	@Nonnull
	T getService(Map<Class<?>, ServiceFactory<?>> resolvedDependencies,
			@Nullable Map<Class<?>, StaticProvider<?>> contextProviders, @Nullable ServiceConsumer consumer);

	/**
	 * 
	 * @return the dependencies
	 */
	@Nonnull
	Collection<Class<?>> getDependencies();

	/**
	 * @return the requiredContextProviderTypes
	 */
	@Nonnull
	Collection<Class<?>> getRequiredContextProviderTypes();

}
