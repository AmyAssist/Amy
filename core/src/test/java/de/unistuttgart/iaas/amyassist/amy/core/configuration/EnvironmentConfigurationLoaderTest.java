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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for {@link EnvironmentConfigurationLoader}
 * 
 * @author Tim Neumann
 */
@ExtendWith(FrameworkExtension.class)
class EnvironmentConfigurationLoaderTest {

	private static final int NUM_RUNS = 1000;
	private static final int MAX_LENGTH = 10;

	@Reference
	private TestFramework testFramework;

	private EnvironmentConfigurationLoader loader;

	private Map<String, String> envVars;

	private static Random rng;
	private static long seed;

	/**
	 * Before all
	 */
	@BeforeAll
	static void init() {
		seed = (long) (Math.random() * Integer.MAX_VALUE);
		EnvironmentConfigurationLoaderTest.rng = new Random(seed);
	}

	/**
	 * Setup before each test
	 */
	@BeforeEach
	void setup() {

		this.envVars = new HashMap<>();
		Environment env = this.testFramework.mockService(Environment.class);
		Mockito.when(env.getEnvironmentVariables()).thenReturn(this.envVars);

		this.loader = this.testFramework.setServiceUnderTest(EnvironmentConfigurationLoaderImpl.class);
	}

	/**
	 * Tests just the trivial behavior.
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadStandard() {
		this.envVars.put("amy_config1_key1", "value11");
		this.envVars.put("amy_config1_key2", "value12");

		Properties orig = new Properties();

		orig.setProperty("key1", "orig11");
		orig.setProperty("key2", "orig12");

		Properties res1 = this.loader.load("config1", orig);

		Assertions.assertEquals("value11", res1.getProperty("key1"));
		Assertions.assertEquals("value12", res1.getProperty("key2"));
	}

	/**
	 * Tests that env vars without amy_ have no influence
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadNoPrefix() {
		this.envVars.put("amyconfig1_key1", "value11");
		this.envVars.put("a_config1_key1", "value11");
		this.envVars.put("am_config1_key1", "value11");
		this.envVars.put("m_config1_key1", "value11");
		this.envVars.put("my_config1_key1", "value11");
		this.envVars.put("y_config1_key1", "value11");
		this.envVars.put("_config1_key1", "value11");
		this.envVars.put("config1_key1", "value11");

		Properties orig = new Properties();

		orig.setProperty("key1", "orig11");

		Properties res1 = this.loader.load("config1", orig);

		Assertions.assertEquals("orig11", res1.getProperty("key1"));
	}

	/**
	 * Tests that '.' in the config name works
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadPointInName() {
		this.envVars.put("amy_config_1_key1", "value11");

		Properties orig = new Properties();

		orig.setProperty("key1", "orig11");

		Properties res = this.loader.load("config.1", orig);

		Assertions.assertEquals("value11", res.getProperty("key1"));
	}

	/**
	 * Tests that '.' in the key works
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadPointInKey() {
		this.envVars.put("amy_config1_key_1", "value11");

		Properties orig = new Properties();

		orig.setProperty("key.1", "orig11");

		Properties res = this.loader.load("config1", orig);

		Assertions.assertEquals("value11", res.getProperty("key.1"));
	}

	/**
	 * Tests that it works with a config set in default.
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadDefaults() {
		this.envVars.put("amy_config1_key1", "value11");

		Properties def = new Properties();

		def.setProperty("key1", "orig11");

		Properties orig = new Properties(def);

		Properties res = this.loader.load("config1", orig);

		Assertions.assertEquals("value11", res.getProperty("key1"));
	}

	/**
	 * Tests for configs that are not set in the environment
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadNotSetInEnv() {
		this.envVars.put("amy_config1_key1", "value11");

		Properties orig1 = new Properties();
		Properties orig2 = new Properties();

		orig1.setProperty("key1", "orig11");
		orig1.setProperty("key2", "orig12");
		orig2.setProperty("key1", "orig21");

		Properties res1 = this.loader.load("config1", orig1);
		Properties res2 = this.loader.load("config2", orig2);

		Assertions.assertEquals("value11", res1.getProperty("key1"));
		Assertions.assertEquals("orig12", res1.getProperty("key2"));
		Assertions.assertEquals("orig21", res2.getProperty("key1"));
	}

	/**
	 * Tests for configs that are not set in the orig
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadNotSetInOrig() {
		this.envVars.put("amy_config1_key1", "value11");
		this.envVars.put("amy_config1_key2", "value12");
		this.envVars.put("amy_config2_key1", "value21");

		Properties orig1 = new Properties();
		Properties orig2 = new Properties();

		orig1.setProperty("key1", "orig11");

		Properties res1 = this.loader.load("config1", orig1);
		Properties res2 = this.loader.load("config2", orig2);

		Assertions.assertEquals("value11", res1.getProperty("key1"));
		Assertions.assertNull(res1.getProperty("key2"));
		Assertions.assertNull(res2.getProperty("key1"));
	}

	/**
	 * Tests the replacing of . with _
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadReplacing() {
		this.envVars.put("amy_config_1_key1", "value11");
		this.envVars.put("amy_config_1_key2", "value12");
		this.envVars.put("amy_config_2_key1", "value21");

		Properties orig1 = new Properties();
		Properties orig2 = new Properties();

		orig1.setProperty("key1", "orig11");
		orig1.setProperty("key2", "orig12");
		orig2.setProperty("key1", "orig21");

		Properties res1 = this.loader.load("config.1", orig1);
		Properties res2 = this.loader.load("config.2", orig2);

		Assertions.assertEquals("value11", res1.getProperty("key1"));
		Assertions.assertEquals("value12", res1.getProperty("key2"));
		Assertions.assertEquals("value21", res2.getProperty("key1"));
	}

	/**
	 * Tests the ignore case
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadIgnoreCase() {
		this.envVars.put("AMY_CONFIG1_key1", "value11");
		this.envVars.put("amy_config1_KEY2", "value12");
		this.envVars.put("aMy_config2_kEy1", "value21");

		Properties orig1 = new Properties();
		Properties orig2 = new Properties();

		orig1.setProperty("key1", "orig11");
		orig1.setProperty("key2", "orig12");
		orig2.setProperty("key1", "orig21");

		Properties res1 = this.loader.load("config1", orig1);
		Properties res2 = this.loader.load("CONFIG2", orig2);

		Assertions.assertEquals("value11", res1.getProperty("key1"));
		Assertions.assertEquals("value12", res1.getProperty("key2"));
		Assertions.assertEquals("value21", res2.getProperty("key1"));
	}

	/**
	 * Tests that multiple keys can get the same value from the same var
	 * <p>
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.configuration.EnvironmentConfigurationLoaderImpl#load(java.lang.String, java.util.Properties)}.
	 */
	@Test
	void testLoadColidingName() {
		this.envVars.put("amy_a_b_c", "value11");

		Properties orig1 = new Properties();
		Properties orig2 = new Properties();

		orig1.setProperty("c", "orig1");
		orig2.setProperty("b_c", "orig2");

		Properties res1 = this.loader.load("a_b", orig1);
		Properties res2 = this.loader.load("a", orig2);

		Assertions.assertEquals("value11", res1.getProperty("c"));
		Assertions.assertEquals("value11", res2.getProperty("b_c"));
	}

