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

import java.util.UUID;

import org.slf4j.Logger;

import asg.cliche.Command;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.IDialogHandler;

/**
 * Console commands to interact with Amy like speaking with her.
 * 
 * @author Leon Kiefer
 */
public class SpeechConsole {
	@Reference
	private Logger logger;
	@Reference
	private IDialogHandler handler;
	private UUID dialog;

	@PostConstruct
	private void setup() {
		this.dialog = this.handler.createDialog(System.out::println);// NOSONAR
	}

	@Command
	public void say(String... speechInput) {
		this.handler.process(String.join(" ", speechInput), this.dialog);
	}
}
