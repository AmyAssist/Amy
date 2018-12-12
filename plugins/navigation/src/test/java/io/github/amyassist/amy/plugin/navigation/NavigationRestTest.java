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

package io.github.amyassist.amy.plugin.navigation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static io.github.amyassist.amy.test.matcher.rest.ResponseMatchers.*;

import java.time.ZonedDateTime;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.google.maps.model.TravelMode;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.plugin.navigation.rest.Route;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test for the rest resource of navigation
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
class NavigationRestTest {

	@Reference
	private TestFramework testFramework;

	private DirectionApiLogic logic;

	private RegistryConnection registryConnection;

	private WebTarget target;

	private TestDataForDirectionsRoutes data;
	private String origin;
	private String destination;
	private TravelMode travelMode;
	private BestTransportResult bestResult;
	private DateTime dateTime;
	private ReadableInstant instant;
	private Route route;

	/**
	 * setUp
	 */
	@BeforeEach
	public void setUp() {
		this.target = this.testFramework.setRESTResource(NavigationResource.class);
		this.logic = this.testFramework.mockService(DirectionApiLogic.class);
		this.registryConnection = this.testFramework.mockService(RegistryConnection.class);
		initInputs();
	}

	/**
	 * inits all needed inputs
	 */
	private void initInputs() {
		this.origin = "Stuttgart";
		this.destination = "Berlin";
		this.travelMode = TravelMode.DRIVING;
		this.data = new TestDataForDirectionsRoutes();
		this.bestResult = new BestTransportResult(TravelMode.DRIVING, this.data.carRoutes[0]);
		this.dateTime = new DateTime("2020-01-02T20:20:20+00:00"); // 2020.01.02 20:20:20 UTC
		this.instant = this.dateTime;
		this.route = new Route();
		this.route.setDestination(this.destination);
		this.route.setOrigin(this.origin);
		this.route.setTravelmode(this.travelMode.toString());
		this.route.setTime(ZonedDateTime.parse("2020-01-02T20:20:20+00:00"));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.navigation.NavigationResource#routeFromTo(Route)}.
	 */
	@Test
	void testRouteFromTo() {
		Mockito.when(this.logic.getTravelMode("driving")).thenReturn(this.travelMode);
		Mockito.when(this.logic.fromTo(this.origin, this.destination, this.travelMode)).thenReturn(this.bestResult);

		this.route.setTime(null);
		Entity<Route> entity = Entity.entity(this.route, MediaType.APPLICATION_JSON);
		try (Response response = this.target.path("fromTo").request().post(entity)) {
			assertThat(response.readEntity(BestTransportResult.class).getMode(), equalTo(this.bestResult.getMode()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).fromTo(this.origin, this.destination, this.travelMode);
		}

		this.route.setTime(ZonedDateTime.parse("2020-01-02T20:20:20+00:00"));
		entity = Entity.entity(this.route, MediaType.APPLICATION_JSON);
		Mockito.when(this.logic.fromToWithDeparture(this.origin, this.destination, this.travelMode, this.dateTime))
				.thenReturn(this.bestResult);
		try (Response response = this.target.path("fromTo").request().post(entity)) {
			assertThat(response.readEntity(BestTransportResult.class).getMode(), equalTo(this.bestResult.getMode()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).fromToWithDeparture(this.origin, this.destination, this.travelMode,
					this.dateTime);
		}

		try (Response response = this.target.path("fromTo").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Missing origin and/or destination input."));
			assertThat(response, status(409));
		}

		Mockito.when(this.logic.getTravelMode("blabla")).thenReturn(null);
		this.route.setTravelmode(null);
		entity = Entity.entity(this.route, MediaType.APPLICATION_JSON);
		try (Response response = this.target.path("fromTo").request().post(entity)) {
			assertThat(response.readEntity(String.class), equalTo("Enter a correct travel mode."));
			assertThat(response, status(409));
			Mockito.verify(this.logic, Mockito.times(4)).getTravelMode("driving");
		}

		this.route.setTime(null);
		this.route.setTravelmode(TravelMode.WALKING.toString());
		entity = Entity.entity(this.route, MediaType.APPLICATION_JSON);
		Mockito.when(this.logic.getTravelMode("walking")).thenReturn(TravelMode.WALKING);
		Mockito.when(this.logic.fromTo(this.origin, this.destination, TravelMode.WALKING)).thenReturn(null);
		try (Response response = this.target.path("fromTo").request().post(entity)) {
			assertThat(response.readEntity(String.class), equalTo("No route found."));
			assertThat(response, status(404));
		}

		this.route.setTime(ZonedDateTime.parse("1960-01-02T20:20:20+00:00"));
		this.route.setTravelmode(TravelMode.WALKING.toString());
		entity = Entity.entity(this.route, MediaType.APPLICATION_JSON);
		try (Response response = this.target.path("fromTo").request().post(entity)) {
			assertThat(response.readEntity(String.class), equalTo("No route found."));
			assertThat(response, status(404));
			Mockito.verify(this.logic, Mockito.times(4)).getTravelMode("walking");
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.navigation.NavigationResource#whenIHaveToGo(Route)}.
	 */
	@Test
	void testWhenIHaveToGo() {
		Mockito.when(this.logic.getTravelMode("driving")).thenReturn(this.travelMode);
		Mockito.when(this.logic.whenIHaveToGo(this.origin, this.destination, this.travelMode, this.dateTime))
				.thenReturn(this.instant);

		Entity<Route> entity = Entity.entity(this.route, MediaType.APPLICATION_JSON);
		try (Response response = this.target.path("when").request().post(entity)) {
			assertThat(response.readEntity(String.class),
					equalTo(ZonedDateTime.parse(this.dateTime.toString()).toString()));
			assertThat(response, status(200));
		}

		Mockito.when(this.logic.whenIHaveToGo(this.origin, this.destination, this.travelMode, this.dateTime))
				.thenReturn(null);
		try (Response response = this.target.path("when").request().post(entity)) {
			assertThat(response.readEntity(String.class), equalTo("No latest starttime found."));
			assertThat(response, status(404));
		}

		try (Response response = this.target.path("when").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Missing origin and/or destination input."));
			assertThat(response, status(409));
		}

		this.route.setTravelmode(null);
		entity = Entity.entity(this.route, MediaType.APPLICATION_JSON);
		Mockito.when(this.logic.getTravelMode("blabla")).thenReturn(null);
		try (Response response = this.target.path("when").request().post(entity)) {
			assertThat(response.readEntity(String.class), equalTo("Enter a correct travel mode."));
			assertThat(response, status(409));
			Mockito.verify(this.logic, Mockito.times(4)).getTravelMode("driving");
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.navigation.NavigationResource#getBestTransportInTime(Route)}.
	 */
	@Test
	void testGetBestTransportInTime() {
		Mockito.when(this.logic.getBestTransportInTime(this.origin, this.destination, this.dateTime))
				.thenReturn(this.bestResult);
		Entity<Route> entity = Entity.entity(this.route, MediaType.APPLICATION_JSON);
		try (Response response = this.target.path("best").request().post(entity)) {
			assertThat(response.readEntity(BestTransportResult.class).getMode(), equalTo(this.bestResult.getMode()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).getBestTransportInTime(this.origin, this.destination, this.dateTime);
		}

		try (Response response = this.target.path("best").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Missing origin and/or destination input."));
			assertThat(response, status(409));
		}

		Mockito.when(this.logic.getBestTransportInTime(this.origin, this.destination, this.dateTime)).thenReturn(null);
		try (Response response = this.target.path("best").request().post(entity)) {
			assertThat(response.readEntity(String.class), equalTo("No best transport type found."));
			assertThat(response, status(404));
		}
	}

}
