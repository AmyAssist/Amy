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

package io.github.amyassist.amy.natlang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.natlang.NatlangInformation;
import io.github.amyassist.amy.natlang.agf.nodes.AGFNode;
import io.github.amyassist.amy.natlang.agf.nodes.WordNode;

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
	 * @see io.github.amyassist.amy.core.natlang.NatlangInformation#getSampleSentencesFromKeyword(java.lang.String,
	 *      int)
	 */
	@Override
	public List<String> getSampleSentencesFromKeyword(String keyword, int nmbSentences) {
		List<AGFNode> grams = this.manager.getPossibleGrammars();
		List<String> result = new ArrayList<>();

		for (AGFNode node : grams) {
			List<WordNode> words = node.getChildWordNodes();
			boolean b = words.stream().anyMatch(w -> w.getContent().equals(keyword));
			if (b)
				result.add(generateSentenceFromGrammar(node).trim());
		}
		return result;
	}

	/**
	 * @see io.github.amyassist.amy.core.natlang.NatlangInformation#getAnySampleSentences(int)
	 */
	@Override
	public List<String> getAnySampleSentences(int nmbSentences) {
		List<String> result = new ArrayList<>(nmbSentences);
		List<AGFNode> grams = this.manager.getPossibleGrammars();
		Collections.shuffle(grams);
		for (AGFNode node : grams) {
			result.add(generateSentenceFromGrammar(node).trim());
		}
		return result;
	}

	/**
	 * convenience method to generate a valid sentence from a grammar
	 * 
	 * @param node
	 *            grammar to generate from
	 * @return a matching string
	 */
	private String generateSentenceFromGrammar(AGFNode node) {
		StringBuilder result = new StringBuilder();
		Random random = new Random();

		switch (node.getType()) {
		case OPG:
			boolean b = random.nextInt(2) % 2 == 0;
			if (!b) {
				return "";
			}
			//$FALL-THROUGH$
		case ORG:
			int i = random.nextInt(node.getChilds().size());
			return result.append(generateSentenceFromGrammar(node.getChilds().get(i))).toString();
		case SHORTWC:
			return result.append(" +").toString();
		case LONGWC:
			return result.append(" *").toString();
		case WORD:
			return result.append(" " + node.getContent()).toString();
		case NUMBER:
			return result + " <number>";
		default:
			for (AGFNode gram : node.getChilds()) {
				result.append(generateSentenceFromGrammar(gram));
			}
			break;
		}

		return result.toString();
	}

}
