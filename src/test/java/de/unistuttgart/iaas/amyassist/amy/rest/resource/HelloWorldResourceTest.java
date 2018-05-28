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

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
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
	@Reference
	private Server server;

	private HttpServer httpServer;
	private WebTarget target;

	@BeforeAll
	public void setUp() {
		this.httpServer = this.server.start(HelloWorldResource.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	@AfterAll
	public void stop() {
		this.httpServer.shutdown();
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
