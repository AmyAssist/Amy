/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;

/**
 * Test Cases for the AnnotationReader
 * 
 * @author Leon Kiefer
 */
class TestAnnotationReader {

	private AnnotationReader annotationReader;

	@BeforeEach
	public void init() {
		this.annotationReader = new AnnotationReader();
	}

	@Test
	void testSpeechKeyword() {
		String[] speechKeyword = this.annotationReader
				.getSpeechKeyword(Plugin.class);

		assertThat(speechKeyword, is(arrayWithSize(2)));
		assertThat(speechKeyword, is(arrayContainingInAnyOrder("test", "unittest")));
	}

	@Test
	public void testGrammar() {
		List<String> grammars = this.annotationReader.getGrammars(Plugin.class);

		assertThat(grammars, containsInAnyOrder("count", "say (hello|test)"));
	}

	@Test
	public void testInit() {
		Method initMethod = this.annotationReader.getInitMethod(Plugin.class);

		assertThat(initMethod, is(notNullValue()));
	}

	@SpeechCommand({ "test", "unittest" })
	class Plugin {
		@Grammar("count")
		public String count(String... s) {
			return "1";
		}

		@Grammar("say (hello|test)")
		public String say(String... s) {
			return s[0];
		}

		@Init
		public void foo(ICore core) {

		}
	}

}
