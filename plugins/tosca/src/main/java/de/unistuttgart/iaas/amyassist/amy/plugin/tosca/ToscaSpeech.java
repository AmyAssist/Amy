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

package de.unistuttgart.iaas.amyassist.amy.plugin.tosca;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.opentosca.containerapi.client.model.Application;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityProvider;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.plugin.tosca.configurations.ConfigurationEntry;

/**
 * The speech class for the tosca plugin
 * 
 * @author Felix Burk, Tim Neumann
 */
@Service
@SpeechCommand
public class ToscaSpeech {

	@Reference
	private ToscaLogic logic;

	/**
	 * Speech method to lists the apps
	 * 
	 * @param entities
	 *            The speech entities. Not used.
	 * @return A string to output, which contains the number of installed apps and their names.
	 */
	@Intent
	public String listApps(Map<String, EntityData> entities) {
		List<Application> apps = this.logic.getInstalledApps();

		StringBuilder ret = new StringBuilder("Found ");

		ret.append(apps.size());
		ret.append(" application");
		if (apps.size() != 1) {
			ret.append('s');
		}
		ret.append(". ");

		apps.forEach(a -> {
			ret.append(a.getDisplayName());
			ret.append(" and ");
		});
		ret.setLength(ret.length() - 5);
		ret.append(".");

		return ret.toString();
	}

	/**
	 * Speech method o install a plugin. The name of the app and the config are in the entities under app and config
	 * respectively
	 * 
	 * @param entities
	 *            The speech entities to use
	 * @return A string to output which contains any error or a confirmation that installation is in progres
	 */
	@Intent
	public String install(Map<String, EntityData> entities) {
		String app = entities.get("app").getString();
		String config = entities.get("config").getString();
		try {
			this.logic.install(app, config);
		} catch (NoSuchElementException | IllegalArgumentException e) {
			return e.getMessage();
		}

		return "Installing.";
	}

	/**
	 * Provides the names of the configurations
	 * 
	 * @return A list of config names.
	 */
	@EntityProvider("config")
	public List<String> provideConfigurations() {
		return this.logic.getConfigurations().stream().map(ConfigurationEntry::getTag).collect(Collectors.toList());
	}

	/**
	 * Provides the names of the apps
	 * 
	 * @return A list of app names.
	 */
	@EntityProvider("app")
	public List<String> provideApps() {
		return this.logic.getInstalledApps().stream().map(Application::getDisplayName).collect(Collectors.toList());
	}

}
