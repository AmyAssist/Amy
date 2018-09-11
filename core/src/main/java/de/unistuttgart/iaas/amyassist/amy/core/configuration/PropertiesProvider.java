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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.di.*;
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

	private static final String CONTEXT_WITH_DEFAULT = "WithDefault";

	@Override
	public @Nonnull ServiceDescription<Properties> getServiceDescription() {
		return new ServiceDescriptionImpl<>(Properties.class);
	}

	@Override
	public ServiceImplementationDescription<Properties> getServiceImplementationDescription(
			@Nonnull ContextLocator locator, @Nonnull ServiceConsumer<Properties> serviceConsumer) {
		Map<String, Object> context = new HashMap<>();
		context.put(Context.PLUGIN, locator.getContextProvider(Context.PLUGIN).getContext(serviceConsumer));
		context.put(CONTEXT_WITH_DEFAULT, serviceConsumer.getServiceDescription().getAnnotations().stream()
				.anyMatch(a -> a instanceof WithDefault));
		return new ServiceImplementationDescriptionImpl<>(serviceConsumer.getServiceDescription(), context,
				Properties.class);
	}

	@Override
	public @Nonnull ServiceHandle<Properties> createService(@Nonnull SimpleServiceLocator locator,
			@Nonnull ServiceImplementationDescription<Properties> serviceImplementationDescription) {
		ConfigurationManager configurationLoader = locator.getService(ConsumerFactory.build(PropertiesProvider.class,
				new ServiceDescriptionImpl<>(ConfigurationManager.class))).getService();
		IPlugin plugin = (IPlugin) serviceImplementationDescription.getContext().get(Context.PLUGIN);
		String uniqueName = plugin.getUniqueName();
		boolean withDefault = (boolean) serviceImplementationDescription.getContext().get(CONTEXT_WITH_DEFAULT);

		if (withDefault) {
			ClassLoader classLoader = plugin.getClassLoader();
			return new ServiceHandleImpl<>(configurationLoader.getConfigurationWithDefaults(uniqueName, classLoader));
		}
		return new ServiceHandleImpl<>(configurationLoader.getConfiguration(uniqueName));
	}

	@Override
	public void dispose(ServiceHandle<Properties> properties) {
		// TODO maybe save the properties
	}

}
