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

package io.github.amyassist.amy.logger;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.amyassist.amy.core.di.ContextLocator;
import io.github.amyassist.amy.core.di.ServiceDescription;
import io.github.amyassist.amy.core.di.ServiceInstantiationDescription;
import io.github.amyassist.amy.core.di.SimpleServiceLocator;
import io.github.amyassist.amy.core.di.consumer.ServiceConsumer;
import io.github.amyassist.amy.core.di.provider.ServiceProvider;
import io.github.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;
import io.github.amyassist.amy.core.di.runtime.ServiceInstantiationDescriptionImpl;

/**
 * The Logger Provider for all Services
 * 
 * @author Leon Kiefer
 */
public class LoggerProvider implements ServiceProvider<Logger> {
	
	private static final String CONTEXT_CLASS = "class";

	@Override
	public @Nonnull ServiceDescription<Logger> getServiceDescription() {
		return new ServiceDescriptionImpl<>(Logger.class);
	}

	@Override
	public ServiceInstantiationDescription<Logger> getServiceInstantiationDescription(@Nonnull ContextLocator locator,
			@Nonnull ServiceConsumer<Logger> serviceConsumer) {
		return new ServiceInstantiationDescriptionImpl<>(serviceConsumer.getServiceDescription(),
				Collections.singletonMap(CONTEXT_CLASS, serviceConsumer.getConsumerClass()), LoggerFactory.class);
	}

	@Override
	public @Nonnull Logger createService(@Nonnull SimpleServiceLocator locator,
			@Nonnull ServiceInstantiationDescription<Logger> serviceInstantiationDescription) {
		Class<?> cls = (Class<?>) serviceInstantiationDescription.getContext().get(CONTEXT_CLASS);
		return LoggerFactory.getLogger(cls);
	}

	@Override
	public void dispose(@Nonnull Logger service,
			@Nonnull ServiceInstantiationDescription<Logger> serviceInstantiationDescription) {
		// Logger MUST NOT be disposed
	}

}
