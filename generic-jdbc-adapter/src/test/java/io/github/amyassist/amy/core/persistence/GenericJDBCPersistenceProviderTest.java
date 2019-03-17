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

package io.github.amyassist.amy.core.persistence;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test cases for the GenericJDBCPersistenceProvider, testing the usage of the ConfigurationManager and the
 * transformation of the url.
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
class GenericJDBCPersistenceProviderTest {

	private static final String TEST_DATABASENAME = "testDatabasename";
	private Properties testProperties;

	@Reference
	private TestFramework testFramework;
	private GenericJDBCPersistenceProvider provider;

	@BeforeEach
	public void setup() {
		this.testProperties = new Properties();
		this.testProperties.setProperty("javax.persistence.jdbc.url", "");
		ConfigurationManager configurationManager = this.testFramework.mockService(ConfigurationManager.class);
		Mockito.when(configurationManager.getConfigurationWithDefaults("javax.persistence"))
				.thenReturn(this.testProperties);
		this.provider = this.testFramework.setServiceUnderTest(GenericJDBCPersistenceProvider.class);
	}

	@Test
	void testURLTransform() {
		this.testProperties.setProperty("javax.persistence.jdbc.url",
				"jdbc:cutom://nohost/{databasename}?user=test,password=databasename");

		Properties properties = this.provider.getProperties(TEST_DATABASENAME);

		assertThat(properties.getProperty("javax.persistence.jdbc.url"),
				is("jdbc:cutom://nohost/" + TEST_DATABASENAME + "?user=test,password=databasename"));
	}

	@Test
	void testNotTransformOtherValues() {
		this.testProperties.setProperty("custom", "some{databasename}");

		Properties properties = this.provider.getProperties(TEST_DATABASENAME);

		assertThat(properties.getProperty("custom"), is("some{databasename}"));
	}

}
