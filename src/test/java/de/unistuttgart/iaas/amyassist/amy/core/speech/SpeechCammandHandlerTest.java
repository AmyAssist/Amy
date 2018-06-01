/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.speech;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Tests for the SpeechCammandHandler
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtention.class)
class SpeechCammandHandlerTest {
	@Reference
	private TestFramework framework;
	private SpeechCommandHandler speechCammandHandler;

	@BeforeEach
	public void setup() {
		this.speechCammandHandler = this.framework.setServiceUnderTest(SpeechCommandHandler.class);
	}

	@Test
	void test() {
		this.speechCammandHandler.registerSpeechCommand(TestSpeechCommand.class);
		this.speechCammandHandler.completeSetup();

		String result = this.speechCammandHandler.handleSpeechInput("testkeyword simple 10");

		assertThat(result, equalTo("testkeyword simple 10"));
		// assertThat(result, equalTo("10"));
	}

	@Test
	void testUnknownKeyword() {
		this.speechCammandHandler.registerSpeechCommand(TestSpeechCommand.class);
		this.speechCammandHandler.completeSetup();

		assertThrows(IllegalArgumentException.class,
				() -> this.speechCammandHandler.handleSpeechInput("unknownKeyword simple 10"));
	}

}
