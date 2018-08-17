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
 * this class contains pre defined entity types 
 * all of them have to start with "amy" to prevent double usage
 * 
 * feel free to add more - keep in mind that they are saved in agf form
 * 
 * @author Lars Buttgereit, Felix Burk
 */
public class PreDefinedEntityTypes {
	
	private PreDefinedEntityTypes() {
		//hide constructor
	}
		
	private static Map<String, AGFNode> map;
	private static final Map<String, String> grammars; 
	static{
		grammars = new HashMap<>();
		grammars.put("amyinteger", "$(0,1000000000, 1)");
		grammars.put("amyhour", "$(0,24,1)");
		grammars.put("amyminute", "$(0,60,1)");
	    grammars.put("amytime", "{amyhour} oh {amyminute}");
	}
	
	private static final String[] ids =  {
		"amyinteger", "amyhour", "amyminute", "amytime"
	};

	/**
	 * returns a hashmap of pre defined types
	 * @return the hashmap
	 */
	public static Map<String, AGFNode> getTypes(){
		if(map == null) {
			map = new HashMap<>();
			generateAGFNodes(grammars);
		}
		
		return map;
	}

	/**
	 * helper method for generation agfNodes from strings
	 * @param grmrs hashmap of grammars to generate
	 */
	private static void generateAGFNodes(Map<String, String> grmrs) {
		for(String s : ids) {
			AGFLexer lex = new AGFLexer(grmrs.get(s));
			AGFParser parser = new AGFParser(lex, map);
			map.put(s.toLowerCase(), parser.parseWholeExpression());
		}
	}
}
