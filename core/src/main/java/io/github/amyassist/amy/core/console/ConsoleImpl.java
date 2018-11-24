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

package io.github.amyassist.amy.core.console;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.service.RunnableService;

/**
 * The Console reads input from the command line and pass it to the TextParser
 * 
 * @author Leon Kiefer
 */
@Service(Console.class)
public class ConsoleImpl implements RunnableService, Runnable, Console {
	private static final String CONFIG_NAME = "core.config";
	private static final String PROPERTY_ENABLE_CONSOLE = "enableConsole";

	@Reference
	private Logger logger;

	@Reference
	private ConfigurationManager configurationmanager;

	private boolean enable;

	private Thread thread;

	private Set<Object> mainHandlers = new HashSet<>();

	@PostConstruct
	private void init() {
		this.enable = Boolean.valueOf(this.configurationmanager.getConfigurationWithDefaults(CONFIG_NAME)
				.getProperty(PROPERTY_ENABLE_CONSOLE, "true"));
	}

	@Override
	public void register(Object handler) {
		this.mainHandlers.add(handler);
	}

	@Override
	public void start() {
		if (this.enable) {
			this.thread = new Thread(this, "Console");
			this.thread.start();
		}
	}

	@Override
	public void run() {
		try {
			Shell shell = ShellFactory.createConsoleShell("amy", "", this.mainHandlers.toArray());
			shell.getOutputConverter().addConverter(toBeFormatted -> {
				if (toBeFormatted instanceof Throwable) {
					this.logger.error("Error while handling console command", (Throwable) toBeFormatted);
					return "";
				}
				return null;
			});
			shell.commandLoop();
		} catch (IOException e) {
			this.logger.error("Error while running the console", e);
		}
	}

	@Override
	public void stop() {
		if (this.enable) {
			this.thread.interrupt();
		}
	}
}
