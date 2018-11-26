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

package io.github.amyassist.amy.test;

import static io.github.amyassist.amy.test.matcher.logger.LoggerMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Test for the TestFramework interface of the TestFrameworkImpl
 * 
 * @author Leon Kiefer
 */
class TestFrameworkImplTest {

	private TestFrameworkImpl testFrameworkImpl;

	@BeforeEach
	void setup() {
		this.testFrameworkImpl = new TestFrameworkImpl();
	}

	@Test
	void testMockService() {
		TestService mockService = this.testFrameworkImpl.mockService(TestService.class);
		assertThat(mockService, is(notNullValue()));
		assertThat(mockService, isA(TestService.class));
	}

	@Test
	void testSUT() {
		TestService sut = this.testFrameworkImpl.setServiceUnderTest(TestService.class);
		assertThat(sut, is(notNullValue()));
		assertThat(sut, isA(TestService.class));
	}

	@Test
	void testSUTMustBeAClass() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> this.testFrameworkImpl.setServiceUnderTest(ServiceAPI.class)).getMessage();
		assertThat(message, is("Sevices must have a @Service annotation"));
	}

	@Test
	void testRESTResource() {
		this.testFrameworkImpl.before();
		this.testFrameworkImpl.after();
	}

	@Test
	void testRESTResourceMustBeAResource() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> this.testFrameworkImpl.setRESTResource(ServiceAPI.class)).getMessage();
		assertThat(message, is("The Resource must have a @Path annotation"));
	}

	@Test
	void testLogger() {
		TestLogger testLogger = TestLoggerFactory.getTestLogger(ServiceWithLogger.class);
		ServiceWithLogger serviceWithLogger = this.testFrameworkImpl.setServiceUnderTest(ServiceWithLogger.class);
		assertThat(serviceWithLogger, is(notNullValue()));
		serviceWithLogger.log("test");
		assertThat(testLogger, hasLogged(info("test")));
		assertThat(testLogger, loggingEventCount(is(1)));
	}

	@Test
	void testStorage() {
		assertThat(this.testFrameworkImpl.storage(), is(notNullValue()));
	}

	@AfterEach
	void cleanUp() {
		TestLoggerFactory.clearAll();
	}

}
