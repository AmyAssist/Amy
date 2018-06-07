/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;

/**
 * The Console reads input from the command line and pass it to the TextParser
 * 
 * @author Leon Kiefer
 */
@Service(Console.class)
public class Console implements SpeechIO {

	private Logger logger = LoggerFactory.getLogger(Console.class);

	@Reference
	private Configuration configuration;

	@Reference
	private Core core;

	private SpeechInputHandler handler;

	@Command
	public String say(String... speechInput) {
		try {
			return this.handler.handle(String.join(" ", speechInput)).get();
		} catch (InterruptedException | ExecutionException e) {
			this.logger.error("Error while handling input {}", speechInput, e);
		}
		return "";
	}

	@Command
	public String plugin(String command) {
		switch (command) {
		case "list":
			return String.join("\n", this.configuration.getInstalledPlugins());

		default:
			this.logger.warn("command {} doesn't exists", command);
			return "command doesn't exists";
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			ShellFactory.createConsoleShell("amy", "", this).commandLoop();
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
