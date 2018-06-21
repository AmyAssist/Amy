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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.di.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;

/**
 * Provide the plugin configuration properties as service
 * 
 * @author Leon Kiefer
 */
public class PropertiesProvider implements ServiceFunction<Properties> {

	@Override
	public Properties getService(Map<Class<?>, ServiceFactory<?>> resolvedDependencies, Map<String, ?> context) {
		ConfigurationLoader configurationLoader = (ConfigurationLoader) resolvedDependencies
				.get(ConfigurationLoader.class).build();
		IPlugin plugin = (IPlugin) context.get(Context.PLUGIN);
		String uniqueName = plugin.getUniqueName();
		return configurationLoader.load(uniqueName);
	}

	@Override
	public Collection<Class<?>> getDependencies() {
		return Collections.singleton(ConfigurationLoader.class);
	}

	@Override
	public Collection<String> getRequiredContextIdentifiers() {
		return Collections.singletonList(Context.PLUGIN);
	}
}
