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

package io.github.amyassist.amy.core.service;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.DependencyInjection;
import io.github.amyassist.amy.core.di.ServiceDescription;
import io.github.amyassist.amy.core.di.ServiceLocator;
import io.github.amyassist.amy.core.di.extension.Extension;

/**
 * This extension deploys the RunnableServices which are registered in the dependency injection to the ServiceManager.
 * 
 * @author Leon Kiefer
 */
public class RunnableServiceExtension implements Extension {

	private ServiceLocator serviceLocator;

	private final Set<ServiceDescription<?>> runnableServices = new HashSet<>();

	@Override
	public void postConstruct(DependencyInjection dependencyInjection) {
		this.serviceLocator = dependencyInjection.getServiceLocator();
	}

	@Override
	public <T> void onRegister(@Nonnull ServiceDescription<T> serviceDescription, @Nonnull Class<? extends T> cls) {
		if (RunnableService.class.isAssignableFrom(cls)) {
			this.runnableServices.add(serviceDescription);
		}
	}

	/**
	 * Deploy all registered RunnableServies
	 */
	public void deploy() {
		ServiceManagerImpl service = this.serviceLocator.getService(ServiceManagerImpl.class);
		this.runnableServices.forEach(service::register);
	}

	/**
	 * Start all registered RunnableServies
	 */
	public void start() {
		this.serviceLocator.getService(ServiceManagerImpl.class).start();
	}

	/**
	 * Stop all running Services
	 */
	public void stop() {
		this.serviceLocator.getService(ServiceManagerImpl.class).stop();
	}

}
