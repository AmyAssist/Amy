/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.api.HelloWorldService;
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

	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.testFramework.setRESTResource(HelloWorldRest.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	/**
	 * test to see the message "Hello World" is sent in response
	 */
	@Test
	public void test() {
		HelloWorldService mockService = this.testFramework.mockService(HelloWorldService.class);
		Mockito.when(mockService.helloWorld()).thenReturn("hello100");

		String responseMsg = this.target.path("helloworld").request().get(String.class);

		assertThat(responseMsg, equalTo("hello100"));
	}

}
