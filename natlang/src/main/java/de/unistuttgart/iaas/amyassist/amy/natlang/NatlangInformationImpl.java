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

package de.unistuttgart.iaas.amyassist.amy.natlang;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NatlangInformation;
import de.unistuttgart.iaas.amyassist.amy.natlang.agf.nodes.AGFNode;

/**
 * Service which provides information of possible sentences to tell amy
 * 
 * @author Felix Burk
 */
@Service
public class NatlangInformationImpl implements NatlangInformation {

	@Reference
	private NLProcessingManager manager;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.NatlangInformation#getSampleSentencesFromKeyword(java.lang.String,
	 *      int)
	 */
	@Override
	public String[] getSampleSentencesFromKeyword(String keyword, int nmbSentences) {
		//for future use
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.NatlangInformation#getSampleSentencesFromPlugin(java.lang.String,
	 *      int)
	 */
	@Override
	public String[] getSampleSentencesFromPlugin(String pluginName, int nmbSentences) {
		//for future use
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.NatlangInformation#getAnySampleSentences(int)
	 */
	@Override
	public String[] getAnySampleSentences(int nmbSentences) {
		String[] sentences = new String[nmbSentences];
		List<AGFNode> grams = this.manager.getPossibleGrammars();
		Collections.shuffle(grams);
		for(int i=0; i < nmbSentences; i++) {
			sentences[i] =  generateSentenceFromGrammar(grams.get(i));
		}
		return sentences;
	}
	
	private String generateSentenceFromGrammar(AGFNode node) {
		String result = "";
		Random random = new Random();
		
		switch(node.getType()) {
		case OPG:
			boolean b = random.nextInt(2) % 2 == 0;
			if(!b) {
				return "";
			}
			//$FALL-THROUGH$
		case ORG:
			int i = random.nextInt(node.getChilds().size());
			return result + generateSentenceFromGrammar(node.getChilds().get(i));
		case SHORTWC:
			return result + " +";
		case LONGWC:
			return result + " *";
		case WORD:
			return result + " " + node.getContent();
		case NUMBER:
			return result + " <number>";
		default:
			for(AGFNode gram : node.getChilds()) {
				result += generateSentenceFromGrammar(gram);
			}
			break;
		}
		
		return result;
	}
	

}
