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

package io.github.amyassist.amy.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.amyassist.amy.core.di.Context;
import io.github.amyassist.amy.core.di.DependencyInjection;
import io.github.amyassist.amy.core.di.consumer.ServiceConsumerImpl;
import io.github.amyassist.amy.core.di.provider.SingletonServiceProvider;
import io.github.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;
import io.github.amyassist.amy.core.io.CommandLineArgumentHandlerService;
import io.github.amyassist.amy.core.io.CommandLineArgumentInfo;
import io.github.amyassist.amy.core.pluginloader.PluginManager;
import io.github.amyassist.amy.core.pluginloader.PluginProvider;
import io.github.amyassist.amy.core.service.DeploymentContainerServiceExtension;
import io.github.amyassist.amy.core.service.RunnableServiceExtension;

/**
 * The central core of the application
 *
 * @author Tim Neumann, Leon Kiefer
 */
public class Core {

	private static final int EXIT_CODE_ALL_GOOD = 0;
	private static final int EXIT_CODE_CMA_FLAGS_INVALID = 11;

	private final Logger logger = LoggerFactory.getLogger(Core.class);

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
	 * The method executed by the main method
	 *
	 * @param args
	 *            The arguments for the core.
	 */
	void start(String[] args) {
		this.registerAllCoreServices();
		this.init();
		CommandLineArgumentHandlerService cmaHandler = this.di.getServiceLocator()
				.getService(new ServiceConsumerImpl<>(this.getClass(),
						new ServiceDescriptionImpl<>(CommandLineArgumentHandlerService.class)))
				.getService();
		cmaHandler.load(args, System.out::println);
		if (cmaHandler.shouldProgramContinue()) {
			this.di.getConfiguration()
					.register(new SingletonServiceProvider<>(CommandLineArgumentInfo.class, cmaHandler.getInfo()));
			this.run();
		} else {
			System.exit(cmaHandler.areFlagsValid() ? EXIT_CODE_ALL_GOOD : EXIT_CODE_CMA_FLAGS_INVALID); // NOSONAR
		}
	}

	/**
	 * The main entry point for the real core logic.
	 */
	private void run() {
		this.logger.debug("run");
		this.loadPlugins();
		this.deploy();
		this.start();
		this.logger.debug("running");
	}

	/**
	 * register all instances and classes in the DI
	 */
	private void registerAllCoreServices() {
		this.di.loadServices();
	}

	/**
	 * Initializes the core
	 */
	private void init() {
		// nothing in init stage
	}

	private void loadPlugins() {
		PluginManager pluginManager = this.di.getServiceLocator()
				.getService(
						new ServiceConsumerImpl<>(this.getClass(), new ServiceDescriptionImpl<>(PluginManager.class)))
				.getService();
		try {
			pluginManager.loadPlugins();
		} catch (IOException e) {
			throw new IllegalStateException("Could not load plugins due to an IOException.", e);
		}
		this.di.getConfiguration().registerContextProvider(Context.PLUGIN,
				new PluginProvider(pluginManager.getPlugins()));
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

	private void doStop() {
		this.logger.debug("stop");
		this.runnableServiceExtension.stop();
		this.logger.debug("stopped");
	}

}
