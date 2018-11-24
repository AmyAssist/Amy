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

import io.github.amyassist.amy.core.di.ServiceDescription;
import io.github.amyassist.amy.core.di.ServiceLocator;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * Manages the RunnableServices
 * 
 * @author Leon Kiefer
 */
@Service
public class ServiceManagerImpl {

	@Reference
	private ServiceLocator serviceLocator;

	private Set<ServiceDescription<?>> serviceDescriptions = new HashSet<>();
	private Set<RunnableService> runningServices = new HashSet<>();

	private boolean running = false;

	/**
	 * Register a RunnableService to be started and stopped by this ServiceManager
	 * 
	 * @param serviceDescription
	 *            the serviceDescription of the RunnableService
	 */
	public void register(ServiceDescription<?> serviceDescription) {
		if (this.running)
			throw new IllegalStateException("Service Manager is already running");
		this.serviceDescriptions.add(serviceDescription);
	}

	/**
	 * Start all registered RunnableServies
	 */
	public void start() {
		if (this.running)
			throw new IllegalStateException("Service Manager is already running");
		for (ServiceDescription<?> runnableService : this.serviceDescriptions) {
			RunnableService service = (RunnableService) this.serviceLocator.getService(runnableService).getService();
			this.runningServices.add(service);
		}
		this.runningServices.forEach(RunnableService::start);
		this.running = true;
	}

	/**
	 * Stop all running Services
	 */
	public void stop() {
		if (!this.running)
			throw new IllegalStateException("Service Manager is not running");
		this.runningServices.forEach(RunnableService::stop);
		this.runningServices.clear();
		this.running = false;
	}
}
