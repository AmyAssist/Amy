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
 * A exception of the dependency injection
 * 
 * @author Leon Kiefer
 */
public class ServiceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2441944380474159637L;
	private Class<?> serviceType;

	/**
	 * @param serviceType
	 */
	public ServiceNotFoundException(Class<?> serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return "The Service " + this.serviceType.getName()
				+ " is not registered in the DI or do not exists.";
	}

}
