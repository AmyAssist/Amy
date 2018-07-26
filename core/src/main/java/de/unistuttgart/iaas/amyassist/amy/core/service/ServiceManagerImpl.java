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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import de.unistuttgart.iaas.amyassist.amy.core.di.Configuration;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceProviderLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Manages the RunnableServices
 * 
 * @author Leon Kiefer
 */
@Service
public class ServiceManagerImpl {

	private static final String SERVICE_DEPLOYMENT_DESCRIPTOR = "META-INF/" + RunnableService.class.getName();

	@Reference
	private Configuration configuration;

	@Reference
	private ServiceLocator serviceLocator;

	private Set<Class<? extends RunnableService>> classes = new HashSet<>();
	private Set<RunnableService> runningServices = new HashSet<>();

	private boolean running = false;

	/**
	 * Loads Services using the deployment descriptor file
	 * META-INF/de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService
	 */
	public void loadServices() {
		ClassLoader classLoader = this.getClass().getClassLoader();
		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(SERVICE_DEPLOYMENT_DESCRIPTOR);
		} catch (IOException e) {
			throw new IllegalStateException("Could not read the Service deployment descriptor", e);
		}
		while (resources.hasMoreElements()) {
			try (InputStream resourceAsStream = resources.nextElement().openStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))) {
				String className;
				while ((className = reader.readLine()) != null) {
					if (className.isEmpty() || className.startsWith("#")) {
						continue;
					}
					this.register(className, classLoader);
				}
			} catch (IOException e) {
				throw new IllegalStateException("Could not read the Service deployment descriptor", e);
			}
		}

	}

	private void register(String className, ClassLoader classLoader) {
		try {
			Class<?> cls = Class.forName(className, true, classLoader);
			if (!RunnableService.class.isAssignableFrom(cls)) {
				throw new IllegalArgumentException(className + " is not a RunnableService");
			}
			this.configuration.register(cls);
			this.register((Class<? extends RunnableService>) cls);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Register a RunnableService to be started and stopped by this ServiceManager
	 * 
	 * @param cls
	 *            the RunnableService class
	 */
	public void register(Class<? extends RunnableService> cls) {
		if (this.running)
			throw new IllegalStateException("Service Manager is already running");
		this.classes.add(cls);
	}

	/**
	 * Start all registered RunnableServies
	 */
	public void start() {
		if (this.running)
			throw new IllegalStateException("Service Manager is already running");
		for (Class<? extends RunnableService> runnableService : this.classes) {
			RunnableService service = this.serviceLocator.getService(runnableService);
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
