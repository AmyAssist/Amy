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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.PropertiesProvider;
import de.unistuttgart.iaas.amyassist.amy.core.console.Console;
import de.unistuttgart.iaas.amyassist.amy.core.di.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.io.EnvironmentService;
import de.unistuttgart.iaas.amyassist.amy.core.logger.LoggerProvider;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginProvider;
import de.unistuttgart.iaas.amyassist.amy.core.speech.AudioUserInteraction;
import de.unistuttgart.iaas.amyassist.amy.core.speech.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommandHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.TaskScheduler;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;

/**
 * The central core of the application
 *
 * @author Tim Neumann, Leon Kiefer
 */
public class Core implements SpeechInputHandler {

	private final Logger logger = LoggerFactory.getLogger(Core.class);

	private List<Thread> threads;
	private ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	private DependencyInjection di = new DependencyInjection();

	private Server server;
	private SpeechCommandHandler speechCommandHandler;
	private IStorage storage = new Storage("", new GlobalStorage());

	private CommandLineArgumentHandlerService cmaHandler;

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
		this.init();
		this.threads.forEach(Thread::start);
		this.server.start();
	}

	/**
	 * Initializes the core
	 */
	private void init() {
		this.registerAllCoreServices();
		this.speechCommandHandler = this.di.getService(SpeechCommandHandler.class);
		this.threads = new ArrayList<>();

		this.server = this.di.getService(Server.class);

		Console console = this.di.getService(Console.class);
		console.setSpeechInputHandler(this);
		this.threads.add(new Thread(console));

		Environment environment = this.di.getService(Environment.class);

		Path grammarFile = environment.getWorkingDirectory().resolve("resources")
				.resolve("sphinx-grammars/grammar.gram");

		this.speechCommandHandler.setFileToSaveGrammarTo(grammarFile.toFile());

		AudioUserInteraction aui = this.di.createAndInitialize(AudioUserInteraction.class);
		aui.setGrammars(new Grammar("grammar", grammarFile.toFile()), null);
		aui.setSpeechInputHandler(this);
		this.threads.add(new Thread(aui));

		PluginManager pluginManager = this.di.getService(PluginManager.class);
		pluginManager.loadPlugins();
		this.di.registerContextProvider(Context.PLUGIN, new PluginProvider(pluginManager.getPlugins()));
		this.speechCommandHandler.completeSetup();
	}

	/**
	 * register all instances and classes in the DI
	 */
	private void registerAllCoreServices() {
		this.di.addExternalService(IStorage.class, this.storage);
		this.di.addExternalService(DependencyInjection.class, this.di);
		this.di.addExternalService(Core.class, this);
		this.di.addExternalService(TaskSchedulerAPI.class, new TaskScheduler(this.singleThreadScheduledExecutor));
		this.di.addExternalService(CommandLineArgumentHandler.class, this.cmaHandler);

		this.di.register(Logger.class, new LoggerProvider());
		this.di.register(Properties.class, new PropertiesProvider());

		this.di.register(Server.class);
		this.di.register(ConfigurationImpl.class);
		this.di.register(Console.class);
		this.di.register(SpeechCommandHandler.class);
		this.di.register(ConfigurationLoader.class);
		this.di.register(PluginLoader.class);
		this.di.register(PluginManager.class);
		this.di.register(EnvironmentService.class);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler#handle(java.lang.String)
	 */
	@Override
	public Future<String> handle(String speechInput) {
		return this.singleThreadScheduledExecutor.submit(() -> {
			return this.speechCommandHandler.handleSpeechInput(speechInput);
		});
	}

	/**
	 * stop all Threads and terminate the application. This is call form the {@link Console}
	 */
	public void stop() {
		this.server.shutdown();
		this.threads.forEach(Thread::interrupt);
		this.singleThreadScheduledExecutor.shutdownNow();
	}

}
