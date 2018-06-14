package de.unistuttgart.iaas.amyassist.amy.core.di;

/**
 * The interface of all ServiceFactories
 * 
 * @author Leon Kiefer
 */
public interface ServiceFactory<T> {
	/**
	 * Build the Service after all configuration is done. This doesn't mean a
	 * new instance is created. Multiple calls of this method must return the
	 * same instance. So after calling this method the configuration can't be
	 * changed.
	 * 
	 * @return the instance of the Service
	 */
	T build();
}
