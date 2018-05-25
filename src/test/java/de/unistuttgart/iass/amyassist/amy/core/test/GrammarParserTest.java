/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iass.amyassist.amy.core.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.GrammarParser;

/**
 * TODO: Description
 * 
 * @author Felix Burk
 */
@ExtendWith(MockitoExtension.class)
public class GrammarParserTest {

	// because GrammarParser is protected, hacked together a quick Mock class
	// to prevent changing everything to public
	private class MockGrammarParser extends GrammarParser {
		/**
		 * @param name
		 * @param wakeup
		 * @param sleep
		 * @param shutdown
		 */
		protected MockGrammarParser(String name, String wakeup, String sleep, String shutdown) {
			super(name, wakeup, sleep, shutdown);
		}

		@Override
		protected void addRule(String s, String ss) {
			super.addRule(s, ss);
		}

		@Override
		protected String getGrammar() {
			return super.getGrammar();
		}

	}

	private MockGrammarParser parser = new MockGrammarParser("amy", "okay amy", "sleep", "die");

	/**
	 * 
	 */
	@Test
	public void test() {
		this.parser.addRule("wecker", "(today|tomorrow) ((at #)|abend|mittag)");
		String grammarResult = this.parser.getGrammar();
		assert (grammarResult.contains("public <wecker> = (today|tomorrow) ((at <digit>)|abend|mittag);"));
		assert (grammarResult.contains("public <sleep> = ( sleep );"));
		assert (grammarResult.contains("public <shutdown> = ( die );"));
		assert (grammarResult.contains("public <wakeup> = ( okay amy );"));
	}

}
