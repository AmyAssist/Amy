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

package io.github.amyassist.amy.core.di;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.amyassist.amy.core.di.context.provider.CustomProvider;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Tests the dependency injection scopes.
 * 
 * @author Tim Neumann
 */
public class DependencyInjectionScopeTest {
	private DependencyInjection dependencyInjection;
	private Configuration configuration;
	private ServiceLocator serviceLocator;

	/**
	 * Setup
	 */
	@BeforeEach
	public void setup() {
		this.dependencyInjection = new DependencyInjection();
		this.configuration = this.dependencyInjection.getConfiguration();
		this.serviceLocator = this.dependencyInjection.getServiceLocator();
	}

	// Scope GLOBAL and once are already being tested by the other test.

	/**
	 * Scope DI for scope class.
	 */
	@Test
	public void testScopeClass() {
		this.configuration.register(Service11.class);
		this.configuration.register(Service12.class);
		this.configuration.register(Service13.class);

		Service11 s1 = this.serviceLocator.getService(Service11.class);
		s1.s.id = 4;

		Service13 s2 = this.serviceLocator.getService(Service13.class);
		assertThat(s1.s, not(theInstance(s2.s1)));
		assertThat(s1, theInstance(s2.s2));
		assertThat(s1.s, theInstance(s2.s2.s));
	}

	/**
	 * Scope DI for scope plugin.
	 */
	@Test
	public void testScopePlugin() {
		this.configuration.register(Service14.class);
		this.configuration.register(Service15.class);
		this.configuration.register(Service16.class);
		this.configuration.register(ServiceForPlugins.class);

		Map<Class<?>, Integer> plugins = new HashMap<>();
		this.configuration.registerContextProvider("custom", new CustomProvider<>(plugins));

		Class<?> cls1 = Service14.class;
		Class<?> cls2 = Service15.class;
		Class<?> cls3 = Service16.class;

		plugins.put(cls1, 1);
		plugins.put(cls2, 1);
		plugins.put(cls3, 2);

		Service14 s1 = this.serviceLocator.getService(Service14.class);
		Service15 s2 = this.serviceLocator.getService(Service15.class);

		Service16 s3 = this.serviceLocator.getService(Service16.class);

		assertThat(s1.s, theInstance(s2.s));
		assertThat(s1.s, not(theInstance(s3.s)));
	}

	@Test
	public void testContextValue() {
		this.configuration.register(Service11.class);
		this.configuration.register(Service12.class);
		this.configuration.register(Service13.class);

		Service11 s1 = this.serviceLocator.getService(Service11.class);
		assertThat(s1.s.getConsumerClass(), notNullValue());
		assertThat(s1.s.getConsumerClass(), equalTo(Service11.class));
	}

	@Test
	public void testInjectContextValue() {
		this.configuration.register(Service12.class);

		Service11 s1 = new Service11();

		this.serviceLocator.inject(s1);
		assertThat(s1.s.getConsumerClass(), notNullValue());
		assertThat(s1.s.getConsumerClass(), equalTo(Service11.class));
	}

	@Test
	public void testNoContextProvider() {
		this.configuration.register(Service16.class);
		this.configuration.register(ServiceForPlugins.class);

		String message = assertThrows(NoSuchElementException.class,
				() -> this.serviceLocator.getService(Service16.class)).getMessage();
		assertThat(message, equalTo("custom"));
	}

	@Test
	public void testContextProviderWithAbstractClass() {
		this.configuration.register(Service12.class);
		this.configuration.register(ServiceTemplate1.class);

		ServiceTemplate1 service = this.serviceLocator.getService(ServiceTemplate1.class);
		assertThat(service.getService12(), is(notNullValue()));
		assertThat(service.getService12FromAbstractServiceTemplate(), is(notNullValue()));

		assertThat(service.getService12().getConsumerClass(), is(equalTo(ServiceTemplate1.class)));
		assertThat(service.getService12FromAbstractServiceTemplate().getConsumerClass(),
				is(equalTo(AbstractServiceTemplate.class)));
		assertThat(service.getService12(), is(not(theInstance(service.getService12FromAbstractServiceTemplate()))));
	}

	/**
	 * Clear loggers
	 */
	@AfterEach
	public void clearLoggers() {
		TestLoggerFactory.clear();
	}
}
