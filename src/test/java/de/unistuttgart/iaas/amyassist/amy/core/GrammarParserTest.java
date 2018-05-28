/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * TODO: Description
 * 
 * @author Felix Burk
 */
public class GrammarParserTest {

	private GrammarParser parser;

	@BeforeEach
	public void setup() {
		this.parser = new GrammarParser("amy", "okay amy", "sleep", "die");
	}

	/**
	 * 
	 */
	@Test
	public void test() {
		this.parser.addRule("wecker", "(today|tomorrow) ((at #)|abend|mittag)");
		String grammarResult = this.parser.getGrammar();
		assertThat(grammarResult, containsString(
				"public <wecker> = (today|tomorrow) ((at <digit>)|abend|mittag);"));
		assertThat(grammarResult,
				containsString("public <sleep> = ( sleep );"));
		assertThat(grammarResult,
				containsString("public <shutdown> = ( die );"));
		assertThat(grammarResult,
				containsString("public <wakeup> = ( okay amy );"));
	}

}
