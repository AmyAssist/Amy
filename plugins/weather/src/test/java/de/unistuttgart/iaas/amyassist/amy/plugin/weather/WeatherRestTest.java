/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

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
public class WeatherRestTest {

	@Reference
	private TestFramework testFramework;
	
	private WeatherDarkSkyAPI logic;
	private WeatherReportDay day;
	private WeatherReportWeek week;

	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.testFramework.setRESTResource(WeatherResource.class);
		this.logic = this.testFramework.mockService(WeatherDarkSkyAPI.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
		
		this.createDay();
		this.createWeek();
	}
	
	private void createDay() {
		this.day = Mockito.mock(WeatherReportDay.class);
		this.day.summary = "Clear throughout the day";
		this.day.precipProbability = "0%";
		this.day.precipType = "no data";
		this.day.temperatureMin = 15;
		this.day.temperatureMax = 25;
		this.day.sunriseTime= "05:00:00";
		this.day.sunsetTime= "21:00:00";
		this.day.weekday = "Monday";
		this.day.timestamp = 12345;
	}
	
	private void createWeek() {
		this.week = Mockito.mock(WeatherReportWeek.class);
		this.week.summary = "Clear throughout the day";
	}
	
	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherToday()}.
	 */
	@Test
	void testGetWeatherToday() {
		Mockito.when(this.logic.getReportToday()).thenReturn(this.day);
		
		Response response = this.target.path("weather").path("today").request().get();

		String result = response.readEntity(String.class);
		
		assertEquals(200, response.getStatus());
		assertTrue(result.contains(this.day.summary));
		assertTrue(result.contains(this.day.precipProbability));
		assertTrue(result.contains(this.day.precipType));
		assertTrue(result.contains(String.valueOf(this.day.temperatureMin)));
		assertTrue(result.contains(String.valueOf(this.day.temperatureMax)));
		assertTrue(result.contains(this.day.sunriseTime));
		assertTrue(result.contains(this.day.sunsetTime));
		assertTrue(result.contains(this.day.weekday));
		assertTrue(result.contains(String.valueOf(this.day.timestamp)));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherTomorrow()}.
	 */
	@Test
	void testGetWeatherTomorrow() {
		Mockito.when(this.logic.getReportTomorrow()).thenReturn(this.day);
		
		Response response = this.target.path("weather").path("tomorrow").request().get();
		
		String result = response.readEntity(String.class);
		
		assertEquals(200, response.getStatus());
		assertTrue(result.contains(this.day.summary));
		assertTrue(result.contains(this.day.precipProbability));
		assertTrue(result.contains(this.day.precipType));
		assertTrue(result.contains(String.valueOf(this.day.temperatureMin)));
		assertTrue(result.contains(String.valueOf(this.day.temperatureMax)));
		assertTrue(result.contains(this.day.sunriseTime));
		assertTrue(result.contains(this.day.sunsetTime));
		assertTrue(result.contains(this.day.weekday));
		assertTrue(result.contains(String.valueOf(this.day.timestamp)));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherWeek()}.
	 */
	@Test
	void testGetWeatherWeek() {
		Mockito.when(this.logic.getReportWeek()).thenReturn(this.week);
		
		Response response = this.target.path("weather").path("week").request().get();

		String result = response.readEntity(String.class);
		
		assertEquals(200, response.getStatus());
		assertTrue(result.contains(this.week.summary));
	}

}
