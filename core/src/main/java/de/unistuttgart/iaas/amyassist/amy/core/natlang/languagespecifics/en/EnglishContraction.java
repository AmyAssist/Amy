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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.en;

import java.util.HashMap;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.Contraction;

/**
 * This class disassembling contracted words to the full words. e.g. I'm -> I am
 * 
 * @author Lars Buttgereit
 */
public class EnglishContraction extends Contraction {

	/**
	 * constructor for the English contraction resolving. all contractions are initialized here
	 */
	public EnglishContraction() {
		Map<String, String> contractions = new HashMap<>();
		contractions.put("ain't", "am not");
		contractions.put("won't", "will not");
		contractions.put("shan't", "shall not");
		contractions.put("n't", " not");
		contractions.put("let's", "let us");
		contractions.put("i'm", "i am");
		contractions.put("'re", " are");
		contractions.put("'ve", " have");
		contractions.put("'ll", " will");
		// more than one possibility. is not possible to transform
		contractions.put("'d", " d");
		contractions.put("'s", " s");
		compilePattern(contractions);
	}
}
