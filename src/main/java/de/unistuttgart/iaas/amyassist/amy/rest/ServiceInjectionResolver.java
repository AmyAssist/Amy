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

package de.unistuttgart.iaas.amyassist.amy.rest;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Add Support for @Reference dependency declarations in REST Resources.
 * 
 * @author Leon Kiefer
 */
@Singleton
public class ServiceInjectionResolver implements InjectionResolver<Reference> {

	private ServiceLocator serviceLocator;

	ServiceInjectionResolver(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	/**
	 * @see org.glassfish.hk2.api.InjectionResolver#isConstructorParameterIndicator()
	 */
	@Override
	public boolean isConstructorParameterIndicator() {
		return false;
	}

	/**
	 * @see org.glassfish.hk2.api.InjectionResolver#isMethodParameterIndicator()
	 */
	@Override
	public boolean isMethodParameterIndicator() {
		return false;
	}

	/**
	 * @see org.glassfish.hk2.api.InjectionResolver#resolve(org.glassfish.hk2.api.Injectee,
	 *      org.glassfish.hk2.api.ServiceHandle)
	 */
	@Override
	public Object resolve(Injectee arg0, ServiceHandle<?> arg1) {
		Type requiredType = arg0.getRequiredType();
		return this.serviceLocator.getService((Class<?>) requiredType);
	}

}
