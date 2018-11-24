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

package io.github.amyassist.amy.core.configuration;

import java.util.Properties;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.annotation.Context;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * Implementation of {@link ConfigurationManager} that uses the {@link ConfigurationLoader} and the
 * {@link InternalDefaultConfigurationLoader}.
 * 
 * @author Leon Kiefer
 */
@Service
public class ConfigurationManagerImpl implements ConfigurationManager {
	@Reference
	private ConfigurationLoader configurationLoader;
	@Reference
	private InternalDefaultConfigurationLoader internalDefaultConfigurationLoader;
	@Reference
	private EnvironmentConfigurationLoader environmentConfiguratinLoader;
	@Context(io.github.amyassist.amy.core.di.Context.CLASSLOADER)
	private ClassLoader classLoader;

	@Override
	public @Nonnull Properties getConfiguration(String configurationName) {
		Properties p = this.configurationLoader.load(configurationName);
		if (this.environmentConfiguratinLoader.isAllowedName(configurationName)) {
			p = this.environmentConfiguratinLoader.load(configurationName, p);
		}
		return p;
	}

	@Override
	public @Nonnull Properties getConfigurationWithDefaults(String configurationName) {
		return this.getConfigurationWithDefaults(configurationName, this.classLoader);
	}

	@Override
	public @Nonnull Properties getConfigurationWithDefaults(String configurationName, ClassLoader loader) {
		Properties defaults = this.internalDefaultConfigurationLoader.load(loader, configurationName);
		Properties p = this.configurationLoader.load(configurationName, defaults);
		if (this.environmentConfiguratinLoader.isAllowedName(configurationName)) {
			p = this.environmentConfiguratinLoader.load(configurationName, p);
		}
		return p;
	}

}
