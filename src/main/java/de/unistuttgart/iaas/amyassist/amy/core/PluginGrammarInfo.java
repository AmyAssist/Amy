/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.List;

/**
 * Plugin grammar info helper class
 * @author Felix Burk
 */
class PluginGrammarInfo {
	
	List<String> keywords;
	List<String> grammars;
	
	PluginGrammarInfo(List<String> keywords, List<String> grammars) {
		this.keywords = keywords;
		this.grammars = grammars;
	}
	

}
