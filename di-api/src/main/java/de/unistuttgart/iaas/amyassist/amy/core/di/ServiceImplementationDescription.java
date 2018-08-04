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

package de.unistuttgart.iaas.amyassist.amy.core.di;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Information about the implementation of a Service and the context in which the service is provided. This Descriptor
 * is created from The ServiceProvider which should provide the Service. The Informations is needed by the Dependency
 * Injection to manage the Service instance.
 * 
 * @author Leon Kiefer
 * @param <T>
 *            the type of the service
 */
public interface ServiceImplementationDescription<T> {
	/**
	 * The Service description independent of this implementation specific description. It is used to find a matching ServiceProvider.
	 * 
	 * @return the ServiceDescription
	 */
	@Nonnull
	ServiceDescription<T> getServiceDescription();

	@Nonnull
	Map<String, Object> getContext();

	/**
	 * The implementation class that is used to generate new instances of the service.
	 * <p>
	 * If the class returned is a Factory, then the factory is used to create instances. In this case the system will
	 * get an instance of the factory and use it to create the instances
	 * 
	 * @return The class that directly implements the contract types, or the class that is the factory for an object
	 *         that implements the contract types
	 */
	@Nonnull
	public Class<?> getImplementationClass();
}
