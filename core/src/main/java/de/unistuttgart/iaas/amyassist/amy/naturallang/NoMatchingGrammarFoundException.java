/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.naturallang;

/**
 * Custom exception class
 * 
 * @author Felix Burk
 */
public class NoMatchingGrammarFoundException extends RuntimeException{
	
	/**
	 * version UID
	 */
	private static final long serialVersionUID = 6279832831343198832L;

	/**
	 * constructor with msg
	 * @param msg msg
	 */
	public NoMatchingGrammarFoundException(String msg) {
		super(msg);
	}
	
	/**
	 * constructor without msg
	 */
	public NoMatchingGrammarFoundException() {
		super();
	}

}
