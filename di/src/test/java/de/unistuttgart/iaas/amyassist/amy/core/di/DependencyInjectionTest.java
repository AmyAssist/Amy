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

package de.unistuttgart.iaas.amyassist.amy.core.di;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static uk.org.lidalia.slf4jtest.LoggingEvent.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Test for DependencyInjection
 * 
 * @author Leon Kiefer
 */
class DependencyInjectionTest {

	private DependencyInjection dependencyInjection;

	@BeforeEach
	public void setup() {
		this.dependencyInjection = new DependencyInjection();
		this.dependencyInjection.register(Service1.class);
		this.dependencyInjection.register(Service2.class);
		this.dependencyInjection.register(Service3.class);
	}

	@Test
	void testServiceAnnotation() {
		Service1 service1 = this.dependencyInjection.getService(Service1.class);
		assertThat(service1, is(instanceOf(Service1.class)));
	}

	@Test
	void testDependencyInjection() {
		Service2 service2 = this.dependencyInjection.getService(Service2.class);
		assertThat(service2.checkServices(), is(true));
	}

	@Test
	void testServiceRegistry() {
		Service1 s1 = this.dependencyInjection.getService(Service1.class);
		Service1 s2 = this.dependencyInjection.getService(Service1.class);
		assertThat(s1, theInstance(s2));
	}

	@Test
	void testCircularDependencies() {
		this.dependencyInjection.register(Service4.class);
		this.dependencyInjection.register(Service5.class);

		assertThrows(RuntimeException.class, () -> this.dependencyInjection.getService(Service4.class));
	}

	@Test()
	void testRegisterNotAService() {
		String message = assertThrows(ClassIsNotAServiceException.class,
				() -> this.dependencyInjection.register(NotAService.class)).getMessage();

		assertThat(message, equalTo("The class " + NotAService.class.getName() + " is not a Service"));
	}

	@Test()
	void testServiceNotFoundException() {
		this.dependencyInjection.register(Service6.class);
		String message = assertThrows(ServiceNotFoundException.class,
				() -> this.dependencyInjection.getService(Service6.class)).getMessage();

		assertThat(message, equalTo(
				"The Service " + Service7API.class.getName() + " is not registered in the DI or do not exists."));
	}

	@Test()
	void testDuplicateServiceException() {
		this.dependencyInjection.register(Service6.class);
		assertThrows(DuplicateServiceException.class, () -> this.dependencyInjection.register(Service6.class));
	}

	@Test()
	void testDuplicateServiceException2() {
		this.dependencyInjection.addExternalService(Service7API.class, new Service7());
		assertThrows(DuplicateServiceException.class,
				() -> this.dependencyInjection.addExternalService(Service7API.class, new Service7()));
	}

	@Test()
	void testConstructorCheck() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> this.dependencyInjection.register(ServiceWithConstructor.class)).getMessage();

		assertThat(message, equalTo("There is a problem with the class " + ServiceWithConstructor.class.getName()
				+ ". It can't be used as a Service"));
	}

	@Test()
	void testServiceWithDuplicateDependency() {
		TestLogger logger = TestLoggerFactory.getTestLogger(ServiceProvider.class);

		this.dependencyInjection.register(ServiceWithDuplicateDependency.class);

		assertThat(logger.getLoggingEvents(), contains(warn("The Service {} have a duplicate dependeny on {}",
				ServiceWithDuplicateDependency.class.getName(), Service6.class.getName())));
	}

	@Test()
	void testPostConstruct() {
		Service1 service1 = this.dependencyInjection.getService(Service1.class);
		assertThat(service1.init, is(1));
		this.dependencyInjection.postConstruct(service1);
		assertThat(service1.init, is(2));
	}

	@Test()
	void testCreateAndInitializeService() {
		Service2 s2_1 = this.dependencyInjection.getService(Service2.class);
		Service2 s2_2 = this.dependencyInjection.createAndInitialize(Service2.class);
		assertThat(s2_1, not(theInstance(s2_2)));
		assertThat(s2_1.getService3(), theInstance(s2_2.getService3()));
	}

	@Test()
	void testCreateService() {
		Service2 s2 = this.dependencyInjection.create(Service2.class);
		assertThat(s2.getService3(), nullValue());

		Service1 service1 = this.dependencyInjection.create(Service1.class);
		assertThat(service1.init, is(0));
	}

	@Test()
	void testCreateNotAService() {
		NotAService2 nas = this.dependencyInjection.createAndInitialize(NotAService2.class);
		assertThat(nas.getInit(), is(1));
		Service1 s1 = this.dependencyInjection.getService(Service1.class);
		this.dependencyInjection.postConstruct(s1);
		assertThat(nas.getInit(), is(2));
		NotAService2 nas2 = this.dependencyInjection.createAndInitialize(NotAService2.class);
		assertThat(nas2, not(theInstance(nas)));
	}

	@Test()
	void testCreateIllegalAccessException() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> this.dependencyInjection.create(Service8.class)).getMessage();

		assertThat(message, equalTo(
				"There is a problem with the class " + Service8.class.getName() + ". It can't be used as a Service"));
	}

	@Test()
	void testExternalService() {
		Service7 service7 = new Service7();
		this.dependencyInjection.addExternalService(Service7API.class, service7);
		assertThat(this.dependencyInjection.getService(Service7API.class), is(theInstance(service7)));
	}

	@Test()
	void testAbstractService() {
		assertThrows(IllegalArgumentException.class, () -> this.dependencyInjection.register(AbstractService.class));
	}

	@Test()
	void testInject() {
		Service10 service10 = new Service10(10);
		this.dependencyInjection.inject(service10);
		assertThat(service10.isInit(), is(false));
		assertThat(service10.getService1(), is(notNullValue()));
		assertThat(service10.getService1(), is(instanceOf(Service1.class)));
	}

	@Test()
	void testServiceType() {
		assertThrows(IllegalArgumentException.class, () -> this.dependencyInjection.register(Service17.class));
	}

	@Test()
	void testCreateAndInitialize() {
		Service1 service1 = this.dependencyInjection.createAndInitialize(Service1.class);
		assertThat(service1, notNullValue());
		assertThat(service1.init, is(1));
	}

	@AfterEach
	public void clearLoggers() {
		TestLoggerFactory.clear();
	}

}
