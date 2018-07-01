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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;

/**
 * A InjectionPoint is an abstraction of where an object is injected into an instance.
 * 
 * @author Leon Kiefer
 */
class InjetionPoint {
	private Field field;

	public InjetionPoint(Field field) {
		this.field = field;
	}

	/**
	 * 
	 * @return the Description of the Service required by this InjetionPoint
	 */
	public ServiceDescription<?> getServiceDescription() {
		return Util.serviceDescriptionFor(this.field);
	}

	public void inject(@Nonnull Object instance, @Nullable Object object) {
		Util.inject(instance, object, this.field);
	}
}
