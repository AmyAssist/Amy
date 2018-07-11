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

import static de.unistuttgart.iaas.amyassist.amy.test.matcher.rest.ResponseMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.FIODaily;
import com.github.dvdme.ForecastIOLib.FIODataPoint;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test for the rest resource of weather
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
public class WeatherRestTest {

	@Reference
	private TestFramework testFramework;

	private WeatherDarkSkyAPI logic;
	private WeatherReportDay day;
	private WeatherReportWeek week;
	private JsonObject obj;

	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.target = this.testFramework.setRESTResource(WeatherResource.class);
		this.logic = this.testFramework.mockService(WeatherDarkSkyAPI.class);

		this.createDay();
		this.createWeek();
	}

	private void createDay() {
		FIODataPoint p = Mockito.mock(FIODataPoint.class);
		Mockito.when(p.summary()).thenReturn("Clear throughout the day");
		Mockito.when(p.precipProbability()).thenReturn((double) 0);
		Mockito.when(p.precipType()).thenReturn("no data");
		Mockito.when(p.temperatureMin()).thenReturn((double) 15);
		Mockito.when(p.temperatureMax()).thenReturn((double) 25);
		Mockito.when(p.sunriseTime()).thenReturn("05:00:00");
		Mockito.when(p.sunsetTime()).thenReturn("21:00:00");
		Mockito.when(p.timestamp()).thenReturn((long) 12345);

		this.day = new WeatherReportDay("This is the weather report for today.", p);

		this.obj = new JsonObject();
		this.obj.add("link", this.day.getLink().toString());
		this.obj.add("summary", this.day.getSummary());
		this.obj.add("precip", this.day.isPrecip());
		this.obj.add("precipProbability", this.day.getPrecipProbability());
		this.obj.add("precipType", this.day.getPrecipType());
		this.obj.add("temperatureMin", this.day.getTemperatureMin());
		this.obj.add("temperatureMax", this.day.getTemperatureMax());
		this.obj.add("sunriseTime", this.day.getSunriseTime());
		this.obj.add("sunsetTime", this.day.getSunsetTime());
		this.obj.add("weekday", this.day.getWeekday());
		this.obj.add("timestamp", this.day.getTimestamp());
	}

	private void createWeek() {
		FIODaily d = Mockito.mock(FIODaily.class);
		Mockito.when(d.getSummary()).thenReturn("Clear throughout the day");
		this.week = new WeatherReportWeek("Preamble", d);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherToday()}.
	 */
	@Test
	void testGetWeatherToday() {
		Mockito.when(this.logic.getReportToday()).thenReturn(this.day);

		Response response = this.target.path("today").request().get();

		assertThat(response, status(200));
		assertEquals(this.obj.toString(), response.readEntity(String.class));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherTomorrow()}.
	 */
	@Test
	void testGetWeatherTomorrow() {
		Mockito.when(this.logic.getReportTomorrow()).thenReturn(this.day);

		Response response = this.target.path("tomorrow").request().get();

		assertThat(response, status(200));
		assertEquals(this.obj.toString(), response.readEntity(String.class));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherWeek()}.
	 */
	@Test
	void testGetWeatherWeek() {
		Mockito.when(this.logic.getReportWeek()).thenReturn(this.week);

		Response response = this.target.path("week").request().get();

		assertThat(response, status(200));
		assertTrue(response.readEntity(String.class).contains(this.week.summary));
	}

}
