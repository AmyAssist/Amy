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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test class for DirectionApiLogic
 * 
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class TestDirectionApiLogic {

	@Reference
	private TestFramework testFramework;

	private DirectionsApiCalls directionsApiCalls;
	private DirectionApiLogic logic;
	private TestDataForDirectionsRoutes data = new TestDataForDirectionsRoutes();

	@BeforeEach
	public void init() {
		this.directionsApiCalls = this.testFramework.mockService(DirectionsApiCalls.class);
		this.logic = this.testFramework.setServiceUnderTest(DirectionApiLogic.class);
	}

	@Test
	public void testFromToCar() {
		when(this.directionsApiCalls.fromTo("A", "B", TravelMode.DRIVING)).thenReturn(this.data.carRoutes);
		BestTransportResult result = this.logic.fromTo("A", "B", TravelMode.DRIVING);
		verify(this.directionsApiCalls).fromTo("A", "B", TravelMode.DRIVING);
		assertThat(result.getRoute(), equalTo(this.data.carRoutes[4]));
		assertThat(result.getMode(), equalTo(TravelMode.DRIVING));
	}

	@Test
	public void testFromToNoRoutes() {
		when(this.directionsApiCalls.fromTo("A", "B", TravelMode.DRIVING)).thenReturn(null);
		BestTransportResult result = this.logic.fromTo("A", "B", TravelMode.DRIVING);
		verify(this.directionsApiCalls).fromTo("A", "B", TravelMode.DRIVING);
		assertThat(result.getRoute(), equalTo(null));
		assertThat(result.getMode(), equalTo(TravelMode.DRIVING));
	}

	@Test
	public void testFromToTransit() {
		when(this.directionsApiCalls.fromTo("A", "B", TravelMode.TRANSIT)).thenReturn(this.data.transportRoutes);
		BestTransportResult result = this.logic.fromTo("A", "B", TravelMode.TRANSIT);
		verify(this.directionsApiCalls).fromTo("A", "B", TravelMode.TRANSIT);
		assertThat(result.getRoute(), equalTo(this.data.transportRoutes[6]));
		assertThat(result.getMode(), equalTo(TravelMode.TRANSIT));
	}

	@Test
	public void testFromToTransitNoRoutes() {
		when(this.directionsApiCalls.fromTo("A", "B", TravelMode.TRANSIT)).thenReturn(null);
		BestTransportResult result = this.logic.fromTo("A", "B", TravelMode.TRANSIT);
		verify(this.directionsApiCalls).fromTo("A", "B", TravelMode.TRANSIT);
		assertThat(result.getRoute(), equalTo(null));
		assertThat(result.getMode(), equalTo(TravelMode.TRANSIT));
	}

	@Test
	public void testFromToDepartureCar() {
		when(this.directionsApiCalls.fromToWithDepartureTime("A", "B", TravelMode.DRIVING, new DateTime(0)))
				.thenReturn(this.data.carRoutes);
		BestTransportResult result = this.logic.fromToWithDeparture("A", "B", TravelMode.DRIVING, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING, new DateTime(0));
		assertThat(result.getRoute(), equalTo(this.data.carRoutes[4]));
		assertThat(result.getMode(), equalTo(TravelMode.DRIVING));
	}

	@Test
	public void testFromToDepartureNoRoutes() {
		when(this.directionsApiCalls.fromToWithDepartureTime("A", "B", TravelMode.DRIVING, new DateTime(0)))
				.thenReturn(null);
		BestTransportResult result = this.logic.fromToWithDeparture("A", "B", TravelMode.DRIVING, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING, new DateTime(0));
		assertThat(result.getRoute(), equalTo(null));
		assertThat(result.getMode(), equalTo(TravelMode.DRIVING));
	}

	@Test
	public void testFromToDepartureTransit() {
		when(this.directionsApiCalls.fromToWithDepartureTime("A", "B", TravelMode.TRANSIT, new DateTime(0)))
				.thenReturn(this.data.transportRoutes);
		BestTransportResult result = this.logic.fromToWithDeparture("A", "B", TravelMode.TRANSIT, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT, new DateTime(0));
		assertThat(result.getRoute(), equalTo(this.data.transportRoutes[6]));
		assertThat(result.getMode(), equalTo(TravelMode.TRANSIT));
	}

	@Test
	public void testFromToDepartureTransitNoRoutes() {
		when(this.directionsApiCalls.fromToWithDepartureTime("A", "B", TravelMode.TRANSIT, new DateTime(0)))
				.thenReturn(null);
		BestTransportResult result = this.logic.fromToWithDeparture("A", "B", TravelMode.TRANSIT, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT, new DateTime(0));
		assertThat(result.getRoute(), equalTo(null));
		assertThat(result.getMode(), equalTo(TravelMode.TRANSIT));
	}

	@Test
	public void testWehnIHaveToGo() {
		when(this.directionsApiCalls.fromToWithDepartureTime(any(), any(), any(), any()))
				.thenReturn(this.data.carRoutes);
		DateTime time = new DateTime(Long.MAX_VALUE);
		time = time.minusSeconds(Math.toIntExact(this.data.carRoutes[4].legs[0].duration.inSeconds));
		assertThat(this.logic.whenIHaveToGo("A", "B", TravelMode.DRIVING, new DateTime(Long.MAX_VALUE)).getMillis(),
				equalTo(time.getMillis()));
		verify(this.directionsApiCalls).fromToWithDepartureTime(any(), any(), any(), any());
	}

	@Test
	public void testWehnIHaveToGoTraffic() {
		DirectionsRoute[] routes = new DirectionsRoute[1];
		routes[0] = this.data.carRoutes[0];
		when(this.directionsApiCalls.fromToWithDepartureTime(any(), any(), any(), any())).thenReturn(routes);
		DateTime time = new DateTime(Long.MAX_VALUE);
		time = time.minusSeconds(Math.toIntExact(this.data.carRoutes[0].legs[0].durationInTraffic.inSeconds));
		assertThat(this.logic.whenIHaveToGo("A", "B", TravelMode.DRIVING, new DateTime(Long.MAX_VALUE)).getMillis(),
				equalTo(time.getMillis()));
		verify(this.directionsApiCalls).fromToWithDepartureTime(any(), any(), any(), any());
	}

	@Test
	public void testWehnIHaveToGoTransit() {
		DirectionsRoute[] routes = new DirectionsRoute[1];
		routes[0] = this.data.transportRoutes[0];
		when(this.directionsApiCalls.fromToWithArrivalTime(any(), any(), any(), any())).thenReturn(routes);
		assertThat(this.logic.whenIHaveToGo("A", "B", TravelMode.TRANSIT, DateTime.now()).getMillis(),
				equalTo(this.data.transportRoutes[0].legs[0].departureTime.getMillis()));
	}

	@Test
	public void testWehnIHaveToGoNotSupportedTravelMode() {
		assertThat(this.logic.whenIHaveToGo("A", "B", TravelMode.WALKING, DateTime.now()), equalTo(null));
	}

	@Test
	public void testGetBestTransportInTimeCar() {
		DirectionsRoute[] car = new DirectionsRoute[1];
		car[0] = this.data.carRoutes[0];
		DirectionsRoute[] transport = new DirectionsRoute[1];
		transport[0] = this.data.transportRoutes[5];
		DirectionsRoute[] bike = new DirectionsRoute[1];
		bike[0] = this.data.bicycleRoutes[1];
		doReturn(car).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING,
				new DateTime(0));
		doReturn(transport).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT,
				new DateTime(0));
		doReturn(bike).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.BICYCLING,
				new DateTime(0));
		BestTransportResult result = this.logic.getBestTransportInTime("A", "B", new DateTime(0));
		assertThat(result.getMode(), equalTo(TravelMode.DRIVING));
		assertThat(result.getRoute(), equalTo(car[0]));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.BICYCLING, new DateTime(0));
	}

	@Test
	public void testGetBestTransportInTimeTransit() {
		DirectionsRoute[] car = new DirectionsRoute[1];
		car[0] = this.data.carRoutes[5];
		DirectionsRoute[] transport = new DirectionsRoute[1];
		transport[0] = this.data.transportRoutes[6];
		DirectionsRoute[] bike = new DirectionsRoute[1];
		bike[0] = this.data.bicycleRoutes[1];
		doReturn(car).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING,
				new DateTime(0));
		doReturn(transport).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT,
				new DateTime(0));
		doReturn(bike).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.BICYCLING,
				new DateTime(0));
		BestTransportResult result = this.logic.getBestTransportInTime("A", "B", new DateTime(0));
		assertThat(result.getMode(), equalTo(TravelMode.TRANSIT));
		assertThat(result.getRoute(), equalTo(transport[0]));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.BICYCLING, new DateTime(0));
	}

	@Test
	public void testGetBestTransportInTimeBike() {
		DirectionsRoute[] car = new DirectionsRoute[1];
		car[0] = this.data.carRoutes[3];
		DirectionsRoute[] transport = new DirectionsRoute[1];
		transport[0] = this.data.transportRoutes[5];
		DirectionsRoute[] bike = new DirectionsRoute[1];
		bike[0] = this.data.bicycleRoutes[0];
		doReturn(car).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING,
				new DateTime(0));
		doReturn(transport).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT,
				new DateTime(0));
		doReturn(bike).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.BICYCLING,
				new DateTime(0));
		BestTransportResult result = this.logic.getBestTransportInTime("A", "B", new DateTime(0));
		assertThat(result.getMode(), equalTo(TravelMode.BICYCLING));
		assertThat(result.getRoute(), equalTo(bike[0]));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.BICYCLING, new DateTime(0));
	}

	@Test
	public void testGetBestTransportInTimeNoResults() {

		doReturn(null).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING,
				new DateTime(0));
		doReturn(null).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT,
				new DateTime(0));
		doReturn(null).when(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.BICYCLING,
				new DateTime(0));
		BestTransportResult result = this.logic.getBestTransportInTime("A", "B", new DateTime(0));
		assertThat(result, equalTo(null));

		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.DRIVING, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.TRANSIT, new DateTime(0));
		verify(this.directionsApiCalls).fromToWithDepartureTime("A", "B", TravelMode.BICYCLING, new DateTime(0));
	}

	@Test
	public void testGetTravelModeDriving() {
		assertThat(this.logic.getTravelMode("driving"), equalTo(TravelMode.DRIVING));
		assertThat(this.logic.getTravelMode("car"), equalTo(TravelMode.DRIVING));
	}

	@Test
	public void testGetTravelModeBicycling() {
		assertThat(this.logic.getTravelMode("bicycling"), equalTo(TravelMode.BICYCLING));
		assertThat(this.logic.getTravelMode("bike"), equalTo(TravelMode.BICYCLING));
	}

	@Test
	public void testGetTravelModeTransit() {
		assertThat(this.logic.getTravelMode("transit"), equalTo(TravelMode.TRANSIT));
		assertThat(this.logic.getTravelMode("public transport"), equalTo(TravelMode.TRANSIT));
		assertThat(this.logic.getTravelMode("transport"), equalTo(TravelMode.TRANSIT));
	}

	@Test
	public void testGetTravelModeWalking() {
		assertThat(this.logic.getTravelMode("walking"), equalTo(TravelMode.WALKING));
		assertThat(this.logic.getTravelMode("walk"), equalTo(TravelMode.WALKING));
	}

	@Test
	public void testGetTravelModeWrongMode() {
		assertThat(this.logic.getTravelMode("tfgzutzvj"), equalTo(null));
	}

}
