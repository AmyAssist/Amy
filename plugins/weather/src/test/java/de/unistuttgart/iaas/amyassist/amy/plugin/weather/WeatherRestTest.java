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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.equalTo;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
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
	private WeatherReportNow now;
	private WeatherReportDay day;
	private WeatherReportWeek week;
	private JsonObject objNow;
	private JsonObject objToday;

	private WebTarget target;

	/**
	 * setUp
	 */
	@BeforeEach
	public void setUp() {
		this.target = this.testFramework.setRESTResource(WeatherResource.class);
		this.logic = this.testFramework.mockService(WeatherDarkSkyAPI.class);

		this.createWeather();
	}

	private void createWeather() {
		FIODataPoint p = Mockito.mock(FIODataPoint.class);
		Mockito.when(p.summary()).thenReturn("Clear throughout the day");
		Mockito.when(p.precipProbability()).thenReturn(0.0);
		Mockito.when(p.precipType()).thenReturn("no data");
		Mockito.when(p.temperature()).thenReturn(20.0);
		Mockito.when(p.temperatureMin()).thenReturn(15.0);
		Mockito.when(p.temperatureMax()).thenReturn(25.0);
		Mockito.when(p.sunriseTime()).thenReturn("05:00:00");
		Mockito.when(p.sunsetTime()).thenReturn("21:00:00");
		Mockito.when(p.windSpeed()).thenReturn(4.00);
		Mockito.when(p.timestamp()).thenReturn(12345L);
		Mockito.when(p.icon()).thenReturn("rainIcon");
		Mockito.when(p.windSpeed()).thenReturn(4.44);

		this.now = new WeatherReportNow("This is the current weather report.", p);
		this.objNow = new JsonObject();
		this.objNow.add("summary", this.now.getSummary());
		this.objNow.add("precip", this.now.isPrecip());
		this.objNow.add("precipProbability", this.now.getPrecipProbability());
		this.objNow.add("precipType", this.now.getPrecipType());
		this.objNow.add("temperatureNow", this.now.getTemperatureNow());
		this.objNow.add("timestamp", this.now.getTimestamp());
		this.objNow.add("icon", this.now.getIcon());
		this.objNow.add("windspeed", this.now.getWindspeed());

		this.day = new WeatherReportDay("This is the weather report for today.", p);
		this.objToday = new JsonObject();
		this.objToday.add("link", this.day.getLink().toString());
		this.objToday.add("summary", this.day.getSummary());
		this.objToday.add("precip", this.day.isPrecip());
		this.objToday.add("precipProbability", this.day.getPrecipProbability());
		this.objToday.add("precipType", this.day.getPrecipType());
		this.objToday.add("temperatureMin", this.day.getTemperatureMin());
		this.objToday.add("temperatureMax", this.day.getTemperatureMax());
		this.objToday.add("sunriseTime", this.day.getSunriseTime());
		this.objToday.add("sunsetTime", this.day.getSunsetTime());
		this.objToday.add("weekday", this.day.getWeekday());
		this.objToday.add("timestamp", this.day.getTimestamp());
		this.objToday.add("icon", this.day.getIcon());
		
		FIODaily d = Mockito.mock(FIODaily.class);
		Mockito.when(d.getSummary()).thenReturn("Clear throughout the day");
		this.week = new WeatherReportWeek("Preamble", d);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherNow()}.
	 */
	@Test
	void testGetWeatherNow() {
		Mockito.when(this.logic.getReportNow()).thenReturn(this.now);
		try (Response response = this.target.path("now").request().get()) {
			assertThat(response.readEntity(String.class), equalTo(this.objNow.toString()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).getReportNow();
		}
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherToday()}.
	 */
	@Test
	void testGetWeatherToday() {
		Mockito.when(this.logic.getReportToday()).thenReturn(this.day);
		try (Response response = this.target.path("today").request().get()) {
			assertThat(response.readEntity(String.class), equalTo(this.objToday.toString()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).getReportToday();
		}
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#getWeatherWeek()}.
	 */
	@Test
	void testGetWeatherWeek() {
		Mockito.when(this.logic.getReportWeek()).thenReturn(this.week);
		try (Response response = this.target.path("week").request().get()) {
			assertTrue(response.readEntity(String.class).contains(this.week.summary));
			assertThat(response, status(200));
			Mockito.verify(this.logic).getReportWeek();
		}
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherResource#setLocation(String)}.
	 */
	@Test
	void testSetLocation() {
		try (Response response = this.target.path("setLocation").request()
				.put(Entity.entity("1", MediaType.TEXT_PLAIN))) {
			assertThat(response, status(204));
			Mockito.verify(this.logic).setLocation(1);
		}
	}

}
