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

package io.github.amyassist.amy.core.di.extension;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.DependencyInjection;
import io.github.amyassist.amy.core.di.ServiceDescription;

/**
 * A Extension can be used to extend the behavior of the dependency injection without make a subclass and override
 * methods.
 * 
 * @author Leon Kiefer
 */
public interface Extension {

	/**
	 * Is called when a new Service implementation is registered
	 * 
	 * @param serviceDescription
	 *            the service description
	 * @param cls
	 *            the implementation class
	 * @param <T>
	 *            the type of the registered service
	 */
	<T> void onRegister(@Nonnull ServiceDescription<T> serviceDescription, @Nonnull Class<? extends T> cls);

	/**
	 * Called after the constructor of the dependency injection
	 * 
	 * @param dependencyInjection
	 *            the instance of the dependency injection
	 */
	void postConstruct(DependencyInjection dependencyInjection);

}
