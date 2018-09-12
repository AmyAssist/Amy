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

package de.unistuttgart.iaas.amyassist.amy.test;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.ContextLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceImplementationDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.SimpleServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandle;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandleImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.runtime.ServiceImplementationDescriptionImpl;

/**
 * Logger Service provider for tests
 * 
 * @author Leon Kiefer
 */
public class LoggerProvider implements ServiceProvider<Logger> {

	private static final String KEY = "class";

	@Override
	public @Nonnull ServiceDescription<Logger> getServiceDescription() {
		return new ServiceDescriptionImpl<>(Logger.class);
	}

	@Override
	public ServiceImplementationDescription<Logger> getServiceImplementationDescription(@Nonnull ContextLocator locator,
			@Nonnull ServiceConsumer<Logger> serviceConsumer) {
		Class<?> cls = serviceConsumer.getConsumerClass();
		return new ServiceImplementationDescriptionImpl<>(serviceConsumer.getServiceDescription(),
				Collections.singletonMap(KEY, cls), LoggerFactory.class);
	}

	@Override
	public @Nonnull ServiceHandle<Logger> createService(@Nonnull SimpleServiceLocator locator,
			@Nonnull ServiceImplementationDescription<Logger> serviceImplementationDescription) {

		Class<?> cls = (Class<?>) serviceImplementationDescription.getContext().get(KEY);
		return new ServiceHandleImpl<>(LoggerFactory.getLogger(cls));
	}

	@Override
	public void dispose(ServiceHandle<Logger> service) {
		// nothing to do here
	}

}
