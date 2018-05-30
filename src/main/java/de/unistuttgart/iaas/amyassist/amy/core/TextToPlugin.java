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
import java.util.HashMap;
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
	List<PluginGrammarInfo> infos;
	
	TextToPlugin(List<PluginGrammarInfo> infos) {
		this.infos = infos;
		
		setupUglyHashMap();

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
	 * @return String[] 0 contains matching keyword, 1 contains matching grammar
	 */
	String[] pluginActionFromText(String text) {		
		ArrayList<String> results = new ArrayList<>();
		String finalResult = null;
		
		
		for(PluginGrammarInfo currentGrammar : this.infos) {
			
			for(String keyword : currentGrammar.keywords) {
				
				int index = text.indexOf(keyword);
				String textTemp = text;
				
				//keyword found! try to match the grammar
				if(index != -1) {
					textTemp = text.substring(index+keyword.length(), text.length());
										
					List<String> grammars = currentGrammar.grammars;
					for (String s : grammars) {
						
						String regex = regexFromGrammar(s);
						Pattern p = Pattern.compile(regex);
						Matcher m = p.matcher(textTemp);
						
						if (m.find())
							results.add(s);
			
					}
					//find looks for matching substrings
					// ! the first matching grammar is returned !
					if(!results.isEmpty()) {
						finalResult = Collections.max(results, Comparator.comparing(s -> s.length()));
						return new String[] {keyword, finalResult};
					}
					
				}
			}
		}
		
		return null;

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
		// \\bnumber\\b is not needed here because of the previous for loop
		regex = regex.replaceAll("#", "((zero|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelfe|thirteen" + 
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
	
	
	/**
	 * get a number as int from any string
	 * only one number in the sentence is supported right now! 
	 * and only numbers ranging from 0 to 99
	 * @param text
	 * @return result
	 */
	int stringToNumber(String text) {
		String regex = "(\\bzero\\b|\\bone\\b|\\btwo\\b|\\bthree\\b|\\bfour\\b|\\bfive\\b|\\bsix\\b|\\bseven\\b|\\beight\\b|\\bnine\\b|"
				+ "\\bten\\b|\\beleven\\b|\\btwelfe\\b|\\bthirteen\\b" + 
				"|\\bfourteen\\b|\\bfifteen\\b|sixteen\\b|\\bseventeen\\b|\\beighteen\\b|\\bnineteen\\b|" + 
				"\\bten\\b|\\btwenty\\b|\\bthirty\\b|\\bforty\\b|\\bfifty\\b|\\bsixty\\b|\\bseventy\\b|\\beigthty\\b|\\bninety\\b)";
	
		Matcher m = Pattern.compile(regex).matcher(text);
		int result = 0;
		while(m.find()) {
			result += Integer.valueOf(stringToNmb.get(m.group()));
		}
		
		return result;
	}
	
	
	private HashMap<String, Integer> stringToNmb = new HashMap<>();
	
	/**
	 * i know this is ugly.. 
	 * this had to be done quickly
	 */
	
	private void setupUglyHashMap() {
		String[] oneToNine = {"zero","one","two","three","four","five","six","seven","eight","nine" };
		String[] teens = {"eleven","twelfe","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen"};
		String[] tens = {"ten","twenty","thirty","forty","fifty","sixty","seventy","eigthty","ninety" };
		
		int i = 0;
		for(String s : oneToNine) {
			this.stringToNmb.put(s, i);
			i++;
		}
		
		i = 11;
		for(String s : teens) {
			this.stringToNmb.put(s, i);
			i++;
		}
		
		i = 10;
		for(String s: tens) {
			this.stringToNmb.put(s, i);
			i += 10;
		}
	}

	

}











