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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;
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
	/**
	 * The project directory.
	 */
	public static final File projectDir = new File(".").getAbsoluteFile().getParentFile();

	private final Logger logger = LoggerFactory.getLogger(Core.class);

	private List<Thread> threads;
	private ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	private DependencyInjection di = new DependencyInjection();
	private PluginLoader pluginLoader = new PluginLoader();
	private Server server;
	private SpeechCommandHandler speechCommandHandler;
	private IStorage storage = new Storage("", new GlobalStorage());

	/**
	 * The method executed by the main method
	 *
	 */
	void run() {
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

		File resourceDir = new File(projectDir, "resources");
		File grammarFile = new File(resourceDir, "/sphinx-grammars/grammar.gram");

		this.speechCommandHandler.setFileToSaveGrammarTo(grammarFile);

		AudioUserInteraction aui = AudioUserInteraction.getAudioUI();
		aui.setGrammars(new Grammar("grammar", grammarFile), null);

		SpeechIO sr = aui;
		this.di.inject(sr);
		sr.setSpeechInputHandler(this);
		this.threads.add(new Thread(sr));

		this.loadPlugins();
	}

	/**
	 * register all instances and classes in the DI
	 */
	private void registerAllCoreServices() {
		this.di.addExternalService(IStorage.class, this.storage);
		this.di.addExternalService(PluginLoader.class, this.pluginLoader);
		this.di.addExternalService(Core.class, this);
		this.di.addExternalService(TaskSchedulerAPI.class, new TaskScheduler(this.singleThreadScheduledExecutor));

		this.di.register(Server.class);
		this.di.register(ConfigurationImpl.class);
		this.di.register(Console.class);
		this.di.register(SpeechCommandHandler.class);
	}

	/**
	 * load the plugins
	 */
	private void loadPlugins() {
		this.logger.debug("projectDir: {}", projectDir);

		if (!projectDir.exists()) {
			this.logger.error("Project directory does not exist.");
			return;
		}

		File pluginDir = new File(projectDir, "plugins");

		if (!pluginDir.exists()) {
			this.logger
					.error("Plugin directory does not exist. Is the project path correct for your working directory?");
			return;
		}

		ArrayList<File> plugins = new ArrayList<>();
		plugins.add(new File(pluginDir, "alarmclock"));
		plugins.add(new File(pluginDir, "example"));
		plugins.add(new File(pluginDir, "spotify"));
		plugins.add(new File(pluginDir, "systemtime"));
		plugins.add(new File(pluginDir, "weather"));

		for (File p : plugins) {
			if (!p.exists()) {
				this.logger.warn("The plugin {} does not exist in the plugin directory.", p.getName());
				continue;
			}
			File target = new File(p, "target");
			if (!target.exists()) {
				this.logger.warn("Plugin {} has no target directory. Did you run mvn install?", p.getName());
				continue;
			}

			for (File child : target.listFiles()) {
				if (child.getName().endsWith("with-dependencies.jar")) {
					this.pluginLoader.loadPlugin(child.toURI());
					break;
				}
			}
		}

		for (IPlugin p : this.pluginLoader.getPlugins()) {
			this.processPlugin(p);
		}
		this.speechCommandHandler.completeSetup();
	}

	/**
	 * Process the plugin components and register them at the right Services
	 *
	 * @param plugin
	 */
	private void processPlugin(IPlugin plugin) {
		for (Class<?> cls : plugin.getClasses()) {
			if (cls.isAnnotationPresent(SpeechCommand.class)) {
				this.speechCommandHandler.registerSpeechCommand(cls);
			}
			if (cls.isAnnotationPresent(Service.class)) {
				this.di.register(cls);
			}
			if (cls.isAnnotationPresent(Path.class)) {
				this.server.register(cls);
			}
		}
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
	 * stop all Threads and terminate the application. This is call form the
	 * {@link Console}
	 */
	public void stop() {
		this.server.shutdown();
		this.threads.forEach(Thread::interrupt);
		this.singleThreadScheduledExecutor.shutdownNow();
	}

}
