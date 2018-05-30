/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.rest.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.rest.Server;

/**
 * A test for the server
 * 
 * @author Christian Bräuner
 */
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(FrameworkExtention.class)
class HelloWorldResourceTest {
	private WebTarget target;
	@Reference
	private TestFramework testFramework;

	@BeforeAll
	public void setUp() {
		this.testFramework.setRESTResource(HelloWorldResource.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	/**
	 * test to see the message "Hello World" is sent in response
	 */
	@Test
	public void test() {
		String responseMsg = this.target.path("helloworld").request()
				.get(String.class);
		assertEquals("Hello World", responseMsg);
	}

}
