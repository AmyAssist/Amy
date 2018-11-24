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

package io.github.amyassist.amy.plugin.social;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.github.amyassist.amy.core.Configuration;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.natlang.NatlangInformation;

/**
 * Logic for this social plugin
 * 
 * @author Felix Burk, Patrick Singer, Florian Bauer
 */
@Service
public class SocialLogic {

	/**
	 * receive information about possible grammars to use
	 */
	@Reference
	NatlangInformation info;

	/**
	 * provides information about installed plugins
	 */
	@Reference
	Configuration constants;

	@Reference
	private JokeAPICaller joker;

	/**
	 * map of internal and human readable plugin names the key are human readable names and values are the internal ones
	 */
	Map<String, String> pluginNames;

	/**
	 * initializes plugin information
	 */
	@PostConstruct
	public void init() {
		String[] pluginInfo = this.constants.getInstalledPlugins();
		String pluginHumanReadableName;
		this.pluginNames = new HashMap<>();

		for (int i = 0; i < pluginInfo.length; i++) {
			pluginHumanReadableName = pluginInfo[i].substring(pluginInfo[i].lastIndexOf("plugin-") + 7,
					pluginInfo[i].length());
			this.pluginNames.put(pluginHumanReadableName, pluginInfo[i]);
		}
	}
	
	/**
	 * receive a random name answer
	 * 
	 * @return her name
	 */
	String getMyNameIs() {
		return generateRandomAnswer(SocialConstants.myNameIs);
	}


	/**
	 * receive a random greeting
	 * 
	 * @return the greeting string
	 */
	String getGreeting() {
		return generateRandomAnswer(SocialConstants.greeting);
	}

	/**
	 * receive a random string answer
	 * 
	 * @return answer
	 */
	String getWhatsUp() {
		return generateRandomAnswer(SocialConstants.whatsUp);
	}

	/**
	 * receive a random string answer
	 * 
	 * @return answer
	 */
	String getHowAreYou() {
		return generateRandomAnswer(SocialConstants.howAreYou);
	}

	/**
	 * receive a random string answer
	 * 
	 * @return answer
	 */
	String getFoxSays() {
		return generateRandomAnswer(SocialConstants.foxSays);
	}
	
	/**
	 * receive a random string answer
	 * 
	 * @return answer
	 */
	String getOneDoesNotSimply() {
		return generateRandomAnswer(SocialConstants.oneDoesNotSimply);
	}
	
	/**
	 * receive a number of sample sentences
	 * 
	 * @param nmbSentences
	 *            number of sentences
	 * @return the sentences
	 */
	String[] getSampleSentences(int nmbSentences) {
		return this.info.getAnySampleSentences(nmbSentences).toArray(new String[nmbSentences]);
	}

	/**
	 * receive a number of sample sentences containing a keyword
	 * 
	 * @param nmbSentences
	 *            number of sentences
	 * @param keyword
	 *            to match
	 * @return the sentences
	 */
	String[] getSampleSentencesWithKeyword(String keyword, int nmbSentences) {
		return this.info.getSampleSentencesFromKeyword(keyword, nmbSentences).toArray(new String[nmbSentences]);
	}

	/**
	 * provides a list of installed plugin names
	 * 
	 * @return the list
	 */
	String[] getInstalledPluginNames() {
		return this.pluginNames.keySet().toArray(new String[this.pluginNames.size()]);

	}

	/**
	 * provides information about a specific plugin
	 * 
	 * @param pluginname
	 *            name of the plugin
	 * @return the information
	 */
	String getPluginInformation(String pluginname) {
		if (this.pluginNames.get(pluginname) != null) {
			return this.constants.getPluginDescription(this.pluginNames.get(pluginname));
		}
		return null;
	}

	/**
	 * Get a random joke from an API
	 * 
	 * @return a random joke
	 */
	String getJoke() {
		return this.joker.getRandomJoke();
	}

	/**
	 * generates a random answer from a string array
	 * 
	 * @param strings
	 *            possible to select
	 * @return selected string
	 */
	private String generateRandomAnswer(String[] strings) {
		Random rand = new Random();
		int rndm = rand.nextInt(strings.length);
		return strings[rndm];
	}
}
