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

/**
 * ServiceLocator is the registry for Services.
 * 
 * @author Leon Kiefer
 */
public interface ServiceLocator {

	/**
	 * Instantiate the given class and inject dependencies. The object created
	 * in this way will not be managed by the DI.
	 * 
	 * @param serviceClass
	 * @return
	 */
	<T> T create(Class<T> serviceClass);

	/**
	 * Get the service of the given type. This method can return external
	 * services and does return a object of the type registered for the service
	 * type.
	 * 
	 * @param serviceType
	 * @return
	 */
	<T> T getService(Class<T> serviceType);

	/**
	 * This will analyze the given object and inject into its fields.
	 * 
	 * @param instance
	 */
	void inject(Object instance);

	/**
	 * init the instance
	 * 
	 * @param instance
	 */
	void postConstruct(Object instance);
}
