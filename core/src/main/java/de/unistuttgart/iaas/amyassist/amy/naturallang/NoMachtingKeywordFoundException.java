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
 * custom exception
 * 
 * @author Felix Burk
 */
public class NoMachtingKeywordFoundException extends RuntimeException {
	
	/**
	 * version UID
	 */
	private static final long serialVersionUID = -8956156097550041141L;

	/**
	 * constructor with msg
	 * @param msg msg
	 */
	public NoMachtingKeywordFoundException(String msg) {
		super(msg);
	}
	
	/**
	 * constructor without msg
	 */
	public NoMachtingKeywordFoundException() {
		super();
	}

}
