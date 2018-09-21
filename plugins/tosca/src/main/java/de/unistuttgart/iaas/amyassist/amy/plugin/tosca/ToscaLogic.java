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

package de.unistuttgart.iaas.amyassist.amy.plugin.tosca;

import java.util.*;

import org.opentosca.containerapi.client.IOpenTOSCAContainerAPIClient;
import org.opentosca.containerapi.client.model.Application;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.WithDefault;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import de.unistuttgart.iaas.amyassist.amy.plugin.tosca.configurations.ConfigurationEntry;
import de.unistuttgart.iaas.amyassist.amy.plugin.tosca.configurations.ConfigurationRegistry;

/**
 * The tosca logic class
 * 
 * @author Felix Burk, Leon Kiefer, Tim Neumann
 */
@Service
public class ToscaLogic {

	@WithDefault
	@Reference
	private Properties configuration;

	@Reference
	private TaskScheduler scheduler;

	@Reference
	private ConfigurationRegistry registry;

	@Reference
	private ToscaLibraryAdapter adapter;

	/**
	 * internal tosca client
	 */
	private IOpenTOSCAContainerAPIClient apiClient;

	@PostConstruct
	private void connect() {
		String containerHost = this.configuration.getProperty("CONTAINER_HOST");
		String containerHostInternal = this.configuration.getProperty("CONTAINER_HOST_INTERNAL");
		this.apiClient = this.adapter.createLibrary(containerHost, containerHostInternal);
	}

	/**
	 * @return All registered configuration entrys.
	 */
	public List<ConfigurationEntry> getConfigurations() {
		return this.registry.getAll();
	}

	/**
	 * Installs the application with the given name, using the parameters in the configuration with the given name
	 * 
	 * @param appName
	 *            The name of the application to install
	 * @param configurationName
	 *            The name of the configuration to use
	 * @throws IllegalArgumentException
	 *             When the configuration does not contain all required keys.
	 * @throws NoSuchElementException
	 *             When no app with the given name is installed.
	 */
	public void install(String appName, String configurationName) {
		List<Application> apps = getInstalledApps();
		for (Application app : apps) {
			if (app.getDisplayName().equalsIgnoreCase(appName)) {
				install(app, configurationName);
				return;
			}
		}
		throw new NoSuchElementException("No app with this name is installed.");
	}

	/**
	 * Installs the given application with the parameters in the configuration with the given name
	 * 
	 * @param app
	 *            The application to install
	 * @param conigurationName
	 *            The name of the configuration to use
	 * @throws IllegalArgumentException
	 *             When the configuration does not contain all required keys.
	 */
	public void install(Application app, String conigurationName) {
		List<String> keys = app.getInputParameters();
		Map<String, String> configurations = new HashMap<>();
		getConfigurations().stream().filter(c -> c.getTag().equals(conigurationName))
				.forEach(c -> configurations.put(c.getKey().toLowerCase(), c.getValue()));
		Map<String, String> parameters = new HashMap<>();
		for (String key : keys) {
			if (!configurations.containsKey(key.toLowerCase()))
				throw new IllegalArgumentException(
						"The configuration with the given name does not have all required keys.");
			parameters.put(key, configurations.get(key.toLowerCase()));
		}
		install(app, parameters);
	}

	/**
	 * Installs the given application with the given parameters
	 * 
	 * @param app
	 *            The application to install
	 * @param parameters
	 *            The parameters to use
	 */
	public void install(Application app, Map<String, String> parameters) {
		this.scheduler.execute(() -> installWait(app, parameters));
	}

	private void installWait(Application app, Map<String, String> parameters) {
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		this.apiClient.createServiceInstance(app, parameters);
	}

	/**
	 * Get a list of installed apps.
	 * 
	 * @return A list of installed apps.
	 */
	public List<Application> getInstalledApps() {
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		return this.apiClient.getApplications();
	}

}
