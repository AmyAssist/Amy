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
 * TODO: Description
 * @author Tim Neumann
 */
public class mainy {

	/**
	 * 
	 */
	public mainy() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		(new Thread(new SpeechRecognizer("amy", "sleep", "amy shutdown", "src/main/resources/grammars"))).start();

	}

}
