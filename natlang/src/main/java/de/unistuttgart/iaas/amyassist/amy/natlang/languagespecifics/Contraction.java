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

package de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * This interface has methods to disassembling contracted words to the full words. e.g. I'm -> I am
 * 
 * @author Lars Buttgereit
 */
public abstract class Contraction {

	/**
	 * contains all replacements for all contractions
	 */
	protected  Map<String, String> contractions = new HashMap<>();
	private Pattern pattern;

	/**
	 * make a pattern out of all contractions. call this in the constructor of a subclass with a hashmap with all
	 * contractions
	 * 
	 */
	private void compilePattern() {

		String patternString = "(" + StringUtils.join(this.contractions.keySet(), "|") + ")";
		this.pattern = Pattern.compile(patternString);
	}

	/**
	 * this method disassembling contracted words to the full words.
	 * 
	 * @param toDisassemble
	 *            string to disassembling
	 * @return the disassembled string
	 */
	public String disassemblingContraction(String toDisassemble) {
		if(this.pattern == null) {
			compilePattern();
		}
		Matcher matcher = this.pattern.matcher(toDisassemble);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, this.contractions.get(matcher.group(1)));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
