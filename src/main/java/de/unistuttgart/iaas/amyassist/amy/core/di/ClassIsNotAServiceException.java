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
public class ClassIsNotAServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5382225920636029620L;
	private final Class<?> cls;

	/**
	 * @param cls
	 *            the class that is not a Service
	 */
	public ClassIsNotAServiceException(Class<?> cls) {
		this.cls = cls;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return "The class " + this.cls.getName() + " is not a Service";
	}

}
