package de.unistuttgart.iaas.amyassist.amy.core.di.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare the use of context information
 * 
 * @author Leon Kiefer
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(java.lang.annotation.ElementType.FIELD)
public @interface Context {
	/**
	 * The class of the context provider to use for getting the context
	 * information
	 * 
	 * @return the ContextProvider class
	 */
	Class<?> value();
}
