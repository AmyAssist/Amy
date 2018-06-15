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
 *
 * For more information see notice.md
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
