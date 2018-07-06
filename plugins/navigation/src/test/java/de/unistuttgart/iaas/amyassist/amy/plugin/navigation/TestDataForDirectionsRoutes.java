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

import org.joda.time.DateTime;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;

/**
 * Some test data. e.g. route data from the api
 * 
 * @author Lars Buttgereit
 */
public class TestDataForDirectionsRoutes {
	protected DirectionsRoute routeCar1;
	protected DirectionsRoute routeCar2;

	protected DirectionsRoute routeTransport1;
	protected DirectionsRoute routeTransport2;

	protected DirectionsRoute routeBicycle1;
	protected DirectionsRoute routeBicycle2;

	private Distance[] distances = new Distance[4];
	private Duration[] durations = new Duration[4];
	private DateTime[] times = new DateTime[6];
	
	protected TestDataForDirectionsRoutes() {
		initdistances();
		initDurations();
		initTimes();
		initRoutes();
	}

	private void initdistances() {
		distances[0] = new Distance();
		for (int i = 0; i < this.distances.length; i++) {
			this.distances[i] = new Distance();
		}
		this.distances[0].inMeters = 40000;
		this.distances[0].humanReadable = "40 km";

		this.distances[1].inMeters = 20000;
		this.distances[1].humanReadable = "20 km";

		this.distances[2].inMeters = 5000;
		this.distances[2].humanReadable = "5 km";

		this.distances[3].inMeters = 2000;
		this.distances[3].humanReadable = "2 km";
	}

	private void initDurations() {
		for (int i = 0; i < this.durations.length; i++) {
			this.durations[i] = new Duration();
		}
		this.durations[0].humanReadable = "5 min";
		this.durations[0].inSeconds = 300;
		this.durations[1].humanReadable = "34 min";
		this.durations[1].inSeconds = 2040;
		this.durations[2].humanReadable = "1 h 20 min";
		this.durations[2].inSeconds = 4800;
		this.durations[3].humanReadable = "2 h 2 min";
		this.durations[3].inSeconds = 7320;
	}

	private void initTimes() {
		this.times[0] = new DateTime(1530774000000l); // 6.7.18 9 :00
		this.times[1] = new DateTime(1530778800000l); // 6.7.18 10:20 
		this.times[2] = new DateTime(1530783000000l); // 6.7.18 11:30 
		this.times[3] = new DateTime(1530785040000l); // 6.7.18 12:04 
		this.times[4] = new DateTime(15307904400000l); // 6.7.18 13:34 
		this.times[5] = new DateTime(1530792000000l); // 6.7.18 14:00
	}

	private  void initRoutes() {
		this.routeCar1 = new DirectionsRoute();
		this.routeTransport1 = new DirectionsRoute();
		this.routeBicycle1 = new DirectionsRoute();
		this.routeCar2 = new DirectionsRoute();
		this.routeTransport2 = new DirectionsRoute();
		this.routeBicycle2 = new DirectionsRoute();
		
		DirectionsLeg legCar1 = new DirectionsLeg();
		legCar1.distance = this.distances[3];
		legCar1.duration = this.durations[0];
		legCar1.durationInTraffic = this.durations[0];
		DirectionsLeg[] legsCar1 = {legCar1};
		this.routeCar1.legs = legsCar1;
		
		DirectionsLeg legCar2 = new DirectionsLeg();
		legCar2.distance = this.distances[3];
		legCar2.duration = this.durations[0];
		DirectionsLeg[] legsCar2 = {legCar2};
		this.routeCar2.legs = legsCar2;
		
		DirectionsLeg legtransport1 = new DirectionsLeg();
		legtransport1.duration = this.durations[2];
		legtransport1.departureTime = this.times[0];
		legtransport1.arrivalTime = this.times[1];
		DirectionsLeg[] legstransport1 = {legtransport1};
		this.routeTransport1.legs = legstransport1;
		
		DirectionsLeg legBicycle1 = new DirectionsLeg();
		legBicycle1.distance = this.distances[3];
		legBicycle1.duration = this.durations[0];
		legBicycle1.durationInTraffic = this.durations[0];
		DirectionsLeg[] legsBicycle1 = {legBicycle1};
		this.routeBicycle1.legs = legsBicycle1;
		
	}

}
