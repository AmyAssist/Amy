/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
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

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * This Class represents a Partial natural language interpreter generated from annotations
 * 
 * @author Leon Kiefer
 */
public class PartialNLI {
	@Nonnull
	private final Method method;
	@Nonnull
	private final AGFNode grammar;
	@Nonnull
	private final Class<?> partialNLIClass;

	/**
	 * @param method
	 *            the method that is called
	 * @param grammar
	 *            the AGFNode representation of the Grammar
	 * @param partialNLIClass
	 *            the class of the partialNLI
	 */
	public PartialNLI(@Nonnull Method method, @Nonnull AGFNode grammar, @Nonnull Class<?> partialNLIClass) {
		this.method = method;
		this.grammar = grammar;
		this.partialNLIClass = partialNLIClass;
	}

	/**
	 * Get's the partialNLI class
	 * 
	 * @return partialNLIClass
	 */
	public Class<?> getPartialNLIClass() {
		return this.partialNLIClass;
	}

	/**
	 * Get's {@link #grammar grammar}
	 * 
	 * @return grammar
	 */
	public AGFNode getGrammar() {
		return this.grammar;
	}

	/**
	 * Invoke the method of this SpeechCommand with an instance of the speechCommandClass
	 * 
	 * @param instance
	 * @param input
	 * @return the result String from calling the command
	 */
	public String call(Object instance, String... input) {
		Object[] params = { input };
		return NLIAnnotationReader.callNLIMethod(this.method, instance, params);
	}
}
