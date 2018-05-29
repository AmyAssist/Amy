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

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * The central core of the application
 * 
 * @author Tim Neumann, Leon Kiefer
 */
public class Core implements ICore {
	private List<Thread> threads;
	private ScheduledExecutorService singleThreadScheduledExecutor;

	private IStorage storage;

	/**
	 * The method executed by the main method
	 * 
	 */
	protected void run() {
		this.init();
		this.threads.forEach(Thread::start);
	}

	/**
	 * Initializes the core
	 */
	void init() {
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

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore#getStorage()
	 */
	@Override
	public IStorage getStorage() {
		return this.storage;
	}

}
