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

package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;

/**
 * A ContextProvider for Plugin informations
 * 
 * @author Leon Kiefer
 */
public class PluginProvider implements StaticProvider<IPlugin> {

	private final Logger logger = LoggerFactory.getLogger(PluginProvider.class);

	private Collection<IPlugin> plugins;

	public PluginProvider(Collection<IPlugin> plugins) {
		this.plugins = plugins;
	}

	@Override
	public IPlugin getContext(Class<?> consumer) {
		ClassLoader classLoader = consumer.getClassLoader();
		for (IPlugin p : this.plugins) {
			if (classLoader.equals(p.getClassLoader())) {
				return p;
			}
		}
		this.logger.error("The class {} does not seem to belong to any plugin.", consumer.getName());
		return null;
	}

}
