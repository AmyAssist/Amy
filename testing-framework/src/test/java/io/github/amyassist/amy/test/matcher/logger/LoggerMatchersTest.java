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

package de.unistuttgart.iaas.amyassist.amy.test.matcher.logger;

import static de.unistuttgart.iaas.amyassist.amy.test.matcher.logger.LoggerMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Tests for the Matcher
 * 
 * @author Leon Kiefer
 */
class LoggerMatchersTest {
	private TestLogger testLogger;

	@BeforeEach
	void setup() {
		this.testLogger = TestLoggerFactory.getTestLogger(this.getClass());
	}

	@Test
	void testLoggingEventCountIs() {
		this.testLogger.error("error");
		assertThat(this.testLogger, loggingEventCount(is(1)));
	}

	@Test
	void testLoggingEventCountGreater() {
		this.testLogger.error("error");
		this.testLogger.debug("second");
		assertThat(this.testLogger, loggingEventCount(is(greaterThan(1))));
	}

	@Test
	void testLoggerException() {
		this.testLogger.error("error", new RuntimeException());
		assertThat(this.testLogger, hasLogged(error("error", instanceOf(RuntimeException.class))));
		assertThat(this.testLogger, loggingEventCount(is(1)));
	}

	@Test
	void testLoggerExceptionIgnore() {
		this.testLogger.error("error", new RuntimeException());
		assertThat(this.testLogger, hasLogged(error("error")));
		assertThat(this.testLogger, loggingEventCount(is(1)));
	}

	@Test
	void testLoggerArguments() {
		this.testLogger.error("error {}", "big problem");
		assertThat(this.testLogger, hasLogged(error("error {}", "big problem")));
	}

	@AfterEach
	void afterEach() {
		TestLoggerFactory.clearAll();
	}

}
