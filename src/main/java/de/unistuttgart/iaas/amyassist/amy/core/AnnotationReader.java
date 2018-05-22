/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to read the annotations of a given class
 * 
 * @author Leon Kiefer
 */
public class AnnotationReader {

	/**
	 * Get's the annotated grammars of this class
	 * 
	 * @param cls
	 *            The class of which to get the grammars
	 * @return a List of grammars
	 */
	public List<String> getGrammars(Class<?> cls) {
		List<String> list = new ArrayList<>();
		for (Field field : cls.getFields()) {
			if (field.isAnnotationPresent(Grammar.class)) {
				String grammar = field.getAnnotation(Grammar.class).value();
				list.add(grammar);

			}
		}
		return list;
	}

	/**
	 * Get's the annotated keyword of this class
	 * 
	 * @param cls
	 *            The class of which to get the keyword
	 * @return the keyword
	 */
	public String getSpeechKeyword(Class<?> cls) {
		SpeechCommand speechCommand = cls.getAnnotation(SpeechCommand.class);
		if (speechCommand != null)
			return speechCommand.value();
		return null;
	}
}
