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

package io.github.amyassist.amy.core.pluginloader;

import asg.cliche.Command;
import io.github.amyassist.amy.core.Configuration;
import io.github.amyassist.amy.core.di.annotation.Reference;

/**
 * Command Line Interface for the PluginManager
 * 
 * @author Leon Kiefer
 */
public class PluginManagerCLI {
	@Reference
	private Configuration configuration;

	@Command(name = "plugin:list", description = "a list of all installed plugins",
			header = "Currently installed plugins are:")
	public String plugins() {
		return String.join("\n", this.configuration.getInstalledPlugins());
	}

	@Command(name = "plugin:version", description = "get the version of an installed plugin")
	public String pluginVersion(String pluginName) {
		return this.configuration.getPluginVersion(pluginName);
	}

	@Command(name = "plugin:description", description = "get the description of an installed plugin")
	public String pluginDescription(String pluginName) {
		return this.configuration.getPluginDescription(pluginName);
	}
}
