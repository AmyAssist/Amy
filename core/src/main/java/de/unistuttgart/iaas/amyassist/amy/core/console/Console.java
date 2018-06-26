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

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import asg.cliche.Command;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandler;
import de.unistuttgart.iaas.amyassist.amy.core.Core;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManagerCLI;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;

/**
 * The Console reads input from the command line and pass it to the TextParser
 * 
 * @author Leon Kiefer
 */
@Service(Console.class)
public class Console implements SpeechIO {
	@Reference
	private Logger logger;

	@Reference
	private ServiceLocator serviceLocator;

	@Reference
	private Core core;

	private SpeechInputHandler handler;

	@Command
	public String say(String... speechInput) {
		try {
			return this.handler.handle(String.join(" ", speechInput)).get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			this.logger.error("Error while handling input {}", speechInput, e.getCause());
		}
		return "";
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			Shell shell = ShellFactory.createConsoleShell("amy", "", this);
			shell.addMainHandler(this.serviceLocator.createAndInitialize(PluginManagerCLI.class), "");
			shell.addMainHandler(new CommandLineArgumentHandler(), "");
			shell.commandLoop();
		} catch (IOException e) {
			this.logger.error("Error while running the console", e);
		}
		this.core.stop();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO#setSpeechInputHandler(de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler)
	 */
	@Override
	public void setSpeechInputHandler(SpeechInputHandler handler) {
		this.handler = handler;
	}
}
