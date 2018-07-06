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
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.maps.model.TravelMode;

/**
 * Test class for the BestTransportResult class
 * 
 * @author Lars Buttgereit
 */
public class TestBestTransportResult {

	private BestTransportResult bestResult;
	private TestDataForDirectionsRoutes data = new TestDataForDirectionsRoutes();

	@Test
	public void testGetter() {
		this.bestResult = new BestTransportResult(TravelMode.DRIVING, this.data.routeCar1);
		assertThat(this.bestResult.getRoute(), equalTo(this.data.routeCar1));
		assertThat(this.bestResult.getMode(), equalTo(TravelMode.DRIVING));
	}

	@Test
	public void testRoutToShortStringCar() {
		this.bestResult = new BestTransportResult(TravelMode.DRIVING, this.data.routeCar1);
		assertThat(this.bestResult.routeToShortString(),
				equalTo("The route is 2 km long and need 5 min time in traffic"));
	}

	@Test
	public void testRoutToShortStringCarwithoutTraffic() {
		this.bestResult = new BestTransportResult(TravelMode.DRIVING, this.data.routeCar2);
		assertThat(this.bestResult.routeToShortString(), equalTo("The route is 2 km long and need 5 min time"));
	}

	@Test
	public void testRoutToShortStringtransit() {
		this.bestResult = new BestTransportResult(TravelMode.TRANSIT, this.data.routeTransport1);
		assertThat(this.bestResult.routeToShortString(), equalTo("Departure time is 09:00, arrival time is 10:20"));
	}

	@Test
	public void testRoutToShortStringBike() {
		this.bestResult = new BestTransportResult(TravelMode.BICYCLING, this.data.routeBicycle1);
		assertThat(this.bestResult.routeToShortString(), equalTo("The route is 2 km long and need 5 min time"));
	}

}
