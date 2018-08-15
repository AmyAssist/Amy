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

import java.util.HashMap;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;

/**
 * TODO: Description
 * @author Lars Buttgereit, Felix Burk
 */
public class PreDefinedEntityTypes {
		
	private static Map<String, AGFNode> map;
	private static final Map<String, String> grammars; 
	static{
		grammars = new HashMap<>();
		grammars.put("AmyInteger", "(one | two | three | four | five | six | seven |nine | ten "
				+ "| eleven | twelve | thirteen | fourteen | fifteen | sixteen | seventeen | eighteen | nineteen "
				+ "| twenty | thirty | forty | fifty | sixty  | seventy | eighty | ninety )"
				+ "[one | two | three | four | five | six | seven | nine | ten "
				+ "| eleven | twelve | thirteen | fourteen | fifteen | sixteen | seventeen | eighteen | nineteen "
				+ "| twenty | thirty | forty | fifty | sixty  | seventy | eighty | ninety ]");
		grammars.put("AmyHour", "(one | two | three | four | five | six | seven |nine | ten "
				+ "| eleven | twelve | thirteen | fourteen | fifteen | sixteen | seventeen | eighteen | nineteen "
				+ "| twenty)");
		grammars.put("AmyMinute", "(one | two | three | four | five | six | seven |nine | ten "
				+ "| eleven | twelve | thirteen | fourteen | fifteen | sixteen | seventeen | eighteen | nineteen "
				+ "| twenty | thirty | forty | fifty | sixty )");
	    grammars.put("AmyTime", "{AmyHour} oh {AmyMinute}");
	};
	
	private static final String[] ids =  {
		"AmyInteger", "AmyHour", "AmyMinute", "AmyTime"
	};

	/**
	 * @return
	 */
	public static Map<String, AGFNode> getTypes(){
		if(map == null) {
			map = new HashMap<>();
			generateAGFNodes(grammars);
		}
		
		return map;
	}

	/**
	 * @param grmrs 
	 */
	private static void generateAGFNodes(Map<String, String> grmrs) {
		for(String s : ids) {
			System.out.println(s);
			AGFLexer lex = new AGFLexer(grmrs.get(s));
			AGFParser parser = new AGFParser(lex, map);
			map.put(s.toLowerCase(), parser.parseWholeExpression());
		}
	}
}
