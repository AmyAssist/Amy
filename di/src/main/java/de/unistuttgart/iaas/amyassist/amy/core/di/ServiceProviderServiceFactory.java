package de.unistuttgart.iaas.amyassist.amy.core.di;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;

/**
 * A ServiceFactory for building Services for ServiceProviders
 * 
 * @author Leon Kiefer
 */
public class ServiceProviderServiceFactory<T> implements ServiceFactory<T> {

	private ServiceProvider<T> serviceProvider;

	private Map<Class<?>, ServiceFactory<?>> resolvedDependencies = new HashMap<>();
	private Map<Class<?>, StaticProvider<?>> contextProviders = new HashMap<>();
	@Nullable
	private ServiceConsumer consumerClass;

	private T buildedInstance;

	public ServiceProviderServiceFactory(ServiceProvider<T> serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	@Override
	public T build() {
		if (this.buildedInstance == null) {
			this.buildedInstance = this.serviceProvider.getService(this.resolvedDependencies, this.contextProviders,
					this.consumerClass);
		}
		return this.buildedInstance;
	}

	public void resolved(Class<?> dependency, ServiceFactory<?> dependencyFactory) {
		this.resolvedDependencies.put(dependency, dependencyFactory);
	}

	public void setContextProvider(Class<?> requiredContextProviderType, StaticProvider<?> contextProvider) {
		this.contextProviders.put(requiredContextProviderType, contextProvider);
	}

	public void setConsumer(@Nullable ServiceConsumer consumer) {
		this.consumerClass = consumer;
	}
}
