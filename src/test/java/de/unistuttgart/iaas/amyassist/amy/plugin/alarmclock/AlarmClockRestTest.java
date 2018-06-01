/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest.Timestamp;
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

	private AlarmClockLogic logic;

	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.logic = this.testFramework.mockService(AlarmClockLogic.class);
		this.testFramework.setRESTResource(AlarmClockResource.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#getAlarm()}.
	 */
	@Test
	void testGetAlarm() {
		when(this.logic.getAlarm()).thenReturn("16:05");

		String responseMsg = this.target.path("alarmclock").request().get(String.class);
		assertThat(responseMsg, equalTo("16:05"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#setAlarm(java.lang.String)}.
	 */
	@Test
	void testSetAlarm() {
		Timestamp ts = new Timestamp();
		ts.hour = 15;
		ts.minute = 20;
		Entity<Timestamp> entity = Entity.entity(ts, MediaType.APPLICATION_JSON);
		System.out.println(this.target.path("alarmclock").request().post(entity).getStatus());

		verify(this.logic, atLeastOnce()).setAlarm("15:20");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.AlarmClockResource#deleteAlarm(java.lang.String)}.
	 */
	@Test
	void testDeleteAlarm() {

	}

}
