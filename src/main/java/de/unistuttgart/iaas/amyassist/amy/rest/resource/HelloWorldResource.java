/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * A simple Hello World example
 * 
 * @author Christian Bräuner
 */
@Path("helloworld")
public class HelloWorldResource {

	@GET
	public String helloWorld() {
		return "Hello World";
	}
	
}
