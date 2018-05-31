/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This Class represents a SpeechCommand that can be called
 * 
 * @author Leon Kiefer
 */
public class SpeechCommand {
	private Method method;
	private String grammar;
	private Class<?> speechCommandClass;

	/**
	 * Get's {@link #speechCommandClass speechCommandClass}
	 * 
	 * @return speechCommandClass
	 */
	public Class<?> getSpeechCommandClass() {
		return this.speechCommandClass;
	}

	/**
	 * @param method
	 *            the method that is called
	 * @param grammar
	 *            the String representation of the Grammar
	 * @param speechCommandClass
	 *            the class of the SpeechCommand
	 */
	public SpeechCommand(Method method, String grammar, Class<?> speechCommandClass) {
		this.method = method;
		this.grammar = grammar;
		this.speechCommandClass = speechCommandClass;
	}

	/**
	 * Invoke the method of this SpeechCommand with an instance of the
	 * speechCommandClass
	 * 
	 * @param instance
	 * @param input
	 * @return the result String from calling the command
	 */
	public String call(Object instance, String... input) {
		try {
			Object[] params = { input };
			return (String) this.method.invoke(instance, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
