package de.unistuttgart.iaas.amyassist.amy.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationReader {

	public List<String> getGrammars(Class<?> c) {
		List<String> list = new ArrayList<>();
		for (Field field : c.getFields()) {
			if (field.isAnnotationPresent(Grammar.class)) {
				String grammar = field.getAnnotation(Grammar.class).value();
				list.add(grammar);

			}
		}
		return list;
	}

	public String getSpeechKeyword(Class<?> c) {
		SpeechCommand speechCommand = c.getAnnotation(SpeechCommand.class);
		if (speechCommand != null) {
			return speechCommand.value();
		} else {
			return null;
		}
	}
}
