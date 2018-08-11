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

import de.unistuttgart.iaas.amyassist.amy.core.console.ExitConsole;
import de.unistuttgart.iaas.amyassist.amy.core.di.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.io.CommandLineArgumentHandlerService;
import de.unistuttgart.iaas.amyassist.amy.core.io.CommandLineArgumentInfo;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginProvider;
import de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerServiceExtension;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableServiceExtension;

/**
 * The central core of the application
 *
 * @author Tim Neumann, Leon Kiefer
 */
public class Core {

	private final Logger logger = LoggerFactory.getLogger(Core.class);

	private ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();

	private final DependencyInjection di;

	private Thread shutdownHook = new Thread(this::doStop, "ShutdownHook");

	private RunnableServiceExtension runnableServiceExtension;

	private DeploymentContainerServiceExtension deploymentContainerServiceExtension;

	/**
	 * 
	 */
	public Core() {
		this.runnableServiceExtension = new RunnableServiceExtension();
		this.deploymentContainerServiceExtension = new DeploymentContainerServiceExtension();
		this.di = new DependencyInjection(this.runnableServiceExtension, this.deploymentContainerServiceExtension);
	}

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
		this.registerAllCoreServices();
		this.init();
		CommandLineArgumentHandlerService cmaHandler = this.di.getService(CommandLineArgumentHandlerService.class);
		cmaHandler.load(args, System.out::println);
		if (cmaHandler.shouldProgramContinue()) {
			this.di.addExternalService(CommandLineArgumentInfo.class, cmaHandler.getInfo());
			run();
		}
	}

	/**
	 * The main entry point for the real core logic.
	 */
	private void run() {
		this.logger.info("run");
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

		this.di.loadServices();
	}

	/**
	 * Initializes the core
	 */
	private void init() {
		// nothing in init stage
	}

	private void loadPlugins() {
		PluginManager pluginManager = this.di.getService(PluginManager.class);
		pluginManager.loadPlugins();
		this.di.registerContextProvider(Context.PLUGIN, new PluginProvider(pluginManager.getPlugins()));
	}

	/**
	 * The deploy stage comes after all plugins are loaded
	 */
	private void deploy() {
		this.runnableServiceExtension.deploy();
		this.deploymentContainerServiceExtension.deploy();
	}

	private void start() {
		this.runnableServiceExtension.start();
		Runtime.getRuntime().addShutdownHook(this.shutdownHook);
	}

	/**
	 * stop all Threads and terminate the application. This is call form the {@link ExitConsole}
	 */
	public void stop() {
		Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
		this.doStop();
	}

	private void doStop() {
		this.logger.info("stop");
		this.runnableServiceExtension.stop();
		this.singleThreadScheduledExecutor.shutdownNow();
		this.di.shutdown();
		this.logger.info("stopped");
	}

}
