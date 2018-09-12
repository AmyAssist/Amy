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

package de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.en;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.SpecialCharacterConversion;

/**
 * Special Character conversion for the english language
 * 
 * @author Felix Burk
 */
public class EnglishSpecialCharacterConversion implements SpecialCharacterConversion {
	
	private Map<String, String> conversion;
	
	public EnglishSpecialCharacterConversion() {
		this.conversion = new HashMap<>();
		
		this.conversion.put("%", "percent");
		this.conversion.put("$", "dollar");
		this.conversion.put("€", "euro");
		this.conversion.put("¢", "cent");
		this.conversion.put("£", "pound");
		this.conversion.put("¥", "yen");
		this.conversion.put("°", "degree");
	}
	
	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.SpecialCharacterConversion#format(java.lang.String)
	 */
	@Override
	public String format(String toFormat) {
		String result = toFormat;
		//this is kinda ugly buuut regex and special characters is pretty bad
		for(Entry<String, String> e : this.conversion.entrySet()) {
			if(result.contains(e.getKey())) {
				result = toFormat.replaceAll(e.getKey(), e.getValue());
			}
		}
		
		return result;
	}

}
