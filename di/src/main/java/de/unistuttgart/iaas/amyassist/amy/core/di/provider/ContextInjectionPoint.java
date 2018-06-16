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

package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import java.lang.reflect.Field;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;

/**
 * A ContextInjectionPoint is an InjectionPoint where the context is injected.
 * 
 * @author Leon Kiefer
 */
public class ContextInjectionPoint extends InjetionPoint {

	private Class<?> contextProviderType;

	/**
	 * @return the contextProviderType
	 */
	public Class<?> getContextProviderType() {
		return this.contextProviderType;
	}

	public ContextInjectionPoint(Field field) {
		super(field);
		Context[] annotations = field.getAnnotationsByType(Context.class);
		if (annotations.length > 1) {
			throw new IllegalArgumentException("In the service " + field.getDeclaringClass().getName() + " the field "
					+ field.getName() + " has the annotation @Context multiple times.");
		}
		Context context = annotations[0];
		this.contextProviderType = context.value();
	}

}
