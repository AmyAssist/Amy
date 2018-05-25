/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy;

import java.util.Set;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.reflections.Reflections;

import de.unistuttgart.iaas.amyassist.amy.TestFramework.DIMock;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * A Jupiter Extension for the TestFramework
 * 
 * @author Leon Kiefer
 */
public class FramworkExtention implements TestInstancePostProcessor {

	private TestFramework testFramework;

	/**
	 * @see org.junit.jupiter.api.extension.TestInstancePostProcessor#postProcessTestInstance(java.lang.Object,
	 *      org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void postProcessTestInstance(Object instance, ExtensionContext arg1)
			throws Exception {
		Reflections reflections = new Reflections(
				"de.unistuttgart.iaas.amyassist.amy");

		this.testFramework = new TestFramework();
		DIMock dependencyInjection = this.testFramework.dependencyInjection;

		Set<Class<?>> annotated = reflections
				.getTypesAnnotatedWith(Service.class);
		for (Class<?> a : annotated) {
			dependencyInjection.register(a);
		}

		dependencyInjection.inject(instance);
	}
}
