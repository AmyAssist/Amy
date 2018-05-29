/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.api.HelloWorldService;

/**
 * Does the logic of the Hello World plugin
 * 
 * @author Tim Neumann
 */
@Service
public class HelloWorldImpl implements HelloWorldService {
	private static final String KEY = "hellocount";

	/**
	 * A reference to the storage.
	 */
	@Reference
	protected IStorage storage;

	@Override
	public String helloWorld() {
		if (!this.storage.has(KEY)) {
			this.storage.put(KEY, "0");
		}
		int count = Integer.parseInt(this.storage.get(KEY));
		count++;

		String countString = String.valueOf(count);
		this.storage.put(KEY, countString);

		return "hello" + countString;
	}
}
