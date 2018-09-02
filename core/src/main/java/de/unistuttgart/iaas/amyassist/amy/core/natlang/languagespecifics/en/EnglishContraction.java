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

import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.Contraction;

/**
 * This class disassembling contracted words to the full words. e.g. I'm -> I am
 * 
 * @author Lars Buttgereit
 */
public class EnglishContraction implements Contraction {

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.Contraction#disassemblingContraction(java.lang.String)
	 */
	@Override
	public String disassemblingContraction(String toDisassemble) {
		String disassembled = toDisassemble;
		disassembled = disassembled.replaceAll("ain't", "am not");
		disassembled = disassembled.replaceAll("won't", "will not");
		disassembled = disassembled.replaceAll("shan't", "shall not");
		disassembled = disassembled.replaceAll("n't", " not");
		disassembled = disassembled.replaceAll("let's", "let us");
		disassembled = disassembled.replaceAll("i'm", "i am");
		disassembled = disassembled.replaceAll("'re", " are");
		disassembled = disassembled.replaceAll("'ve", " have");
		disassembled = disassembled.replaceAll("'ll", " will");
		// more than one possibility. is not possible to transform
		disassembled = disassembled.replaceAll("'d", " d");
		disassembled = disassembled.replaceAll("'s", " s");
		return disassembled;
	}
}
