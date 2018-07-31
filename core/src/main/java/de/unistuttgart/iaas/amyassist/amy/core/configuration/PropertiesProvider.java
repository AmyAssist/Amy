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

package de.unistuttgart.iaas.amyassist.amy.core.configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.unistuttgart.iaas.amyassist.amy.core.di.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ConsumerFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;

/**
 * Provide the plugin configuration properties as service
 * 
 * @author Leon Kiefer
 */
public class PropertiesProvider implements ServiceProvider<Properties> {

	private ServiceConsumer<ConfigurationLoader> consumer;

	public PropertiesProvider() {
		this.consumer = ConsumerFactory.build(null, Util.serviceDescriptionFor(ConfigurationLoader.class));
	}

	@Override
	public Properties getService(Map<ServiceConsumer<?>, ServiceFactory<?>> resolvedDependencies,
			Map<String, ?> context) {
		ConfigurationLoader configurationLoader = (ConfigurationLoader) resolvedDependencies.get(this.consumer).build();
		IPlugin plugin = (IPlugin) context.get(Context.PLUGIN);
		String uniqueName = plugin.getUniqueName();
		return configurationLoader.load(uniqueName);
	}

	@Override
	public Set<ServiceConsumer<?>> getDependencies() {
		return Collections.singleton(this.consumer);
	}

	@Override
	public Set<String> getRequiredContextIdentifiers() {
		return Collections.singleton(Context.PLUGIN);
	}

	@Override
	public void dispose(Properties properties) {
		// TODO maybe save the properties
	}
}
