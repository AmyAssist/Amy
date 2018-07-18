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

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.console.Console;
import de.unistuttgart.iaas.amyassist.amy.core.di.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginProvider;
import de.unistuttgart.iaas.amyassist.amy.core.speech.LocalAudioUserInteraction;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.TaskScheduler;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;
import de.unistuttgart.iaas.amyassist.amy.registry.rest.ContactRegistryResource;
import de.unistuttgart.iaas.amyassist.amy.registry.rest.LocationRegistryResource;
import de.unistuttgart.iaas.amyassist.amy.restresources.home.HomeResource;

/**
 * The central core of the application
 *
 * @author Tim Neumann, Leon Kiefer
 */
public class Core {
	private static final String CONFIG_NAME = "core.config";
	private static final String PROPERTY_ENABLE_CONSOLE = "enableConsole";

	private final Logger logger = LoggerFactory.getLogger(Core.class);

	private List<Thread> threads;
	private ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();

	private DependencyInjection di = new DependencyInjection();

	private Server server;
	private LocalAudioUserInteraction recognizer;

	private CommandLineArgumentHandlerService cmaHandler;

	private Properties config;

	/**
	 * Get's {@link #singleThreadScheduledExecutor singleThreadScheduledExecutor}
	 * 
	 * @return singleThreadScheduledExecutor
	 */
	public ScheduledExecutorService getScheduledExecutor() {
		return this.singleThreadScheduledExecutor;
	}

	/**
	 * The method executed by the main method
	 *
	 * @param args
	 *            The arguments for the core.
	 */
	void start(String[] args) {
		this.cmaHandler = new CommandLineArgumentHandlerService();
		this.cmaHandler.init(args);
		if (this.cmaHandler.shouldProgramContinue()) {
			run();
		}
	}

	/**
	 * The main entry point for the real core logic.
	 */
	private void run() {
		this.logger.info("run");
		this.init();
		this.threads.forEach(Thread::start);
		this.recognizer.start();
		this.server.start();
		this.logger.info("running");
	}

	/**
	 * Initializes the core
	 */
	private void init() {
		this.registerAllCoreServices();
		ConfigurationLoader configLoader = this.di.getService(ConfigurationLoader.class);
		this.config = configLoader.load(CONFIG_NAME);

		this.threads = new ArrayList<>();

		this.server = this.di.getService(Server.class);
		this.server.register(HomeResource.class);
		this.server.register(LocationRegistryResource.class);
		this.server.register(ContactRegistryResource.class);

		initConsole();

		PluginManager pluginManager = this.di.getService(PluginManager.class);
		pluginManager.loadPlugins();
		this.di.registerContextProvider(Context.PLUGIN, new PluginProvider(pluginManager.getPlugins()));

		this.di.getService(GrammarObjectsCreator.class).completeSetup();
		this.recognizer = this.di.getService(LocalAudioUserInteraction.class);
		this.recognizer.init();
	}

	private void initConsole() {
		String enableConsole = this.config.getProperty(PROPERTY_ENABLE_CONSOLE);
		if (enableConsole == null) {
			this.logger.warn("Core config missing key {}.", PROPERTY_ENABLE_CONSOLE);
			enableConsole = "true";
		}
		if (enableConsole.equals("true")) {
			Console console = this.di.getService(Console.class);
			this.threads.add(new Thread(console));
		}
	}

	/**
	 * register all instances and classes in the DI
	 */
	private void registerAllCoreServices() {
		this.di.addExternalService(DependencyInjection.class, this.di);
		this.di.addExternalService(Core.class, this);
		this.di.addExternalService(TaskSchedulerAPI.class, new TaskScheduler(this.singleThreadScheduledExecutor));
		this.di.addExternalService(CommandLineArgumentHandler.class, this.cmaHandler);


		this.di.loadServices();
	}

	/**
	 * stop all Threads and terminate the application. This is call form the {@link Console}
	 */
	public void stop() {
		this.logger.info("stop");
		this.server.shutdown();
		this.recognizer.stop();
		this.threads.forEach(Thread::interrupt);
		this.singleThreadScheduledExecutor.shutdownNow();
		this.di.shutdown();
		this.logger.info("stopped");
	}

}
