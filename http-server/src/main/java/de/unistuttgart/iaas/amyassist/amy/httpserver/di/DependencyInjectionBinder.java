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

package de.unistuttgart.iaas.amyassist.amy.httpserver.di;

import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Allow the use of {@link Reference} in Rest Resource classes.
 * 
 * @author Leon Kiefer
 */
public class DependencyInjectionBinder extends AbstractBinder {

	private final ServiceLocator di;

	/**
	 * Create a new Binder for the dependencyInjection
	 * 
	 * @param di
	 *            the ServiceLocator to use to resolve Services in Rest classes
	 */
	public DependencyInjectionBinder(ServiceLocator di) {
		this.di = di;
	}

	@Override
	protected void configure() {
		this.bind(new ServiceInjectionResolver(this.di)).to(new TypeLiteral<Reference>() {
		});
	}

}
