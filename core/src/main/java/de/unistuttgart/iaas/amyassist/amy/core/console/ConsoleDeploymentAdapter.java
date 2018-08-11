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

package de.unistuttgart.iaas.amyassist.amy.core.console;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.information.ProgramInformationCLI;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManagerCLI;
import de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService;
import de.unistuttgart.iaas.amyassist.amy.core.speech.tts.TTSConsole;

/**
 * The adapter that deploy all Console commands.
 * 
 * @author Leon Kiefer
 */
@Service(ConsoleDeploymentAdapter.class)
public class ConsoleDeploymentAdapter implements DeploymentContainerService {
	@Reference
	private Console console;

	@Reference
	private ServiceLocator serviceLocator;

	@Override
	public void deploy() {
		this.console.register(this.serviceLocator.createAndInitialize(PluginManagerCLI.class));
		this.console.register(this.serviceLocator.createAndInitialize(TTSConsole.class));
		this.console.register(this.serviceLocator.createAndInitialize(SpeechConsole.class));
		this.console.register(this.serviceLocator.createAndInitialize(ExitConsole.class));
		this.console.register(this.serviceLocator.createAndInitialize(ProgramInformationCLI.class));
	}

}
