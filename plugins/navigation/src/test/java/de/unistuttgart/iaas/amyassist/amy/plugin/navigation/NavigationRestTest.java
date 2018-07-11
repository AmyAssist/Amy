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

package de.unistuttgart.iaas.amyassist.amy.plugin.navigation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test for the rest resource of navigation
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
@Disabled
class NavigationRestTest {

	@Reference
	private TestFramework testFramework;

	private DirectionApiLogic logic;

	private WebTarget target;

	private Timestamp timestamp;
	private TestDataForDirectionsRoutes data;
	private String origin;
	private String destination;
	private TravelMode travelMode;
	private BestTransportResult bestResult;
	private DateTime dateTime;
	private Entity<Timestamp> entity;
	private ReadableInstant instant;

	/**
	 * setUp
	 */
	@BeforeEach
	public void setUp() {
		this.target = this.testFramework.setRESTResource(NavigationResource.class);
		this.logic = this.testFramework.mockService(DirectionApiLogic.class);
		initInputs();
	}

	/**
	 * inits all needed inputs
	 */
	private void initInputs() {
		this.timestamp = new Timestamp(2020, 1, 2, 20, 20, 20); // 2020.01.02 20:20:20
		this.origin = "Stuttgart";
		this.destination = "Berlin";
		this.travelMode = TravelMode.DRIVING;
		this.data = new TestDataForDirectionsRoutes();
		this.bestResult = new BestTransportResult(TravelMode.DRIVING, this.data.carRoutes[0]);
		this.dateTime = new DateTime(this.timestamp.getYear(), this.timestamp.getMonth(), this.timestamp.getDay(),
				this.timestamp.getHour(), this.timestamp.getMinute(), this.timestamp.getSecond());
		this.entity = Entity.entity(this.timestamp, MediaType.APPLICATION_JSON);
		this.instant = this.dateTime;
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.NavigationResource#routeFromTo(String, String, String, Timestamp)}.
	 */
	@Test
	void testRouteFromTo() {
		Mockito.when(this.logic.getTravelMode("driving")).thenReturn(this.travelMode);
		Mockito.when(this.logic.fromTo(this.origin, this.destination, this.travelMode)).thenReturn(this.bestResult);

		Response response = this.target.path("fromTo").queryParam("origin", this.origin)
				.queryParam("destination", this.destination).queryParam("travelMode", "driving").request().post(null);
		BestTransportResult actual = response.readEntity(BestTransportResult.class);
		assertThat(actual.getRoute().bounds, is(this.bestResult.getRoute().bounds));
		assertThat(actual.getRoute().copyrights, is(this.bestResult.getRoute().copyrights));
		assertThat(actual.getRoute().fare, is(this.bestResult.getRoute().fare));
		assertThat(actual.getRoute().overviewPolyline, is(this.bestResult.getRoute().overviewPolyline));
		assertThat(actual.getRoute().summary, is(this.bestResult.getRoute().summary));
		assertThat(actual.getMode(), is(this.bestResult.getMode()));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).fromTo(this.origin, this.destination, this.travelMode);

		Mockito.when(this.logic.fromToWithDeparture(this.origin, this.destination, this.travelMode, this.dateTime))
				.thenReturn(this.bestResult);
		response = this.target.path("fromTo").queryParam("origin", this.origin)
				.queryParam("destination", this.destination).queryParam("travelMode", "driving").request()
				.post(this.entity);
		actual = response.readEntity(BestTransportResult.class);
		assertThat(actual.getRoute().bounds, is(this.bestResult.getRoute().bounds));
		assertThat(actual.getRoute().copyrights, is(this.bestResult.getRoute().copyrights));
		assertThat(actual.getRoute().fare, is(this.bestResult.getRoute().fare));
		assertThat(actual.getRoute().overviewPolyline, is(this.bestResult.getRoute().overviewPolyline));
		assertThat(actual.getRoute().summary, is(this.bestResult.getRoute().summary));
		assertThat(actual.getMode(), is(this.bestResult.getMode()));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).fromToWithDeparture(this.origin, this.destination, this.travelMode, this.dateTime);

		response = this.target.path("fromTo").queryParam("destination", null).queryParam("travelMode", "driving")
				.request().post(null);
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Missing origin and/or destination input."));
		assertThat(response.getStatus(), is(409));

