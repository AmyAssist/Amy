package de.unistuttgart.iaas.amyassist.amy.core;
/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */

/**
 * Placeholder Interface 
 * 
 * @author Felix Burk
 */
interface ITextToPlugin {
		
	/**
	 * returns true if action is successful
	 * 
	 * @param text
	 * @return success
	 */
	boolean pluginActionFromText(String text);
	
	/**
	 * internal helper method to generate possible regex for the specific grammar
	 * might be removed/changed soon
	 * @param grammar
	 * @return
	 */
	String regexFromGrammar(String grammar);
}
