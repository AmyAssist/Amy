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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.maps.model.TravelMode;

/**
 * Test class for the BestTransportResult class
 * 
 * @author Lars Buttgereit, Muhammed Kaya
 */
public class TestBestTransportResult {

	private BestTransportResult bestResult;
	private TestDataForDirectionsRoutes data = new TestDataForDirectionsRoutes();

	@Test
	public void testGetter() {
		this.bestResult = new BestTransportResult(TravelMode.DRIVING, this.data.carRoutes[0]);
		assertThat(this.bestResult.getRoute(), equalTo(this.data.carRoutes[0]));
		assertThat(this.bestResult.getMode(), equalTo(TravelMode.DRIVING));
	}

	@Test
	public void testRoutToShortStringCar() {
		this.bestResult = new BestTransportResult(TravelMode.DRIVING, this.data.carRoutes[0]);
		assertThat(this.bestResult.routeToShortString(),
				equalTo("The route is 2 km long and you will need 5 min time in traffic"));
	}

	@Test
	public void testRoutToShortStringCarwithoutTraffic() {
		this.bestResult = new BestTransportResult(TravelMode.DRIVING, this.data.carRoutes[1]);
		assertThat(this.bestResult.routeToShortString(), equalTo("The route is 2 km long and you will need 5 min time"));
	}

	@Test
	public void testRoutToShortStringBike() {
		this.bestResult = new BestTransportResult(TravelMode.BICYCLING, this.data.bicycleRoutes[0]);
		assertThat(this.bestResult.routeToShortString(), equalTo("The route is 2 km long and you will need 5 min time"));
	}

	@Test
	void testEquals() {
		this.bestResult = new BestTransportResult(TravelMode.TRANSIT, this.data.transportRoutes[0]);
		assertTrue(this.bestResult.equals(new BestTransportResult(TravelMode.TRANSIT, this.data.transportRoutes[0])));
		assertFalse(this.bestResult.equals(new BestTransportResult(TravelMode.BICYCLING, this.data.bicycleRoutes[0])));
		assertFalse(this.bestResult.equals(new BestTransportResult()));
	}
}
