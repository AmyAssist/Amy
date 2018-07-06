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

import java.util.Properties;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoaderImpl;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.PropertiesProvider;
import de.unistuttgart.iaas.amyassist.amy.core.console.Console;
import de.unistuttgart.iaas.amyassist.amy.core.di.Configuration;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceProviderLoader;
import de.unistuttgart.iaas.amyassist.amy.core.io.EnvironmentService;
import de.unistuttgart.iaas.amyassist.amy.core.logger.LoggerProvider;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.PersistenceService;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.storage.DatabaseStorage;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManagerService;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommandHandler;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;

/**
 * Register the Services of Core
 * 
 * @author Leon Kiefer
 */
public class CoreServiceProviderLoader implements ServiceProviderLoader {

	@Override
	public void load(Configuration di) {
		di.register(Logger.class, new LoggerProvider());
		di.register(Properties.class, new PropertiesProvider());

		di.register(Server.class);
		di.register(ConfigurationImpl.class);
		di.register(Console.class);
		di.register(SpeechCommandHandler.class);
		di.register(ConfigurationLoaderImpl.class);
		di.register(PluginLoader.class);
		di.register(PluginManagerService.class);
		di.register(EnvironmentService.class);
		di.register(DatabaseStorage.class);
		di.register(PersistenceService.class);
		di.register(NaturalLanaguageInputHandlerService.class);
	}

}
