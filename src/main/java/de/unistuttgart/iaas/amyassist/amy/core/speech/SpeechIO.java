/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.speech;

/**
 * A interface that defines an Speech Input Output component. The component is
 * executed in its own Thread and must use the SpeechInputHandler to pass the
 * user speech input to the application.
 * 
 * @author Leon Kiefer
 */
public interface SpeechIO extends Runnable {

	/**
	 * Setter for the SpeechInputHandler. The SpeechInputHandler is used to
	 * process the user input.
	 * 
	 * @param handler
	 *            the SpeechInputHandler to use
	 */
	void setSpeechInputHandler(SpeechInputHandler handler);
}
