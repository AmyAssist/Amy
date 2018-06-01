/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.Path;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommandHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.rest.Server;

/**
 * The central core of the application
 * 
 * @author Tim Neumann, Leon Kiefer
 */
public class Core implements ICore, SpeechInputHandler {
	private List<Thread> threads;
	private ScheduledExecutorService singleThreadScheduledExecutor;
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
		this.singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();

		this.server = this.di.getService(Server.class);

		Console console = this.di.getService(Console.class);
		console.setSpeechInputHandler(this);

		this.threads.add(new Thread(console));

		this.loadPlugins();
	}

	/**
	 * register all instances and classes in the DI
	 */
	private void registerAllCoreServices() {
		this.di.addExternalService(IStorage.class, this.storage);
		this.di.addExternalService(PluginLoader.class, this.pluginLoader);
		this.di.addExternalService(Core.class, this);

		this.di.register(Server.class);
		this.di.register(ConfigurationImpl.class);
		this.di.register(Console.class);
		this.di.register(SpeechCommandHandler.class);
	}

	/**
	 * load the plugins
	 */
	private void loadPlugins() {
		this.pluginLoader.loadPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example", "amy.plugin.example",
				"de.unistuttgart.iaas.amyassist", "0.0.1");

		for (Plugin p : this.pluginLoader.getPlugins()) {
			this.processPlugin(p);
		}
		this.speechCommandHandler.completeSetup();
	}

	/**
	 * Process the plugin components and register them at the right Services
	 * 
	 * @param plugin
	 */
	private void processPlugin(Plugin plugin) {
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
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore#getStorage()
	 */
	@Override
	public IStorage getStorage() {
		return this.storage;
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
