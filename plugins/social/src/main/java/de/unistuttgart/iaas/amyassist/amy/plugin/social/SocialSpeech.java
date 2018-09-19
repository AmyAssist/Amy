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

package de.unistuttgart.iaas.amyassist.amy.plugin.social;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;

/**
 * A plugin which handles social interactions
 * 
 * @author Felix Burk
 */
@Service
@SpeechCommand
public class SocialSpeech {

	@Reference
	private SocialLogic logic;
	
	private static final int NMB_SAMPLE_SENTENCES = 4;

	/**
	 * returns a greeting
	 * @param entities of input
	 * @return the greeting
	 */
	@Intent
	public String greeting(Map<String, EntityData> entities) {
		return this.logic.getGreeting();
	}
	
	/**
	 * greets user back
	 * @param entities of input
	 * @return the greeting
	 */
	@Intent
	public String userName(Map<String, EntityData> entities) {
		return "Nice to meet you, " + entities.get("name").getString();
	}
	
	/**
	 * tells user amys name
	 * @param entities of input
	 * @return the greeting
	 */
	@Intent
	public String askForName(Map<String, EntityData> entities) {
		return "My name is Amy, nice to meet you";
	}
	
	/**
	 * get a "whats up" answer
	 * @param entities of input
	 * @return the greeting
	 */
	@Intent
	public String whatsUp(Map<String, EntityData> entities) {
		return this.logic.getWhatsUp();
	}
	
	/**
	 * get an answer to "how are you" or similar phrases
	 * @param entities entities of input
	 * @return the answer
	 */
	@Intent
	public String howAreYou(Map<String, EntityData> entities) {
		return this.logic.getHowAreYou();
	}
	
	/**
	 * returns 4 sample sentences that may be told to amy
	 * @param entities of input
	 * @return the answer
	 */
	@Intent
	public String sampleSentences(Map<String, EntityData> entities) {
		StringBuilder s = new StringBuilder();
		s.append("You may ask me for example");
		
		if(entities.get("pluginname") == null) {
			s.append("\n" +  StringUtils.join(
					this.logic.getSampleSentences(NMB_SAMPLE_SENTENCES), "\n"));
		}else {
			String keyword = entities.get("pluginname").getString();
			String[] results = this.logic.getSampleSentencesWithKeyword(keyword, NMB_SAMPLE_SENTENCES);
			if(results[0] != null && !results[0].equals("")) {
				s.append("\n" +  StringUtils.join(
						this.logic.getSampleSentencesWithKeyword(keyword, NMB_SAMPLE_SENTENCES), "\n"));
			} else {
				return "I don't know anything about this";
			}
		}
		return s.toString();
	}
	
	/**
	 * returns the names of all installed plugins
	 * @param entities of input
	 * @return the answer
	 */
	@Intent
	public String getInstalledPlugins(Map<String, EntityData> entities) {
		StringBuilder s = new StringBuilder();
		s.append("These are my currenty installed plugins");
		s.append("\n" +  StringUtils.join(this.logic.getInstalledPluginNames(), ", "));
		s.append("\nfeel free to add more!");
		return s.toString();
	}
	
	/**
	 * returns the description of a plugin
	 * @param entities of input
	 * @return the answer
	 */
	@Intent
	public String tellMeAboutPlugin(Map<String, EntityData> entities) {
		String pluginName = entities.get("plugin").getString();
		String info = this.logic.getPluginInformation(pluginName);
		if(info != null) {
			return info;
		}
		return "i don't know about " + pluginName + " yet";
	}

}




