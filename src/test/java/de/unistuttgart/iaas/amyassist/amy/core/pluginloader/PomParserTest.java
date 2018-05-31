/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
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
