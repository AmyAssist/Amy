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

import java.util.Set;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.PropertiesProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.Configuration;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceProviderLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.Services;
import de.unistuttgart.iaas.amyassist.amy.core.logger.LoggerProvider;

/**
 * Register the Services of Core
 * 
 * @author Leon Kiefer
 */
public class CoreServiceProviderLoader implements ServiceProviderLoader {

	@Override
	public void load(Configuration di) {
		Set<Class<?>> loadServices = new Services().loadServices(this.getClass().getClassLoader());
		loadServices.forEach(di::register);

		di.register(new LoggerProvider());
		di.register(new PropertiesProvider());
	}

}
