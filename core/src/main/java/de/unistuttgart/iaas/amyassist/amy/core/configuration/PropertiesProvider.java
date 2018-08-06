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
import java.util.Properties;

import de.unistuttgart.iaas.amyassist.amy.core.di.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescriptionImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceImplementationDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.SimpleServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ConsumerFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandle;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandleImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceImplementationDescriptionImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;

/**
 * Provide the plugin configuration properties as service
 * 
 * @author Leon Kiefer
 */
public class PropertiesProvider implements ServiceProvider<Properties> {
	@Override
	public ServiceDescription<Properties> getServiceDescription() {
		return new ServiceDescriptionImpl<>(Properties.class);
	}

	@Override
	public ServiceImplementationDescription<Properties> getServiceImplementationDescription(
			SimpleServiceLocator locator, ServiceConsumer<Properties> serviceConsumer) {
		IPlugin plugin = (IPlugin) locator.getContextProvider(Context.PLUGIN)
				.getContext(serviceConsumer.getConsumerClass());
		return new ServiceImplementationDescriptionImpl<>(serviceConsumer.getServiceDescription(),
				Collections.singletonMap(Context.PLUGIN, plugin), Properties.class);
	}

	@Override
	public ServiceHandle<Properties> createService(SimpleServiceLocator locator,
			ServiceImplementationDescription<Properties> serviceImplementationDescription) {
		ConfigurationLoader configurationLoader = locator.getService(ConsumerFactory.build(PropertiesProvider.class,
				new ServiceDescriptionImpl<>(ConfigurationLoader.class))).getService();
		IPlugin plugin = (IPlugin) serviceImplementationDescription.getContext().get(Context.PLUGIN);
		String uniqueName = plugin.getUniqueName();
		return new ServiceHandleImpl<>(configurationLoader.load(uniqueName));
	}

	@Override
	public void dispose(ServiceHandle<Properties> properties) {
		// TODO maybe save the properties
	}

}
