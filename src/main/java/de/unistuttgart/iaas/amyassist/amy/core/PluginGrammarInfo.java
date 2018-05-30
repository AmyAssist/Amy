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
import java.util.Set;

/**
 * Plugin grammar info helper class
 * 
 * @author Felix Burk
 */
public class PluginGrammarInfo {

	List<String> keywords;
	Set<String> grammars;

	public PluginGrammarInfo(List<String> keywords, Set<String> set) {
		this.keywords = keywords;
		this.grammars = set;
	}
}
