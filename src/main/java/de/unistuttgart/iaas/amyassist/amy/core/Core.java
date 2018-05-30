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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;
import de.unistuttgart.iaas.amyassist.amy.rest.Server;
import de.unistuttgart.iaas.amyassist.amy.rest.resource.HelloWorldResource;

/**
 * The central core of the application
 * 
 * @author Tim Neumann, Leon Kiefer
 */
public class Core implements ICore {
	private List<Thread> threads;
	private ScheduledExecutorService singleThreadScheduledExecutor;
	private DependencyInjection di = new DependencyInjection();
	private PluginLoader pluginLoader = new PluginLoader();
	private Server server = new Server(this.di);
	private IStorage storage;

	/**
	 * The method executed by the main method
	 * 
	 */
	protected void run() {
		this.init();
		this.threads.forEach(Thread::start);
		this.server.start(HelloWorldResource.class);
	}

	/**
	 * Initializes the core
	 */
	private void init() {
		this.loadPlugins();
		this.threads = new ArrayList<>();
		this.singleThreadScheduledExecutor = Executors
				.newSingleThreadScheduledExecutor();

		Console console = new Console();
		console.setSpeechInputHandler((speechInput) -> {
			return this.singleThreadScheduledExecutor.schedule(() -> {
				//TODO pass the input to the plugins
				return "Input was " + speechInput;
			}, 0, TimeUnit.MILLISECONDS);
		});

		this.threads.add(new Thread(console));
	}

	private void loadPlugins() {
		this.pluginLoader.loadPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example", "amy.plugin.example", "de.unistuttgart.iaas.amyassist", "0.0.1");

		for (Plugin p : this.pluginLoader.getPlugins()) {
			this.di.registerAll(p.getClasses());
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore#getStorage()
	 */
	@Override
	public IStorage getStorage() {
		return this.storage;
	}

}