		Mockito.when(this.logic.getTravelMode("blabla")).thenReturn(null);
		response = this.target.path("fromTo").queryParam("origin", this.origin)
				.queryParam("destination", this.destination).queryParam("travelMode", "blabla").request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Enter a correct travel mode."));
		assertThat(response.getStatus(), is(409));
		Mockito.verify(this.logic, Mockito.times(4)).getTravelMode("driving");

		Mockito.when(this.logic.getTravelMode("walking")).thenReturn(TravelMode.WALKING);
		Mockito.when(this.logic.fromTo(this.origin, this.destination, TravelMode.WALKING)).thenReturn(null);
		response = this.target.path("fromTo").queryParam("origin", this.origin)
				.queryParam("destination", this.destination).queryParam("travelMode", "walking").request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("No route found."));
		assertThat(response.getStatus(), is(404));
		Mockito.verify(this.logic, Mockito.times(2)).getTravelMode("walking");
		Mockito.verify(this.logic).fromTo(this.origin, this.destination, TravelMode.WALKING);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.NavigationResource#whenIHaveToGo(String, String, String, Timestamp)}.
	 */
	@Test
	void testWhenIHaveToGo() {
		Mockito.when(this.logic.getTravelMode("driving")).thenReturn(this.travelMode);
		Mockito.when(this.logic.whenIHaveToGo(this.origin, this.destination, this.travelMode, this.dateTime))
				.thenReturn(this.instant);

		Response response = this.target.path("when").queryParam("origin", this.origin)
				.queryParam("destination", this.destination).queryParam("travelMode", "driving").request()
				.post(this.entity);
		Timestamp actual = response.readEntity(Timestamp.class);
		assertThat(actual, is(this.timestamp));
		assertThat(response.getStatus(), is(200));

		Mockito.when(this.logic.whenIHaveToGo(this.origin, this.destination, this.travelMode, this.dateTime))
				.thenReturn(null);
		response = this.target.path("when").queryParam("origin", this.origin)
				.queryParam("destination", this.destination).queryParam("travelMode", "driving").request()
				.post(this.entity);
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("No latest starttime found."));
		assertThat(response.getStatus(), is(404));

		response = this.target.path("when").queryParam("destination", null).queryParam("travelMode", "driving")
				.request().post(this.entity);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Missing origin and/or destination input."));
		assertThat(response.getStatus(), is(409));

		Mockito.when(this.logic.getTravelMode("blabla")).thenReturn(null);
		response = this.target.path("when").queryParam("origin", this.origin)
				.queryParam("destination", this.destination).queryParam("travelMode", "blabla").request()
				.post(this.entity);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Enter a correct travel mode."));
		assertThat(response.getStatus(), is(409));
		Mockito.verify(this.logic, Mockito.times(4)).getTravelMode("driving");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.NavigationResource#getBestTransportInTime(String, String, Timestamp)}.
	 */
	@Test
	void testGetBestTransportInTime() {
		Mockito.when(this.logic.getBestTransportInTime(this.origin, this.destination, this.dateTime))
				.thenReturn(this.bestResult);
		Response response = this.target.path("best").queryParam("origin", this.origin)
				.queryParam("destination", this.destination).request().post(this.entity);
		BestTransportResult actual = response.readEntity(BestTransportResult.class);
		assertThat(actual.getRoute().bounds, is(this.bestResult.getRoute().bounds));
		assertThat(actual.getRoute().copyrights, is(this.bestResult.getRoute().copyrights));
		assertThat(actual.getRoute().fare, is(this.bestResult.getRoute().fare));
		assertThat(actual.getRoute().overviewPolyline, is(this.bestResult.getRoute().overviewPolyline));
		assertThat(actual.getRoute().summary, is(this.bestResult.getRoute().summary));
		assertThat(actual.getMode(), is(this.bestResult.getMode()));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).getBestTransportInTime(this.origin, this.destination, this.dateTime);

		response = this.target.path("best").queryParam("destination", null).request().post(null);
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Missing origin and/or destination input."));
		assertThat(response.getStatus(), is(409));

		Mockito.when(this.logic.getBestTransportInTime(this.origin, this.destination, this.dateTime)).thenReturn(null);
		response = this.target.path("best").queryParam("origin", this.origin)
				.queryParam("destination", this.destination).queryParam("travelMode", "driving").request()
				.post(this.entity);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("No best transport type found."));
		assertThat(response.getStatus(), is(404));
	}

}
