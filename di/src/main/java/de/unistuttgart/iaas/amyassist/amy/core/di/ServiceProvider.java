package de.unistuttgart.iaas.amyassist.amy.core.di;

class ServiceProvider<T> {
	private Class<T> cls;

	ServiceProvider(Class<T> cls) {
		this.cls = cls;
	}

	T newInstance() throws InstantiationException, IllegalAccessException {
		return cls.newInstance();
	}
}
