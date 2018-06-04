/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests the pom parser
 * 
 * @author Tim Neumann
 */
class PomParserTest {

	private static PomParser parser;

	/**
	 * @throws java.lang.Exception
	 *             when something goes wrong
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		PomParserTest.parser = new PomParser(PomParserTest.class.getResource("/de/unistuttgart/iaas/amyassist/amy/core/pluginloader/pom.xml").openStream());
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PomParser#getName()}.
	 */
	@Test
	void testGetName() {
		Assertions.assertEquals("A old version of the pom for testing the pom parser", parser.getName(), "Wrong name");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PomParser#getId()}.
	 */
	@Test
	void testGetId() {
		Assertions.assertEquals("amy-mock", parser.getId(), "Wrong id");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PomParser#getGroupId()}.
	 */
	@Test
	void testGetGroupId() {
		Assertions.assertEquals("de.unistuttgart.iaas.amyassist", parser.getGroupId(), "Wrong group id");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PomParser#getVersion()}.
	 */
	@Test
	void testGetVersion() {
		Assertions.assertEquals("1.0.0", parser.getVersion(), "Wrong version");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PomParser#getFullId()}.
	 */
	@Test
	void testGetFullId() {
		Assertions.assertEquals("de.unistuttgart.iaas.amyassist.amy-mock", parser.getFullId(), "Wrong full id");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PomParser#getDependencyIds()}.
	 */
	@Test
	void testGetDependencyIds() {
		Assertions.assertArrayEquals(new String[] { "org.junit.jupiter.junit-jupiter-engine", "org.hamcrest.hamcrest-all" }, parser.getDependencyIds(), "Wrong dependencies");
	}

}
