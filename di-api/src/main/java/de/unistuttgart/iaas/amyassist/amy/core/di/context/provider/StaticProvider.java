package de.unistuttgart.iaas.amyassist.amy.core.di.context.provider;

/**
 * A Static Context Provider which provides context information from then
 * consumer class.
 * 
 * @author Leon Kiefer
 */
public interface StaticProvider<T> {
	/**
	 * 
	 * @param consumer
	 *            the class of the consumer
	 * @return the context information
	 */
	T getContext(Class<?> consumer);
}
