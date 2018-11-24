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

package io.github.amyassist.amy.plugin.weather;

import static io.github.amyassist.amy.test.matcher.rest.ResponseMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Random;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.registry.Location;
import io.github.amyassist.amy.registry.LocationRegistry;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test for the rest resource of weather
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
public class WeatherRestTest {

	@Reference
	private TestFramework testFramework;

	private WeatherLogic logic;
	private LocationRegistry registry;

	private Location mockLocation;
	private WeatherReport report;

	private WebTarget target;

	/**
	 * setUp
	 */
	@BeforeEach
	public void setUp() {
		this.logic = this.testFramework.mockService(WeatherLogic.class);
		this.registry = this.testFramework.mockService(LocationRegistry.class);
		this.mockLocation = Mockito.mock(Location.class);
		Mockito.when(this.registry.getById(1)).thenReturn(this.mockLocation);
		Mockito.when(this.mockLocation.getLatitude()).thenReturn(4.2);
		Mockito.when(this.mockLocation.getLongitude()).thenReturn(5.3);
		this.report = WeatherTestUtil.generateWeatherReport(new Random());
		Mockito.when(this.logic.getWeatherReport(ArgumentMatchers.any())).thenReturn(this.report);
		this.target = this.testFramework.setRESTResource(WeatherResource.class);
	}

	@Test
	public void testGetWeatherReport() {
		try (Response response = this.target.path("report").queryParam("id", 1).request().get()) {
			assertThat(response, status(200));
			Mockito.verify(this.logic).getWeatherReport(ArgumentMatchers.any());
		}
	}
}
