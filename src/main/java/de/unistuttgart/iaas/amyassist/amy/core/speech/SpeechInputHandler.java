/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.util.concurrent.Future;

/**
 * 
 * @author Leon Kiefer
 */
public interface SpeechInputHandler {
	/**
	 * 
	 * @param speechInput
	 *            the user input
	 * @return a Future, that completes with the result of processing the user
	 *         input
	 */
	Future<String> handle(String speechInput);
}
