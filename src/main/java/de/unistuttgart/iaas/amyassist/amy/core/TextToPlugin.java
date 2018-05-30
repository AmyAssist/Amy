/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class maps a sentence to one of the given grammars in jsgf format
 * 
 * @author Felix Burk
 */
class TextToPlugin {

	AnnotationReader reader;
	List<String> grammars;

	TextToPlugin(List<String> grammars) {
		this.grammars = grammars;
	}

	/**
	 * returns first matching grammar
	 * 
	 * this method will ignore things before and after the grammar
	 * which means @Grammar(jhdawdap amy says wjapdjawp) will work
	 * this is great for error prone speech to text 
	 * 
	 * !the longest matching grammar will be returned!
	 * 
	 * @param text
	 * @return success
	 */
	String pluginActionFromText(String text) {		
		ArrayList<String> results = new ArrayList<>();
		
		for (String s : this.grammars) {
			
			String regex = regexFromGrammar(s);
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(text);
			
			//find looks for matching substrings
			// ! the first matching grammar is returned !
			if (m.find())
				results.add(s);

		}
		String finalResult = null;
		if(!results.isEmpty()) {
			finalResult = Collections.max(results, Comparator.comparing(s -> s.length()));
		}
		
		return finalResult;

	}
	
	/**
	 * some regex fun!
	 * because java string handling is weird i have to use alot of \
	 * 
	 * this method is prone to errors, i tested it as much as possible 
	 * but if there are any bugs tell me immediately -Felix B
	 * 
	 * @param grammar
	 * @return the generated regex for the specific grammar
	 */
	private String regexFromGrammar(String grammar) {	
		String regex = grammar;
		
		// find all occurences of optional words example: [test|test] or [test]
		// and replace them with: (test|test)?
		Matcher mmm = Pattern.compile("\\[(.*?)\\]").matcher(regex);
		while(mmm.find()) {
			regex = regex.replace( mmm.group() , "(" + mmm.group().substring(1, mmm.group().length()-1) + ")?");
		}
		
		//find all words and replace them with \\bword\\b 
		Matcher mm = Pattern.compile("[a-zA-Z0-9]+").matcher(regex);
		while(mm.find()) {
			String s = mm.group();
			regex = regex.replace("\\b"+s+"\\b", convertWord(s));
		}
		
		//replace convenience characters for pre defined rules with corresponding regex
		//(this might be hard for other pre defined rules in the future, but numbers are easy)
		regex = regex.replaceAll("#", "((one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelfe|thirteen" + 
				"|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|" + 
				"ten|twenty|thirty|forty|fifty|sixty|seventy|eigthty|ninety)+|([0-9]+))");
				
		//at last replace whitespace with an arbitrary number of whitespaces
		//makes things like @Grammar(this    has   lots of        space) possible
		return regex.replaceAll(" ", "\\\\s*");

	}
	
	/**
	 * convenience method
	 * @param word
	 * @return
	 */
	private String convertWord(String word) {
		return "\\b"+word.toLowerCase()+"\\b";
	}
	
	

}