	/**
	 * Tests that load accepts all valid names.
	 * 
	 * @param var
	 *            a name
	 */
	@ParameterizedTest
	@MethodSource("validNames")
	void testValidNames(String var) {
		try {
			this.loader.load(var, new Properties());
		} catch (IllegalArgumentException e) {
			Assertions.fail("There should not be an exception. Name: " + var);
		}
	}

	/**
	 * Tests that load rejects all invalid names.
	 * 
	 * @param var
	 *            a name
	 */
	@ParameterizedTest
	@MethodSource("invalidNames")
	void testInvalidNames(String var) {
		Assertions.assertThrows(IllegalArgumentException.class, () -> this.loader.load(var, new Properties()),
				"No Exception. Name: " + var);
	}

	private static String allowedChars = "0123456789abcdefghijklmnopqrstuvwxyz_.";
	private static String badChars = "+*~#'-:,;<>|^°!\"§$%&/()=?\\}][{";

	/**
	 * @return A stream of valid names
	 */
	static Stream<String> validNames() {
		List<String> vars = new ArrayList<>();
		while (vars.size() < NUM_RUNS) {
			for (int i = 10; i < allowedChars.length(); i++) {
				int length = rng.nextInt(MAX_LENGTH - 1);
				vars.add(allowedChars.charAt(i) + getRandomString(allowedChars, length, rng));
			}
		}
		return vars.stream();
	}

	/**
	 * @return A stream of invalid names
	 */
	static Stream<String> invalidNames() {
		List<String> vars = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			int length = rng.nextInt(MAX_LENGTH - 1);
			vars.add(allowedChars.charAt(i) + getRandomString(allowedChars, length, rng));
			length = rng.nextInt(MAX_LENGTH - 1);
			vars.add(allowedChars.charAt(i) + getRandomString(allowedChars, length, rng));
			length = rng.nextInt(MAX_LENGTH - 1);
			vars.add(allowedChars.charAt(i) + getRandomString(badChars, length, rng));
		}
		while (vars.size() < NUM_RUNS) {
			int length = rng.nextInt(MAX_LENGTH);
			vars.add(getRandomString(badChars, length, rng));
		}
		return vars.stream();
	}

	private static String getRandomString(String chars, int length, Random p_rng) {
		StringBuilder builder = new StringBuilder();
		while (builder.length() < length) {
			builder.append(chars.charAt(p_rng.nextInt(chars.length())));
		}
		return builder.toString();
	}

}
