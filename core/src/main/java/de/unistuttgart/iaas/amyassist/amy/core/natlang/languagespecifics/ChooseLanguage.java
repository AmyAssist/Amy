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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.en.EnglishNumberConversion;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.en.EnglishStemmer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.en.EnglishTimeUtility;

/**
 * 
 * @author Lars Buttgereit
 */
public class ChooseLanguage {
	private IStemmer stemmer;
	private INumberConversion numberConversion;
	private ITimeUtility timeUtility;

	/**
	 * constructor for choose language
	 * 
	 * @param language
	 *                           which language should be used (language code after ISO 639-1)
	 * @param stemmerEnabled
	 *                           if stemmerEnabled true the correct stemmer is set, false stemmer is null
	 */
	public ChooseLanguage(String language, boolean stemmerEnabled) {
		switch (language.toLowerCase()) {
		case "en":
		default:
			if (stemmerEnabled) {
				this.stemmer = new EnglishStemmer();
			}
			this.numberConversion = new EnglishNumberConversion();
			this.timeUtility = new EnglishTimeUtility();
			break;
		}
	}

	/**
	 * Get's {@link #stemmer stemmer}
	 * 
	 * @return stemmer
	 */
	public IStemmer getStemmer() {
		return this.stemmer;
	}

	/**
	 * Get's {@link #numberConversion numberConversion}
	 * 
	 * @return numberConversion
	 */
	public INumberConversion getNumberConversion() {
		return this.numberConversion;
	}

	/**
	 * Get's {@link #timeUtility timeUtility}
	 * @return  timeUtility
	 */
	public ITimeUtility getTimeUtility() {
		return this.timeUtility;
	}
	
	
}
