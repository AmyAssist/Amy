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

package de.unistuttgart.iaas.amyassist.amy.core.console;

import java.util.Set;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService;
import de.unistuttgart.iaas.amyassist.amy.deployment.DeploymentDescriptorUtil;

/**
 * The adapter that deploy all Console commands.
 * 
 * @author Leon Kiefer
 */
@Service(ConsoleDeploymentAdapter.class)
public class ConsoleDeploymentAdapter implements DeploymentContainerService {
	private static final String DEPLOYMENT_DESCRIPTOR = "META-INF/" + Console.class.getName();

	@Reference
	private Console console;

	@Reference
	private PluginManager pluginManager;

	@Reference
	private ServiceLocator serviceLocator;

	@Override
	public void deploy() {
		Set<Class<?>> all = DeploymentDescriptorUtil.getClasses(this.getClass().getClassLoader(),
				DEPLOYMENT_DESCRIPTOR);

		for (IPlugin plugin : this.pluginManager.getPlugins()) {
			all.addAll(DeploymentDescriptorUtil.getClasses(plugin.getClassLoader(), DEPLOYMENT_DESCRIPTOR));
		}
		all.forEach(this::register);
	}

	private void register(Class<?> cls) {
		this.console.register(this.serviceLocator.createAndInitialize(cls));
	}

}
