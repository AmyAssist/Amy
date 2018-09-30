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

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.di.exception.ClassIsNotAServiceException;
import de.unistuttgart.iaas.amyassist.amy.core.di.exception.DuplicateServiceException;
import de.unistuttgart.iaas.amyassist.amy.core.di.exception.ServiceNotFoundException;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.SingletonServiceProvider;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Test for DependencyInjection
 * 
 * @author Leon Kiefer
 */
class DependencyInjectionTest {

	private DependencyInjection dependencyInjection;
	private Configuration configuration;
	private ServiceLocator serviceLocator;

	@BeforeEach
	public void setup() {
		this.dependencyInjection = new DependencyInjection();
		this.configuration = this.dependencyInjection.getConfiguration();
		this.serviceLocator = this.dependencyInjection.getServiceLocator();
		this.configuration.register(Service1.class);
		this.configuration.register(Service2.class);
		this.configuration.register(Service3.class);
	}

	@Test
	void testServiceAnnotation() {
		Service1 service1 = this.serviceLocator.getService(Service1.class);
		assertThat(service1, is(instanceOf(Service1.class)));
	}

	@Test
	void testDependencyInjection() {
		Service2 service2 = this.serviceLocator.getService(Service2.class);
		assertThat(service2.checkServices(), is(true));
	}

	@Test
	void testServiceRegistry() {
		Service1 s1 = this.serviceLocator.getService(Service1.class);
		Service1 s2 = this.serviceLocator.getService(Service1.class);
		assertThat(s1, theInstance(s2));
	}

	@Test
	void testCircularDependencies() {
		this.configuration.register(Service4.class);
		this.configuration.register(Service5.class);

		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			assertThrows(RuntimeException.class, () -> this.serviceLocator.getService(Service4.class));
		});
	}

	@Test()
	void testRegisterNotAService() {
		String message = assertThrows(ClassIsNotAServiceException.class,
				() -> this.configuration.register(NotAService.class)).getMessage();

		assertThat(message, equalTo("The class " + NotAService.class.getName() + " is not a Service"));
	}

	@Test()
	void testServiceNotFoundException() {
		this.configuration.register(Service6.class);
		String message = assertThrows(ServiceNotFoundException.class,
				() -> this.serviceLocator.getService(Service6.class)).getMessage();

		assertThat(message, equalTo("No Service of type " + Service7API.class.getName()
				+ " with qualifier [] is registered in the DI." + "\nRequired by:"
				+ "\nde.unistuttgart.iaas.amyassist.amy.core.di.Service6\n└── de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection"
				+ "\nSo first make sure you use the Service type and not the Service implementation to find the service."
				+ "\nIs " + Service7API.class.getName() + " the type of the Service?"
				+ "\nIf not you MUST change the JavaType of the Reference to match the Service type to get the Service."
				+ "\nElse make sure there is a Service implementation for the Service type "
				+ Service7API.class.getName() + " registered in the DI."
				+ "\nFirst step find the implementation of the Service."
				+ "\nSecond check if the type of the Service is " + Service7API.class.getName()
				+ "\nif you use the @Service read the JavaDoc to find out how to set the correct type of the Service."
				+ "\nThird check if the Service implemenatin is loaded either by using a deployment descriptor or by a programmatic call."));
	}

	@Test()
	void testDuplicateServiceException() {
		this.configuration.register(Service6.class);
		assertThrows(DuplicateServiceException.class, () -> this.configuration.register(Service6.class));
	}

	@Test()
	void testDuplicateServiceException2() {
		this.configuration.register(new SingletonServiceProvider<>(Service7API.class, new Service7()));
		assertThrows(DuplicateServiceException.class,
				() -> this.configuration.register(new SingletonServiceProvider<>(Service7API.class, new Service7())));
	}

	@Test()
	void testConstructorCheck() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> this.configuration.register(ServiceWithConstructor.class)).getMessage();

		assertThat(message, equalTo("There is a problem with the class " + ServiceWithConstructor.class.getName()
				+ ". It can't be used as a Service"));
	}

	@Test()
	void testServiceWithDuplicateDependency() {
		TestLogger logger = TestLoggerFactory.getTestLogger(ServiceProvider.class);

		this.configuration.register(ServiceWithDuplicateDependency.class);
		ServiceWithDuplicateDependency service = this.serviceLocator.getService(ServiceWithDuplicateDependency.class);
		assertThat(service, is(notNullValue()));
	}

	@Test()
	void testPostConstruct() {
		Service1 service1 = this.serviceLocator.getService(Service1.class);
		assertThat(service1.init, is(1));
		this.serviceLocator.postConstruct(service1);
		assertThat(service1.init, is(2));
	}

	@Test()
	void testCreateAndInitializeService() {
		Service2 s2_1 = this.serviceLocator.getService(Service2.class);
		Service2 s2_2 = this.serviceLocator.createAndInitialize(Service2.class);
		assertThat(s2_1, not(theInstance(s2_2)));
		assertThat(s2_1.getService3(), theInstance(s2_2.getService3()));
	}

	@Test()
	void testCreateAndInitializeServicePostConstruct() {
		Service18 service18 = this.serviceLocator.createAndInitialize(Service18.class);
		assertThat(service18.setup, is(true));
	}

	@Test()
	void testCreateNotAService() {
		NotAService2 nas = this.serviceLocator.createAndInitialize(NotAService2.class);
		assertThat(nas.getInit(), is(1));
		Service1 s1 = this.serviceLocator.getService(Service1.class);
		this.serviceLocator.postConstruct(s1);
		assertThat(nas.getInit(), is(2));
		NotAService2 nas2 = this.serviceLocator.createAndInitialize(NotAService2.class);
		assertThat(nas2, not(theInstance(nas)));
	}

	@Test()
	void testCreateIllegalAccessException() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> this.serviceLocator.createAndInitialize(Service8.class)).getMessage();

		assertThat(message, equalTo(
				"There is a problem with the class " + Service8.class.getName() + ". It can't be used as a Service"));
	}

	@Test()
	void testSingletonServiceProvider() {
		Service7 service7 = new Service7();
		this.configuration.register(new SingletonServiceProvider<>(Service7API.class, service7));
		assertThat(this.serviceLocator.getService(Service7API.class), is(theInstance(service7)));
	}

	@Test()
	void testAbstractService() {
		assertThrows(IllegalArgumentException.class, () -> this.configuration.register(AbstractService.class));
	}

	@Test()
	void testInject() {
		Service10 service10 = new Service10(10);
		this.serviceLocator.inject(service10);
		assertThat(service10.isInit(), is(false));
		assertThat(service10.getService1(), is(notNullValue()));
		assertThat(service10.getService1(), is(instanceOf(Service1.class)));
	}

	@Test()
	void testServiceType() {
		assertThrows(IllegalArgumentException.class, () -> this.configuration.register(Service17.class));
	}

	@Test()
	void testRegisterServiceTypeFromInterface() {
		this.configuration.register(Service7Impl.class);
		assertThat(this.serviceLocator.getService(Service7API.class), is(notNullValue()));
	}

	@Test()
	void testRegisterServiceTypeFromMultipleInterfaces() {
		assertThrows(IllegalArgumentException.class,
				() -> this.configuration.register(ServiceImplementingMultipleInterfaces.class));
	}

	@Test()
	void testCreateAndInitialize() {
		Service1 service1 = this.serviceLocator.createAndInitialize(Service1.class);
		assertThat(service1, notNullValue());
		assertThat(service1.init, is(1));
	}

	@AfterEach
	public void clearLoggers() {
		TestLoggerFactory.clear();
	}

}
