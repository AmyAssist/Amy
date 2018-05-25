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

import de.unistuttgart.iaas.amyassist.amy.rest.Server;

/**
 * A test for the server
 * 
 * @author Christian Bräuner
 */
@TestInstance(Lifecycle.PER_CLASS)
class HelloWorldResourceTest {

	private HttpServer server;
	private WebTarget target;
	
	@BeforeAll
	public void setUp() {
		server = Server.start();
		
		Client c = ClientBuilder.newClient();
        target = c.target(Server.BASE_URI);
	}
	
	@AfterAll
	public void stop() {
		server.shutdown();
	}
	
	/**
	 * test to see the message "Hello World" is sent in response
	 */
	@Test
	public void test() {
		String responseMsg = target.path("helloworld").request().get(String.class);
		System.out.println("fdash");
        assertEquals("Hello World", responseMsg);
	}

}
