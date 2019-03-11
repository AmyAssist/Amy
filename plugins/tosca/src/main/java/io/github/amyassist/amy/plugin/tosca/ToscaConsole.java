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

package io.github.amyassist.amy.plugin.tosca;

import java.util.List;
import java.util.NoSuchElementException;

import org.opentosca.container.client.model.Application;

import asg.cliche.Command;
import io.github.amyassist.amy.core.di.annotation.Reference;

/**
 * The console class for the tosca plugin
 * 
 * @author Leon Kiefer, Tim Neumann
 */
public class ToscaConsole {

	@Reference
	private ToscaLogic logic;

	/**
	 * List all apps
	 * 
	 * @return A string to output
	 */
	@Command
	public String listApps() {
		List<Application> apps = this.logic.getInstalledApps();

		StringBuilder ret = new StringBuilder("Found ");

		ret.append(apps.size());
		ret.append(" application");
		if (apps.size() != 1) {
			ret.append('s');
		}
		ret.append(": \n");

		apps.forEach(a -> {
			ret.append("  ");
			ret.append(a.getDisplayName());
			ret.append(" : ");
			ret.append(a.getDescription());
			ret.append("\n ");
		});
		ret.setLength(ret.length() - 2);

		return ret.toString();
	}

	/**
	 * Install a app
	 * 
	 * @param app
	 *            The app name
	 * @param config
	 *            The config name
	 * 
	 * @return A string to output.
	 */
	@Command
	public String install(String app, String config) {
		try {
			this.logic.install(app, config);
		} catch (NoSuchElementException | IllegalArgumentException e) {
			return e.getMessage();
		}

		return "Installing.";

	}
}
