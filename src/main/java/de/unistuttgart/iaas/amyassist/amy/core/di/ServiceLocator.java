/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

/**
 * ServiceLocator is the registry for Services.
 * 
 * @author Leon Kiefer
 */
public interface ServiceLocator {

	/**
	 * Instantiate the given class and inject dependencies. The object created
	 * in this way will not be managed by the DI.
	 * 
	 * @param serviceClass
	 * @return
	 */
	<T> T create(Class<T> serviceClass);

	/**
	 * Get a Service instance
	 * 
	 * @param serviceType
	 * @return
	 */
	<T> T getService(Class<T> serviceType);

	/**
	 * This will analyze the given object and inject into its fields.
	 * 
	 * @param instance
	 */
	void inject(Object instance);
}
