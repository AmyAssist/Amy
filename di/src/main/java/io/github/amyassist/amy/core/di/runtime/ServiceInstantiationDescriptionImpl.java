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

package io.github.amyassist.amy.core.di.runtime;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.ServiceDescription;
import io.github.amyassist.amy.core.di.ServiceInstantiationDescription;

/**
 * Immutable implementation of the ServiceInstantiationDescription
 * 
 * @author Leon Kiefer
 * @param <T>
 *            the type of the service
 */
public class ServiceInstantiationDescriptionImpl<T> implements ServiceInstantiationDescription<T> {
	@Nonnull
	private final ServiceDescription<T> serviceDescription;
	@Nonnull
	private final Map<String, Object> context;
	@Nonnull
	private final Class<?> cls;

	/**
	 * @param serviceDescription
	 *            {@link #getServiceDescription()}
	 * @param context
	 *            {@link #getContext()}
	 * @param cls
	 *            the implementation of the Service {@link #getImplementationClass()}
	 */
	public ServiceInstantiationDescriptionImpl(@Nonnull ServiceDescription<T> serviceDescription,
			@Nonnull Map<String, Object> context, @Nonnull Class<?> cls) {
		this.serviceDescription = serviceDescription;
		this.context = context;
		this.cls = cls;
	}

	/**
	 * Create a new ServiceInstantiationDescription with a empty context.
	 * 
	 * @param serviceDescription
	 *            {@link #getServiceDescription()}
	 * @param cls
	 *            the implementation of the Service {@link #getImplementationClass()}
	 */
	public ServiceInstantiationDescriptionImpl(@Nonnull ServiceDescription<T> serviceDescription,
			@Nonnull Class<?> cls) {
		this.serviceDescription = serviceDescription;
		this.context = Collections.emptyMap();
		this.cls = cls;
	}

	@Override
	public @Nonnull ServiceDescription<T> getServiceDescription() {
		return this.serviceDescription;
	}

	@Override
	public @Nonnull Map<String, Object> getContext() {
		return this.context;
	}

	@Override
	public @Nonnull Class<?> getImplementationClass() {
		return this.cls;
	}

}
