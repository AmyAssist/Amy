/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.rest;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Add Support for @Reference dependency declarations in REST Resources.
 * 
 * @author Leon Kiefer
 */
@Singleton
public class ServiceInjectionResolver implements InjectionResolver<Reference> {

	private DependencyInjection dependencyInjection;

	ServiceInjectionResolver(DependencyInjection dependencyInjection) {
		this.dependencyInjection = dependencyInjection;
	}

	/**
	 * @see org.glassfish.hk2.api.InjectionResolver#isConstructorParameterIndicator()
	 */
	@Override
	public boolean isConstructorParameterIndicator() {
		return false;
	}

	/**
	 * @see org.glassfish.hk2.api.InjectionResolver#isMethodParameterIndicator()
	 */
	@Override
	public boolean isMethodParameterIndicator() {
		return false;
	}

	/**
	 * @see org.glassfish.hk2.api.InjectionResolver#resolve(org.glassfish.hk2.api.Injectee,
	 *      org.glassfish.hk2.api.ServiceHandle)
	 */
	@Override
	public Object resolve(Injectee arg0, ServiceHandle<?> arg1) {
		Type requiredType = arg0.getRequiredType();
		return this.dependencyInjection.getService((Class<?>) requiredType);
	}

}
