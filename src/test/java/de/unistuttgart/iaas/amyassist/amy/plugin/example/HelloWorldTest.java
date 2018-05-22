/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.AnnotationReader;

/**
 * A Test for the Hello World Plugin
 * 
 * @author Leon Kiefer
 */
public class HelloWorldTest {

	/**
	 * Test
	 */
	@Test
	public void test() {
		MatcherAssert.assertThat(new AnnotationReader().getSpeechKeyword(HelloWorld.class), Matchers.equalTo("Hello world"));
	}
}
