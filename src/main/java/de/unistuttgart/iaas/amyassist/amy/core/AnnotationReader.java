/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Init;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * This class is responsible to read the annotations of a given class
 * 
 * @author Leon Kiefer
 */
public class AnnotationReader {

	/**
	 * Get's the with @Init annotated init method of the class
	 * 
	 * @param cls
	 *            The class of which to get the init method
	 * @return the method or null if not found
	 */
	public Method getInitMethod(Class<?> cls) {
		for (Method method : cls.getMethods()) {
			if (method.isAnnotationPresent(Init.class)) {
				if (!method.getReturnType().equals(Void.TYPE)) {
					System.err.println(
							"The method annotated with @Init must have return type void");
					return null;
				}
				Class<?>[] parameterTypes = method.getParameterTypes();
				Class<ICore> c = ICore.class;
				if (parameterTypes.length != 1
						|| !parameterTypes[0].equals(c)) {
					System.err.println(
							"The method annotated with @Init must have only one parameter of type ICore");
					return null;
				}

				return method;
			}
		}
		return null;
	}

	/**
	 * Get's the annotated grammars of this class
	 * 
	 * @param cls
	 *            The class of which to get the grammars
	 * @return a List of grammars
	 */
	public List<String> getGrammars(Class<?> cls) {
		List<String> list = new ArrayList<>();
		for (Method method : cls.getMethods()) {
			if (method.isAnnotationPresent(Grammar.class)) {
				String grammar = method.getAnnotation(Grammar.class).value();
				list.add(grammar);

			}
		}
		return list;
	}

	/**
	 * Get's the annotated keywords of this class
	 * 
	 * @param cls
	 *            The class of which to get the keyword
	 * @return the keywords
	 */
	public String[] getSpeechKeyword(Class<?> cls) {
		SpeechCommand speechCommand = cls.getAnnotation(SpeechCommand.class);
		if (speechCommand != null)
			return speechCommand.value();
		return null;
	}
}
