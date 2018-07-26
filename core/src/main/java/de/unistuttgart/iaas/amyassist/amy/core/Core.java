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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.console.Console;
import de.unistuttgart.iaas.amyassist.amy.core.di.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginProvider;
import de.unistuttgart.iaas.amyassist.amy.core.service.ServiceManagerImpl;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
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

	private final Logger logger = LoggerFactory.getLogger(Core.class);

	private ServiceManagerImpl serviceManager;
	private ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();

	private DependencyInjection di = new DependencyInjection();

	private CommandLineArgumentHandlerService cmaHandler;

	private Thread shutdownHook = new Thread(this::doStop, "ShutdownHook");

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
		this.registerAllCoreServices();
		this.init();
		this.loadPlugins();
		this.deploy();
		this.start();
		this.logger.info("running");
	}

	/**
	 * register all instances and classes in the DI
	 */
	private void registerAllCoreServices() {
		this.di.addExternalService(Core.class, this);
		this.di.addExternalService(CommandLineArgumentHandler.class, this.cmaHandler);

		this.di.loadServices();
	}

	/**
	 * Initializes the core
	 */
	private void init() {
		this.serviceManager = this.di.getService(ServiceManagerImpl.class);
		this.serviceManager.loadServices();
	}

	private void loadPlugins() {
		PluginManager pluginManager = this.di.getService(PluginManager.class);
		pluginManager.loadPlugins();
		this.di.registerContextProvider(Context.PLUGIN, new PluginProvider(pluginManager.getPlugins()));
	}

	private void deploy() {
		this.di.getService(GrammarObjectsCreator.class).completeSetup();

		Server server = this.di.getService(Server.class);
		server.register(HomeResource.class);
		server.register(LocationRegistryResource.class);
		server.register(ContactRegistryResource.class);
	}

	private void start() {
		this.serviceManager.start();
		Runtime.getRuntime().addShutdownHook(this.shutdownHook);
	}

	/**
	 * stop all Threads and terminate the application. This is call form the {@link Console}
	 */
	public void stop() {
		Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
		this.doStop();
	}

	private void doStop() {
		this.logger.info("stop");
		this.serviceManager.stop();
		this.singleThreadScheduledExecutor.shutdownNow();
		this.di.shutdown();
		this.logger.info("stopped");
	}

}
