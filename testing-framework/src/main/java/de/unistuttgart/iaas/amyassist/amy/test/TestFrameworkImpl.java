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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;

import javax.ws.rs.Path;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceNotFoundException;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;

/**
 * The Implementation of the TestFramework
 * 
 * @author Leon Kiefer, Tim Neumann
 */
public class TestFrameworkImpl implements TestFramework {
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

	/**
	 * Create a new instance of the TestFramework
	 */
	public TestFrameworkImpl() {
		this.storage = Mockito.mock(Storage.class,
				Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS).useConstructor());

		this.dependencyInjection = new DependencyInjection();
		this.dependencyInjection.addExternalService(TestFramework.class, this);
		this.dependencyInjection.addExternalService(IStorage.class, this.storage);
		this.dependencyInjection.register(Logger.class, new LoggerProvider());

	}

	/**
	 * Prepares the server.
	 */
	public void prepareServer() {
		Properties serverConfig = new Properties();
		serverConfig.setProperty(Server.PROPERTY_PORT, "50080");
		serverConfig.setProperty(Server.PROPERTY_ROOT_PATH, "/test/");
		serverConfig.setProperty(Server.PROPERTY_LOCALHOST, "true");
		ConfigurationLoader configLoader = Mockito.mock(ConfigurationLoader.class);
		Mockito.when(configLoader.load(Server.CONFIG_NAME)).thenReturn(serverConfig);

		this.dependencyInjection.addExternalService(ConfigurationLoader.class, configLoader);
		this.dependencyInjection.register(Server.class);
		this.server = this.dependencyInjection.getService(Server.class);
	}

	/**
	 * 
	 * @param testInstance
	 *            the object of the test class
	 */
	public void setup(Object testInstance) {
		this.dependencyInjection.inject(testInstance);
	}

	/**
	 * call this before the test execution
	 */
	public void before() {
		if (this.server != null && !this.restResources.isEmpty()) {
			this.server.start(this.restResources.toArray(new Class<?>[this.restResources.size()]));
		}
	}

	/**
	 * call this after the test execution
	 */
	public void after() {
		if (this.server != null && this.server.isRunning()) {
			this.server.shutdown();
		}
	}

	@Override
	public IStorage storage() {
		return this.storage;
	}

	@Override
	public <T> T mockService(Class<T> serviceType) {
		T mock = Mockito.mock(serviceType);
		this.dependencyInjection.addExternalService(serviceType, mock);
		return mock;
	}

	@Override
	public <T> T setServiceUnderTest(Class<T> serviceClass) {
		if (Util.isValidServiceClass(serviceClass) && serviceClass.isAnnotationPresent(Service.class)) {
			this.dependencyInjection.register(serviceClass);
			try {
				return this.dependencyInjection.createAndInitialize(serviceClass);
			} catch (ServiceNotFoundException e) {
				throw new IllegalStateException("The dependencies of " + serviceClass.getName()
						+ " must be mocked with mockService before calling this method!", e);
			}
		}
		throw new IllegalArgumentException("Sevices must have a @Service annotation");
	}

	@Override
	public void setRESTResource(Class<?> resource) {
		if (this.server == null)
			throw new IllegalStateException("Test Framwork not setup with Server.");

		if (resource.isAnnotationPresent(Path.class)) {
			this.restResources.add(resource);
		} else
			throw new IllegalArgumentException("The Resource must have a @Path annotation");
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.test.TestFramework#getServerBaseURI()
	 */
	@Override
	public URI getServerBaseURI() {
		if (this.server == null)
			throw new IllegalStateException("Test Framwork not setup with Server.");
		return this.server.getBaseUri();
	}
}
