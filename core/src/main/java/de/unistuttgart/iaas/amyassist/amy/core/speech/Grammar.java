/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.io.File;
import java.util.HashMap;

/**
 * Grammar Object, that contains all important Information of a Grammar
 * @author Kai Menzel
 */
public class Grammar {
	
	private String name;
	private File file;
	private HashMap<String, Grammar> switchList = new HashMap<>();

	/**
	 * @param name Name of the Grammar
	 * @param file Path to the Grammar
	 * 
	 */
	public Grammar(String name, File file) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.file = file;
	}
	
	/**
	 * @param name Name of the Grammar
	 * @param file Path to the Grammar
	 * @param switchList List of all possible Grammar changes
	 */
	public Grammar(String name, File file, HashMap<String, Grammar> switchList) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.file = file;
		this.switchList = switchList;
	}
	
	/**
	 * Add a new Grammar that can be switched to
	 * @param switchCommand Command to say to switch to new Grammar
	 * @param gram Grammar to change to by said Grammarswitch
	 */
	public void putChangeGrammar(String switchCommand, Grammar grammar){
		this.switchList.put(switchCommand, grammar);
	}

	public String getName() {
		return this.name;
	}

	public File getFile() {
		return this.file;
	}

	public HashMap<String, Grammar> getSwitchList() {
		return this.switchList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setSwitchList(HashMap<String, Grammar> switchList) {
		this.switchList = switchList;
	}

	

}
