/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

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
		this.testFramework.setRESTResource(WeatherResource.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
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
