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

package de.unistuttgart.iaas.amyassist.amy.core.service;

import java.util.HashSet;
import java.util.Set;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumerImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.extension.Extension;

/**
 * This Extension calls the deploy method on all registered {@link DeploymentContainerService}.
 * 
 * @author Leon Kiefer
 */
public class DeploymentContainerServiceExtension implements Extension {

	private DependencyInjection di;

	private final Set<ServiceDescription<?>> deploymentContainerServices = new HashSet<>();

	@Override
	public void postConstruct(DependencyInjection dependencyInjection) {
		this.di = dependencyInjection;
	}

	@Override
	public <T> void onRegister(ServiceDescription<T> serviceDescription, Class<? extends T> cls) {
		if (DeploymentContainerService.class.isAssignableFrom(cls)) {
			this.deploymentContainerServices.add(serviceDescription);
		}
	}

	/**
	 * Deploy all registered RunnableServies
	 */
	public void deploy() {
		for (ServiceDescription<?> deploymentContainerServiceDescription : this.deploymentContainerServices) {
			DeploymentContainerService deploymentContainerService = (DeploymentContainerService) this.di
					.getService(new ServiceConsumerImpl<>(this.getClass(), deploymentContainerServiceDescription))
					.getService();
			deploymentContainerService.deploy();
		}
	}

}
