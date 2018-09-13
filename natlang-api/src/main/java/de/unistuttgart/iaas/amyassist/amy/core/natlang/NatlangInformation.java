/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

/**
 * Natlang information interface provides informations about agf grammars beeing used in natural language recognition
 * 
 * @author Felix Burk
 */
public interface NatlangInformation {

	/**
	 * provides possible sentences that contain a keyword
	 * 
	 * @param keyword
	 *            the sentence contains
	 * @param nmbSentences
	 *            how many sentences should be provided at max
	 * @return a list of sentences
	 */
	public String[] getSampleSentencesFromKeyword(String keyword, int nmbSentences);

	/**
	 * provides possible sentences from a loaded plugin
	 * 
	 * @param pluginName
	 *            display name of the plugin
	 * @param nmbSentences
	 *            how many sentences should be provided at max
	 * @return a list of sentences
	 */
	public String[] getSampleSentencesFromPlugin(String pluginName, int nmbSentences);

	/**
	 * provides any number of possible sentences
	 * 
	 * @param nmbSentences
	 *            how many sentences should be provided
	 * @return a list of sentences
	 */
	public String[] getAnySampleSentences(int nmbSentences);

}
