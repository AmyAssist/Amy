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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opentosca.containerapi.client.IOpenTOSCAContainerAPIClient;
import org.opentosca.containerapi.client.impl.OpenTOSCAContainerLegacyAPIClient;
import org.opentosca.containerapi.client.model.Application;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.WithDefault;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskScheduler;

/**
 * The tosca logic ckass
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

	/**
	 * internal toska client
	 */
	private IOpenTOSCAContainerAPIClient apiClient;

	@PostConstruct
	private void connect() {
		String containerHost = this.configuration.getProperty("CONTAINER_HOST");
		String containerHostInternal = this.configuration.getProperty("CONTAINER_HOST_INTERNAL");
		this.apiClient = new OpenTOSCAContainerLegacyAPIClient(containerHost, containerHostInternal);
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
