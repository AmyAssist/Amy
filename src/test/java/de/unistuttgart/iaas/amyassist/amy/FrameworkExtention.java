/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * A Jupiter Extension for the TestFramework
 * 
 * @author Leon Kiefer
 */
public class FrameworkExtention
		implements TestInstancePostProcessor, BeforeTestExecutionCallback, AfterTestExecutionCallback {

	private TestFramework testFramework;

	/**
	 * @see org.junit.jupiter.api.extension.TestInstancePostProcessor#postProcessTestInstance(java.lang.Object,
	 *      org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void postProcessTestInstance(Object instance, ExtensionContext arg1) throws Exception {
		this.testFramework = new TestFramework();
		this.testFramework.setup(instance);
	}

	/**
	 * @see org.junit.jupiter.api.extension.BeforeTestExecutionCallback#beforeTestExecution(org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void beforeTestExecution(ExtensionContext arg0) throws Exception {
		this.testFramework.before();
	}

	/**
	 * @see org.junit.jupiter.api.extension.AfterTestExecutionCallback#afterTestExecution(org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void afterTestExecution(ExtensionContext arg0) throws Exception {
		this.testFramework.after();
	}
}
