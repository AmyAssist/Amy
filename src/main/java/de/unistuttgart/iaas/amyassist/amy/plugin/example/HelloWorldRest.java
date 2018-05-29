/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.api.HelloWorldService;

/**
 * Example REST Resource
 * 
 * @author Leon Kiefer
 */
@Path("helloworld")
public class HelloWorldRest {
	@Reference
	private HelloWorldService helloWorld;

	@GET
	public String helloWorld() {
		return this.helloWorld.helloWorld();
	}
}
