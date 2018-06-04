/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.unistuttgart.iaas.amyassist.amy.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.MethodUtils;

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
	@Deprecated
	public Method getInitMethod(Class<?> cls) {
		Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(cls, Init.class);
		if (methodsWithAnnotation.length != 1) {
			return null;
		}
		Method method = methodsWithAnnotation[0];
		if (!method.getReturnType().equals(Void.TYPE)) {
			System.err.println("The method annotated with @Init must have return type void");
			return null;
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<ICore> c = ICore.class;
		if (parameterTypes.length != 1 || !parameterTypes[0].equals(c)) {
			System.err.println("The method annotated with @Init must have only one parameter of type ICore");
			return null;
		}

		return method;
	}

	/**
	 * Get's the annotated grammars of this class
	 * 
	 * @param cls
	 *            The class of which to get the grammars
	 * @return a List of grammars
	 */
	public Map<String, de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommand> getGrammars(Class<?> cls) {
		Map<String, de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommand> map = new HashMap<>();
		Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(cls, Grammar.class);
		for (Method method : methodsWithAnnotation) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != 1 || !parameterTypes[0].isArray()
					|| !parameterTypes[0].getComponentType().equals(String.class)) {
				throw new IllegalArgumentException("The method " + method.toString()
						+ " does not have the correct parameter type. It should be String[].");
			}
			if (!method.getReturnType().equals(String.class)) {
				throw new IllegalArgumentException(
						"The returntype of a method annotated with @Grammar should be String.");
			}
			String grammar = method.getAnnotation(Grammar.class).value();
			map.put(grammar, new de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommand(method, grammar, cls));

		}
		return map;
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
		if (speechCommand == null)
			throw new RuntimeException("The class " + cls.getName() + " have no SpeechCommand Annotation.");

		return speechCommand.value();
	}
}
