package de.unistuttgart.iaas.amyassist.amy.core.di.context.provider;

/**
 * A ContextProvider which provides the class of the consumer
 * 
 * @author Leon Kiefer
 */
public class ClassProvider implements StaticProvider<Class<?>> {

	@Override
	public Class<?> getContext(Class<?> consumer) {
		return consumer;
	}

}
