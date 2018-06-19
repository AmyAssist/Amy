package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;

public class ClassServiceProviderWithoutDependencies<T> implements ServiceFunction<T> {

	protected Class<? extends T> cls;

	public ClassServiceProviderWithoutDependencies(Class<? extends T> cls) {
		if (!Util.classCheck(cls))
			throw new IllegalArgumentException(
					"There is a problem with the class " + cls.getName() + ". It can't be used as a Service");
		this.cls = cls;
	}

	@Override
	public T getService(Map<Class<?>, ServiceFactory<?>> resolvedDependencies, Map<String, ?> context) {
		return this.createService();
	}

	protected T createService() {
		try {
			return cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("The constructor of " + this.cls.getName() + " should have been checked",
					e);
		}
	}

	@Override
	public Collection<Class<?>> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<String> getRequiredContextIdentifiers() {
		return Collections.emptyList();
	}

}
