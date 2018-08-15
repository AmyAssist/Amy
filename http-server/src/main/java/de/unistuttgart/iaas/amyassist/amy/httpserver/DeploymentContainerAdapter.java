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

package de.unistuttgart.iaas.amyassist.amy.httpserver;

import java.util.Set;

import javax.ws.rs.Path;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService;
import de.unistuttgart.iaas.amyassist.amy.deployment.DeploymentDescriptorUtil;

/**
 * An adapter to load resources from deployment descriptors and from the PluginManager.
 * 
 * @author Leon Kiefer
 */
@Service(DeploymentContainerAdapter.class)
public class DeploymentContainerAdapter implements DeploymentContainerService {

	private static final String SERVICE_DEPLOYMENT_DESCRIPTOR = "META-INF/" + Path.class.getName();

	@Reference
	private Server server;
	@Reference
	private PluginManager pluginManager;

	@Override
	public void deploy() {
		Set<Class<?>> all = DeploymentDescriptorUtil.getClasses(this.getClass().getClassLoader(),
				SERVICE_DEPLOYMENT_DESCRIPTOR);

		for (IPlugin plugin : this.pluginManager.getPlugins()) {
			all.addAll(DeploymentDescriptorUtil.getClasses(plugin.getClassLoader(), SERVICE_DEPLOYMENT_DESCRIPTOR));

			// TODO Breaking all Resources MUST be specified in a deployment descriptor
			for (Class<?> cls : plugin.getClasses()) {
				if (cls.isAnnotationPresent(Path.class)) {
					all.add(cls);
				}
			}
		}
		all.forEach(this.server::register);
	}
}
