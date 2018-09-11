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

package de.unistuttgart.iaas.amyassist.amy.core.configuration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test loading using from the default ClassLoader
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
class InternalDefaultConfigurationLoaderTest {
	@Reference
	private TestFramework testFramework;
	private InternalDefaultConfigurationLoader loader;
	private ClassLoader classLoader;

	/**
	 * Setup before each test
	 */
	@BeforeEach
	void setup() {
		this.loader = this.testFramework.setServiceUnderTest(InternalDefaultConfigurationLoader.class);
		this.classLoader = this.getClass().getClassLoader();
	}

	@Test
	void test() {
		Properties properties = this.loader.load(this.classLoader, "configuration.test.config");
		assertThat(properties, is(notNullValue()));
		assertThat(properties, hasEntry("test.key", "hello world"));
		assertThat(properties.size(), is(1));
	}

	@Test
	void testConfigurationNotFoundException() {
		assertThrows(ConfigurationNotFoundException.class,
				() -> this.loader.load(this.classLoader, "does.not.exists.config"));
	}

}
