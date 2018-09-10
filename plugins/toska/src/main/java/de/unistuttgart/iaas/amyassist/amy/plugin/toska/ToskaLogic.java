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

package de.unistuttgart.iaas.amyassist.amy.plugin.toska;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.opentosca.containerapi.client.IOpenTOSCAContainerAPIClient;
import org.opentosca.containerapi.client.impl.OpenTOSCAContainerLegacyAPIClient;
import org.opentosca.containerapi.client.model.Application;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.WithDefault;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * TODO: Description
 * 
 * @author Felix Burk, Leon Kiefer
 */
@Service
public class ToskaLogic {

	@WithDefault
	@Reference
	private Properties configuration;

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
	 * 
	 */
	public void install() {
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		List<Application> applist = this.apiClient.getApplications();
		this.apiClient.createServiceInstance(applist.get(0), Collections.emptyMap());
	}

	public String getInstalledPlugins() {
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		List<Application> applist = this.apiClient.getApplications();

		List<String> apps = new ArrayList<>();
		applist.forEach(a -> apps.add(a.getDisplayName()));

		return String.join(", ", apps);
	}

}
