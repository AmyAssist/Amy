/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;

/**
 * A Jupiter Extension for the TestFramework
 * 
 * @author Leon Kiefer
 */
public class FrameworkExtention implements TestInstancePostProcessor {

	private TestFramework testFramework;

	/**
	 * @see org.junit.jupiter.api.extension.TestInstancePostProcessor#postProcessTestInstance(java.lang.Object,
	 *      org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void postProcessTestInstance(Object instance, ExtensionContext arg1)
			throws Exception {
		this.testFramework = new TestFramework();
		DependencyInjection dependencyInjection = this.testFramework.dependencyInjection;
		dependencyInjection.inject(instance);
	}
}
