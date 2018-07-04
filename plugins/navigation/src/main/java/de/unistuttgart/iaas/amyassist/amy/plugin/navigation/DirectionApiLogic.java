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

import java.util.EnumMap;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * This class implements the logic that is needed to call the DirectionsApiCalls and processing the results
 * 
 * @author Lars Buttgereit
 */
@Service
public class DirectionApiLogic {

	@Reference
	private DirectionsApiCalls calls;

	/*
	 * public DirectionsRoute fromTo() { DirectionsRoute[] routes = this.calls.fromToWithDepartureTime("Friolzheim",
	 * "Universität Stuttgart", getTravelMode("Car"), DateTime.now()); ReadableInstant minTime = null;
	 * for(DirectionsRoute route : routes) { for(DirectionsLeg leg : route.legs) { if(minTime == null) { minTime =
	 * leg.arrivalTime; } else if(minTime.) } }
	 * 
	 * }
	 */
	// public

	/**
	 * this method says when you have to go from one place to another place with the planned time
	 * 
	 * @param origin
	 * @param destination
	 * @param mode
	 *            driving, transit, etc
	 * @param arrivalTime
	 *            time you plan to arrive at the destination
	 * @return a ReadableInstnat with the latest start time, can be null
	 */
	public ReadableInstant whenIHaveToGo(String origin, String destination, TravelMode mode, DateTime arrivalTime) {
		DirectionsRoute[] routes = this.calls.fromToWithDepartureTime(origin, destination, mode, DateTime.now());
		DirectionsLeg leg;
		switch (mode) {
		case DRIVING:
			leg = findBestRoute(routes, true);
			if (leg != null && arrivalTime.getMillis() > DateTime.now()
					.plusSeconds(Math.toIntExact(leg.durationInTraffic.inSeconds)).getMillis()) {
				return new DateTime(
						arrivalTime.minusSeconds(Math.toIntExact(leg.durationInTraffic.inSeconds)).getMillis());
			}
			break;
		case TRANSIT:
			leg = findBestRoute(routes, false);
			if (leg != null && arrivalTime.getMillis() > DateTime.now()
					.plusSeconds(Math.toIntExact(leg.duration.inSeconds)).getMillis()) {
				return new DateTime(arrivalTime.minusSeconds(Math.toIntExact(leg.duration.inSeconds)).getMillis());
			}
			break;
		default:
			break;
		}

		return null;
	}

	public BestTransportResult getBestTransportInTime(String origin, String destination,
			ReadableInstant departureTime) {
		EnumMap<TravelMode, DirectionsLeg> routesOfTravelModes = new EnumMap<>(TravelMode.class);
		routesOfTravelModes.put(TravelMode.DRIVING, findBestRoute(
				this.calls.fromToWithDepartureTime(origin, destination, TravelMode.DRIVING, departureTime), true));
		routesOfTravelModes.put(TravelMode.TRANSIT, findBestArrivalTime(
				this.calls.fromToWithDepartureTime(origin, destination, TravelMode.TRANSIT, departureTime)));
		routesOfTravelModes.put(TravelMode.BICYCLING, findBestRoute(
				this.calls.fromToWithDepartureTime(origin, destination, TravelMode.BICYCLING, departureTime), false));
		long bestTime = Long.MAX_VALUE;
		TravelMode bestTravelMode = null;
		long time = new DateTime(departureTime)
				.plusSeconds(Math.toIntExact(routesOfTravelModes.get(TravelMode.DRIVING).durationInTraffic.inSeconds))
				.getMillis();
		if (routesOfTravelModes.get(TravelMode.DRIVING) != null && bestTime > time) {
			bestTravelMode = TravelMode.DRIVING;
			bestTime = time;
		}
		time = routesOfTravelModes.get(TravelMode.TRANSIT).arrivalTime.getMillis();
		if (routesOfTravelModes.get(TravelMode.TRANSIT) != null && bestTime > time) {
			bestTravelMode = TravelMode.TRANSIT;
			bestTime = time;
		}
		time = new DateTime(departureTime)
				.plusSeconds(Math.toIntExact(routesOfTravelModes.get(TravelMode.BICYCLING).duration.inSeconds))
				.getMillis();
		if (routesOfTravelModes.get(TravelMode.BICYCLING) != null && bestTime > time) {
			bestTravelMode = TravelMode.BICYCLING;
		}
		if (bestTravelMode != null) {
			return new BestTransportResult(bestTravelMode, routesOfTravelModes.get(bestTravelMode));
		}
		return null;
	}

	/**
	 * find the route with the shortest time
	 * 
	 * @param routes
	 *            result from the call
	 * @param withTraffic
	 *            true if driving by car, else false
	 * @return the shortest route, can be null
	 */
	private DirectionsLeg findBestRoute(DirectionsRoute[] routes, boolean withTraffic) {
		long shortestTime = Long.MAX_VALUE;
		DirectionsLeg shortestRoute = null;
		if (routes != null) {
			for (DirectionsRoute route : routes) {
				for (DirectionsLeg leg : route.legs) {
					if (withTraffic && leg.durationInTraffic.inSeconds < shortestTime) {
						shortestTime = leg.durationInTraffic.inSeconds;
						shortestRoute = leg;
					} else if (leg.duration.inSeconds < shortestTime) {
						shortestTime = leg.duration.inSeconds;
						shortestRoute = leg;
					}
				}
			}
		}
		return shortestRoute;
	}

	/**
	 * find the best arrival time. Use only for results from TransportMode.TRANSIT
	 * 
	 * @param routes
	 * @return
	 */
	private DirectionsLeg findBestArrivalTime(DirectionsRoute[] routes) {
		long shortestTime = Long.MAX_VALUE;
		DirectionsLeg shortestRoute = null;
		if (routes != null) {
			for (DirectionsRoute route : routes) {
				for (DirectionsLeg leg : route.legs) {
					if (leg.arrivalTime != null && leg.arrivalTime.getMillis() < shortestTime) {
						shortestTime = leg.arrivalTime.getMillis();
						shortestRoute = leg;
					}
				}
			}
		}
		return shortestRoute;
	}

	private TravelMode getTravelMode(String mode) {
		switch (mode.toLowerCase()) {
		case "driving":
		case "car":
			return TravelMode.DRIVING;
		case "bicycling":
		case "bike":
			return TravelMode.BICYCLING;
		case "transit":
		case "public transport":
			return TravelMode.TRANSIT;
		case "walking":
		case "walk":
			return TravelMode.WALKING;
		default:
			return null;
		}
	}

}
