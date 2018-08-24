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

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;

/**
 * Implementation of {@link Configuration}
 * 
 * @author Leon Kiefer
 */
@Service
public class ConfigurationImpl implements Configuration {

	@Reference
	private PluginManager pluginManager;

	@Override
	public String[] getInstalledPlugins() {
		Set<String> pluginNames = this.pluginManager.getPlugins().stream().map(IPlugin::getUniqueName)
				.collect(Collectors.toSet());
		return pluginNames.toArray(new String[pluginNames.size()]);
	}

	@Override
	public String getPluginVersion(String pluginName) {
		return this.getPlugin(pluginName).map(IPlugin::getVersion).orElse(null);
	}

	@Override
	public String getPluginDescription(String pluginName) {
		return this.getPlugin(pluginName).map(IPlugin::getDescription).orElse(null);
	}

	private Optional<IPlugin> getPlugin(String pluginName) {
		return this.pluginManager.getPlugins().stream().filter(plugin -> plugin.getUniqueName().equals(pluginName))
				.findFirst();
	}

}
