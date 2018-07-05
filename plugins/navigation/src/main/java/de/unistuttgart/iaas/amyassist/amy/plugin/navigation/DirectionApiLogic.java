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

	public DirectionsRoute fromTo(String origin, String destination, TravelMode mode) {
		DirectionsRoute[] routes = this.calls.fromTo(origin, destination, mode);
		return findBestRoute(routes, false);
	}

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
		DirectionsRoute route;
		switch (mode) {
		case DRIVING:
			route = findBestRoute(routes, true);
			if (route != null && arrivalTime.getMillis() > DateTime.now()
					.plusSeconds(Math.toIntExact(route.legs[0].durationInTraffic.inSeconds)).getMillis()) {
				return new DateTime(arrivalTime.minusSeconds(Math.toIntExact(route.legs[0].durationInTraffic.inSeconds))
						.getMillis());
			}
			break;
		case TRANSIT:
			route = findBestRoute(routes, false);
			if (route != null && arrivalTime.getMillis() > DateTime.now()
					.plusSeconds(Math.toIntExact(route.legs[0].duration.inSeconds)).getMillis()) {
				return new DateTime(
						arrivalTime.minusSeconds(Math.toIntExact(route.legs[0].duration.inSeconds)).getMillis());
			}
			break;
		default:
			break;
		}

		return null;
	}

	public BestTransportResult getBestTransportInTime(String origin, String destination,
			ReadableInstant departureTime) {
		EnumMap<TravelMode, DirectionsRoute> routesOfTravelModes = new EnumMap<>(TravelMode.class);
		routesOfTravelModes.put(TravelMode.DRIVING, findBestRoute(
				this.calls.fromToWithDepartureTime(origin, destination, TravelMode.DRIVING, departureTime), true));
		routesOfTravelModes.put(TravelMode.TRANSIT, findBestArrivalTime(
				this.calls.fromToWithDepartureTime(origin, destination, TravelMode.TRANSIT, departureTime)));
		routesOfTravelModes.put(TravelMode.BICYCLING, findBestRoute(
				this.calls.fromToWithDepartureTime(origin, destination, TravelMode.BICYCLING, departureTime), false));
		long bestTime = Long.MAX_VALUE;
		TravelMode bestTravelMode = null;
		long time = Long.MAX_VALUE;
		if (routesOfTravelModes.get(TravelMode.DRIVING) != null) {
			time = new DateTime(departureTime)
					.plusSeconds(Math.toIntExact(
							routesOfTravelModes.get(TravelMode.DRIVING).legs[0].durationInTraffic.inSeconds))
					.getMillis();
			if (bestTime > time) {
				bestTravelMode = TravelMode.DRIVING;
				bestTime = time;
			}
		}
		if (routesOfTravelModes.get(TravelMode.TRANSIT) != null) {
			time = routesOfTravelModes.get(TravelMode.TRANSIT).legs[0].arrivalTime.getMillis();
			if (bestTime > time) {
				bestTravelMode = TravelMode.TRANSIT;
				bestTime = time;
			}
		}
		if (routesOfTravelModes.get(TravelMode.BICYCLING) != null) {
			time = new DateTime(departureTime)
					.plusSeconds(
							Math.toIntExact(routesOfTravelModes.get(TravelMode.BICYCLING).legs[0].duration.inSeconds))
					.getMillis();
			if (bestTime > time) {
				bestTravelMode = TravelMode.BICYCLING;
			}
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
	private DirectionsRoute findBestRoute(DirectionsRoute[] routes, boolean withTraffic) {
		long shortestTime = Long.MAX_VALUE;
		DirectionsRoute shortestRoute = null;
		if (routes != null) {
			for (DirectionsRoute route : routes) {
				if (route.legs[0] != null) {
					if (withTraffic && route.legs[0].durationInTraffic.inSeconds < shortestTime) {
						shortestTime = route.legs[0].durationInTraffic.inSeconds;
						shortestRoute = route;
					} else if (route.legs[0].duration.inSeconds < shortestTime) {
						shortestTime = route.legs[0].duration.inSeconds;
						shortestRoute = route;
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
	private DirectionsRoute findBestArrivalTime(DirectionsRoute[] routes) {
		long shortestTime = Long.MAX_VALUE;
		DirectionsRoute shortestRoute = null;
		if (routes != null) {
			for (DirectionsRoute route : routes) {
				if (route.legs[0] != null && route.legs[0].arrivalTime != null
						&& route.legs[0].arrivalTime.getMillis() < shortestTime) {
					shortestTime = route.legs[0].arrivalTime.getMillis();
					shortestRoute = route;
				}
			}
		}
		return shortestRoute;
	}

	public TravelMode getTravelMode(String mode) {
		switch (mode.toLowerCase()) {
		case "driving":
		case "car":
			return TravelMode.DRIVING;
		case "bicycling":
		case "bike":
			return TravelMode.BICYCLING;
		case "transit":
		case "public transport":
		case "transport":
			return TravelMode.TRANSIT;
		case "walking":
		case "walk":
			return TravelMode.WALKING;
		default:
			return null;
		}
	}

}
