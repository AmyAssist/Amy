/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.rest.Server;

/**
 * Test for the rest resource of weather
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtention.class)
@Disabled
public class WeatherRestTest {

	@Reference
	private TestFramework testFramework;

	@Reference
	private Server server;

	@Reference
	private WeatherResource weatherLogic;

	private HttpServer httpServer;
	private WebTarget target;
	
	@BeforeEach
	public void setUp() {
		this.httpServer = this.server.start(WeatherResource.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	@AfterEach
	public void stop() {
		this.httpServer.shutdown();
	}
	
	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherToday()}.
	 */
	@Test
	void testGetWeatherReportToday() {
		// TODO
	}
	
	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherTomorrow()}.
	 */
	@Test
	void testGetWeatherReportTomorrow() {
		// TODO
	}
	
//	/**
//	 * Test method for
//	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherWeek()}.
//	 */
//	@Test
//	void testGetWeatherReportWeek() {
//		// TODO
//	}
	
}
