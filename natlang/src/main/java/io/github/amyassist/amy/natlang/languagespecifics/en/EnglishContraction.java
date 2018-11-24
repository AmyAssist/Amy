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

package io.github.amyassist.amy.natlang.languagespecifics.en;

import io.github.amyassist.amy.natlang.languagespecifics.Contraction;

/**
 * This class disassembling contracted words to the full words. e.g. I'm -&gt; I am
 * 
 * @author Lars Buttgereit
 */
public class EnglishContraction extends Contraction {

	/**
	 * constructor for the English contraction resolving. all contractions are initialized here
	 */
	public EnglishContraction() {
		this.contractions.put("ain't", "am not");
		this.contractions.put("won't", "will not");
		this.contractions.put("shan't", "shall not");
		this.contractions.put("n't", " not");
		this.contractions.put("let's", "let us");
		this.contractions.put("i'm", "i am");
		this.contractions.put("'re", " are");
		this.contractions.put("'ve", " have");
		this.contractions.put("'ll", " will");
		// more than one possibility. is not possible to transform
		this.contractions.put("'d", " d");
		this.contractions.put("'s", " s");
		this.contractions.put("%", " percent");
	}
}
