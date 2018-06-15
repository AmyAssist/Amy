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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

import javax.ws.rs.Path;

import org.mockito.Mockito;
import org.slf4j.bridge.SLF4JBridgeHandler;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;

/**
 * A Framework to test plugins
 * 
 * @author Leon Kiefer
 */
public class TestFramework {

	static {
		LogManager.getLogManager().reset();
		SLF4JBridgeHandler.install();
		// workaround for
		// https://github.com/cmusphinx/sphinx4/blob/master/sphinx4-core/src/main/java/edu/cmu/sphinx/util/props/ConfigurationManagerUtils.java#L138
		System.setProperty("java.util.logging.config.file", "");
	}

	private IStorage storage;
	private DependencyInjection dependencyInjection;
	private Server server;
	private List<Class<?>> restResources = new ArrayList<>();

	public TestFramework() {
		this.storage = Mockito.mock(Storage.class,
				Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS).useConstructor());
		this.dependencyInjection = new DependencyInjection();
		this.dependencyInjection.addExternalService(TestFramework.class, this);
		this.dependencyInjection.addExternalService(IStorage.class, this.storage);
		this.dependencyInjection.register(Server.class);
	}

	void setup(Object testInstance) {
		this.dependencyInjection.inject(testInstance);
	}

	public void before() {
		if (!this.restResources.isEmpty()) {
			this.server = this.dependencyInjection.getService(Server.class);
			this.server.start(this.restResources.toArray(new Class<?>[this.restResources.size()]));
		}

	}

	public void after() {
		if (this.server != null) {
			this.server.shutdown();
		}
	}

	public IStorage storage() {
		return this.storage;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection#register(Class)
	 */
	public void register(Class<?> cls) {
		this.dependencyInjection.register(cls);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection#getService(Class)
	 */
	public <T> T get(Class<T> serviceType) {
		return this.dependencyInjection.getService(serviceType);
	}

	/**
	 * create a mock for the serviceType and bind it in the DI.
	 * 
	 * @param serviceType
	 * @return the service mock
	 */
	public <T> T mockService(Class<T> serviceType) {
		T mock = Mockito.mock(serviceType);
		this.dependencyInjection.addExternalService(serviceType, mock);
		return mock;
	}

	/**
	 * specify the Service Under Test
	 * 
	 * @param serviceClass
	 *            the class to be tested
	 * @return the ServiceUnderTest
	 */
	public <T> T setServiceUnderTest(Class<T> serviceClass) {
		if (serviceClass.isAnnotationPresent(Service.class)) {
			this.dependencyInjection.register(serviceClass);
			return this.dependencyInjection.create(serviceClass);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * specify the Rest resource
	 * 
	 * @param resource
	 *            the class of the Rest resource
	 */
	public void setRESTResource(Class<?> resource) {
		if (resource.isAnnotationPresent(Path.class)) {
			this.restResources.add(resource);
		} else {
			throw new IllegalArgumentException();
		}
	}

}
