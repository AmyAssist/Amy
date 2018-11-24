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

package io.github.amyassist.amy.core.di.provider;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.ServiceDescription;
import io.github.amyassist.amy.core.di.ServiceLocator;
import io.github.amyassist.amy.core.di.SimpleServiceLocator;
import io.github.amyassist.amy.core.di.consumer.ServiceConsumer;
import io.github.amyassist.amy.core.di.consumer.ServiceConsumerImpl;
import io.github.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;
import io.github.amyassist.amy.core.di.util.ServiceLocatorUtil;
import io.github.amyassist.amy.core.di.util.Util;

/**
 * Facade for the {@link SimpleServiceLocator}
 * 
 * @author Leon Kiefer
 */
class InjectableServiceLocator implements ServiceLocator {
	@Nonnull
	private final SimpleServiceLocator locator;
	@Nonnull
	private final Class<?> consumerClass;

	/**
	 * @param locator
	 *            the simple service locator used to resolve Services.
	 * @param consumerClass
	 *            the class this instance is injected in
	 */
	public InjectableServiceLocator(@Nonnull SimpleServiceLocator locator, @Nonnull Class<?> consumerClass) {
		this.locator = locator;
		this.consumerClass = consumerClass;
	}

	@Override
	public <T> T createAndInitialize(@Nonnull Class<T> serviceClass) {
		return ServiceLocatorUtil.createAndInitialize(serviceClass, this.locator);
	}

	@Override
	public <T> T getService(Class<T> serviceType) {
		return this.getService(new ServiceDescriptionImpl<>(serviceType)).getService();
	}

	@Override
	public <T> ServiceHandle<T> getService(ServiceDescription<T> serviceDescription) {
		return this.getService(new ServiceConsumerImpl<>(this.consumerClass, serviceDescription));
	}

	@Override
	public <T> ServiceHandle<T> getService(@Nonnull ServiceConsumer<T> serviceConsumer) {
		return this.locator.getService(serviceConsumer);
	}

	@Override
	public void inject(@Nonnull Object injectMe) {
		ServiceLocatorUtil.inject(injectMe, this.locator);
	}

	@Override
	public void postConstruct(@Nonnull Object postConstructMe) {
		Util.postConstruct(postConstructMe);
	}

	@Override
	public void preDestroy(@Nonnull Object destroyMe) {
		Util.preDestroy(destroyMe);
	}

	@Override
	public void shutdown() {
		throw new UnsupportedOperationException("InjectableServiceLocator can't be shutdown!");
	}

}
