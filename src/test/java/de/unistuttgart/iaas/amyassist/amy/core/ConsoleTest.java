/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;

/**
 * TODO: Description
 * 
 * @author Tim Neumann
 */
class ConsoleTest {

	@Test
	void test() {
		final String testInput = "Hello world say hello";
		final String expected = "hello1";

		SpeechInputHandler handler = Mockito.mock(SpeechInputHandler.class);
		CompletableFuture<String> completableFuture = new CompletableFuture<>();
		completableFuture.complete(expected);
		Mockito.when(handler.handle(testInput)).thenReturn(completableFuture);

		Console console = new Console();
		console.setSpeechInputHandler(handler);

		assertThat(console.say(testInput), equalTo(expected));
	}

}
