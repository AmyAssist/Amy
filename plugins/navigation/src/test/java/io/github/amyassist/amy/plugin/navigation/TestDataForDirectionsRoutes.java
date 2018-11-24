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
	protected DirectionsRoute[] carRoutes = new DirectionsRoute[6];
	protected DirectionsRoute[] transportRoutes = new DirectionsRoute[7];
	protected DirectionsRoute[] bicycleRoutes = new DirectionsRoute[2];

	private Distance[] distances = new Distance[4];
	private Duration[] durations = new Duration[5];
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
		this.durations[4].humanReadable = "3 min";
		this.durations[4].inSeconds = 180;
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
		for(int i = 0; i < this.carRoutes.length; i++) {
			this.carRoutes[i] = new DirectionsRoute();
		}
		for(int i = 0; i < this.transportRoutes.length; i++) {
			this.transportRoutes[i] = new DirectionsRoute();
		}
		for(int i = 0; i < this.bicycleRoutes.length; i++) {
			this.bicycleRoutes[i] = new DirectionsRoute();
		}
				
		this.carRoutes[0] = createCarRoute(this.distances[3], this.durations[0], this.durations[0]);		
		this.carRoutes[1] = createCarRoute(this.distances[3], this.durations[0], null);		
		this.carRoutes[2] = createCarRoute( this.distances[1], null, this.durations[2]);		
		this.carRoutes[3].legs = new DirectionsLeg[1];		
		this.carRoutes[4] = createCarRoute(this.distances[3], this.durations[4], null);
		this.carRoutes[5] = createCarRoute( this.distances[0], this.durations[3], this.durations[3]);
		
		this.transportRoutes[0] = createTransportRoute(this.durations[2], this.times[0], this.times[1]);
		this.transportRoutes[1] = createTransportRoute(this.durations[2], this.times[0], this.times[2]);		
		this.transportRoutes[2] = createTransportRoute(this.durations[3], this.times[0], this.times[0]);
		this.transportRoutes[3] = createTransportRoute(this.durations[3], this.times[0], null);
		this.transportRoutes[4].legs = new DirectionsLeg[1];
		this.transportRoutes[5] = createTransportRoute(this.durations[3], new DateTime(Long.MAX_VALUE), new DateTime(Long.MAX_VALUE));
		this.transportRoutes[6] = createTransportRoute(this.durations[3], new DateTime(Long.MIN_VALUE), new DateTime(Long.MIN_VALUE));
		
		this.bicycleRoutes[0] = createBikeRoute(this.distances[3], this.durations[0]);
		this.bicycleRoutes[1] = createBikeRoute(this.distances[0], this.durations[3]);
		
	}
	private DirectionsRoute createCarRoute(Distance distance, Duration duration, Duration traffic) {
		DirectionsRoute route = new DirectionsRoute();
		route.legs = new DirectionsLeg[1];
		route.legs[0] = new DirectionsLeg();
		route.legs[0].distance = distance;
		route.legs[0].duration = duration;
		route.legs[0].durationInTraffic = traffic;
		return route;
	}
	
	private DirectionsRoute createBikeRoute(Distance distance, Duration duration) {
		DirectionsRoute route = new DirectionsRoute();
		route.legs = new DirectionsLeg[1];
		route.legs[0] = new DirectionsLeg();
		route.legs[0].distance = distance;
		route.legs[0].duration = duration;
		return route;
	}
	
	private DirectionsRoute createTransportRoute(Duration duration, DateTime depatureTime, DateTime arrivalTime) {
		DirectionsRoute route = new DirectionsRoute();
		route.legs = new DirectionsLeg[1];
		route.legs[0] = new DirectionsLeg();
		route.legs[0].duration = duration;
		route.legs[0].departureTime = depatureTime;
		route.legs[0].arrivalTime = arrivalTime;
		return route;
	}

}
