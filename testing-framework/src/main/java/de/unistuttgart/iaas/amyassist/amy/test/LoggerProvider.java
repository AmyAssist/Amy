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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandle;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandleImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;

/**
 * Logger Service provider for tests
 * 
 * @author Leon Kiefer
 */
public class LoggerProvider implements ServiceProvider<Logger> {

	@Override
	public ServiceHandle<Logger> getService(ServiceLocator locator, ServiceConsumer<Logger> consumer) {
		Class<?> cls = consumer.getConsumerClass();
		return new ServiceHandleImpl<>(LoggerFactory.getLogger(cls));
	}

	@Override
	public void dispose(ServiceHandle<Logger> service) {
		// nothing to do here
	}
}
