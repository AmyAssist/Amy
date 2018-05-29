/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.rest.Server;

/**
 * A Test for the Hello World Rest Service
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtention.class)
class HelloWorldRestTest {

	@Reference
	private TestFramework testFramework;

	@Reference
	private Server server;

	private HttpServer httpServer;
	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.httpServer = this.server.start(HelloWorldRest.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	@AfterEach
	public void stop() {
		this.httpServer.shutdown();
	}

	/**
	 * test to see the message "Hello World" is sent in response
	 */
	@Test
	public void test() {
		HelloWorldLogic mockService = this.testFramework
				.mockService(HelloWorldLogic.class);
		Mockito.when(mockService.helloWorld()).thenReturn("hello100");

		String responseMsg = this.target.path("helloworld").request()
				.get(String.class);

		assertThat(responseMsg, equalTo("hello100"));
	}

}
