/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.HelloWorldRest;
import de.unistuttgart.iaas.amyassist.amy.rest.Server;

/**
 * test for the rest resource of the alarmclock
 * 
 * @author Christian Bräuner
 */
@ExtendWith(FrameworkExtention.class)
class AlarmClockRestTest {
	
	@Reference
	private TestFramework testFramework;

	@Reference
	private Server server;
	
	@Reference
	private AlarmClockLogic logic;

	private HttpServer httpServer;
	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.httpServer = this.server.start(AlarmClockResource.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	@AfterEach
	public void stop() {
		this.httpServer.shutdown();
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#getAlarm()}.
	 */
	@Test
	void testGetAlarm() {
		IStorage storage = this.testFramework
				.storage(TestFramework.store("alarm1", "16:05"));
		
		
		String responseMsg = this.target.path("alarmclock").request()
				.get(String.class);
		assertEquals("16:05", responseMsg);
		System.out.println(responseMsg);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#setAlarm(java.lang.String)}.
	 */
	@Test
	void testSetAlarm() {
		IStorage storage = this.testFramework
				.storage(TestFramework.store("alarmCounter", "0"));
		
		Entity<String> entity = Entity.entity("15:20", MediaType.TEXT_PLAIN);
		this.target.path("alarmclock").request().post(entity);
		assertEquals("15:20",logic.getAlarm());
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#deleteAlarm(java.lang.String)}.
	 */
	@Test
	void testDeleteAlarm() {
		fail("Not yet implemented");
	}

}
