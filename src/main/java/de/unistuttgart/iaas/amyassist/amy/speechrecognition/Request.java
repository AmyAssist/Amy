/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.speechrecognition;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Description
 * 
 * @author Tim Neumann, Patrick Gebhardt, Patrick Singer, Florian Bauer, Kai
 *         Menzel
 */
public class Request {
	private String keyword;
	private String action;
	private List<String> parameters = new ArrayList<>();

	/**
	 * Creates a new request with the given words
	 *
	 * @param p_words
	 *            The words to create the request from. Needs to at least
	 *            contain the keyword and the action. May contain additional
	 *            parameters.
	 * @throws IllegalArgumentException
	 *             When the keyword or action is missing
	 *
	 */
	public Request(String... p_words) throws IllegalArgumentException {
		if (p_words.length < 2)
			throw new IllegalArgumentException("Need at least a keyword and action.");
		this.keyword = p_words[0];
		this.action = p_words[1];
		for (int i = 2; i < p_words.length; i++) {
			this.parameters.add(p_words[i]);
		}
	}

	/**
	 * Creates a new request with the given info
	 *
	 * @param p_keyword
	 *            The keyword of the request
	 * @param p_action
	 *            The action of the request
	 * @param p_parameters
	 *            additional parameters
	 */
	public Request(String p_keyword, String p_action, String... p_parameters) {
		this.keyword = p_keyword;
		this.action = p_action;
		for (String para : p_parameters) {
			this.parameters.add(para);
		}
	}

	/**
	 * @return all words of this request
	 */
	public String[] getWords() {
		String[] arr = new String[2 + this.parameters.size()];
		arr[0] = this.keyword;
		arr[1] = this.action;
		for (int i = 0; i < this.parameters.size(); i++) {
			arr[i + 2] = this.parameters.get(i);
		}
		return arr;
	}

	/**
	 * @return the keyword of the request
	 */
	public String getKeyword() {
		return this.keyword;
	}

	/**
	 * @return the action of the request
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @return the parameters of the request
	 */
	public String[] getParameters() {
		return this.parameters.toArray(new String[0]);
	}

}
