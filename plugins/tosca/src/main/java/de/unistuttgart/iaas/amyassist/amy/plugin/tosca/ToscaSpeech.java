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

/**
 * The speech class for the tosca plugin
 * 
 * @author Felix Burk
 */
@Service
@SpeechCommand
public class ToscaSpeech {

	@Reference
	private ToscaLogic logic;

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

	@EntityProvider("config")
	public List<String> provideConfigurations() {
		return this.logic.getConfigurations().stream().map(c -> c.getTag()).collect(Collectors.toList());
	}

	@EntityProvider("app")
	public List<String> provideApps() {
		return this.logic.getInstalledApps().stream().map(a -> a.getDisplayName()).collect(Collectors.toList());
	}

}
