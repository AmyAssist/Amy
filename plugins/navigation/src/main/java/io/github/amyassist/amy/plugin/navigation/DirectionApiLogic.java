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

import java.util.EnumMap;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * This class implements the logic that is needed to call the DirectionsApiCalls and processing the results
 * 
 * @author Lars Buttgereit
 */
@Service
public class DirectionApiLogic {

	@Reference
	private DirectionsApiCalls calls;

	/**
	 * find the best route from A to B with a given departure time
	 * 
	 * @param origin
	 * @param destination
	 * @param mode
	 *            driving, transit, etc
	 * @return a data structure with the best route and the transport type of the query
	 */
	public BestTransportResult fromTo(String origin, String destination, TravelMode mode) {
		DirectionsRoute[] routes = this.calls.fromTo(origin, destination, mode);
		if (mode == TravelMode.TRANSIT) {
			return new BestTransportResult(mode, findBestArrivalTime(routes));
		}
		return new BestTransportResult(mode, findBestRoute(routes));
	}

	/**
	 * find the best route from A to B with a given departure time
	 * 
	 * @param origin
	 * @param destination
	 * @param mode
	 *            driving, transit, etc
	 * @param departureTime
	 * @return a data structure with the best route and the transport type of the query
	 */
	public BestTransportResult fromToWithDeparture(String origin, String destination, TravelMode mode,
			ReadableInstant departureTime) {
		DirectionsRoute[] routes = this.calls.fromToWithDepartureTime(origin, destination, mode, departureTime);
		if (mode == TravelMode.TRANSIT) {
			return new BestTransportResult(mode, findBestArrivalTime(routes));
		}
		return new BestTransportResult(mode, findBestRoute(routes));
	}

	/**
	 * this method says when you have to go from one place to another place with the planned time
	 * 
	 * @param origin
	 * @param destination
	 * @param mode
	 *            only tranist and driving are supported
	 * @param arrivalTime
	 *            time you plan to arrive at the destination
	 * @return a ReadableInstnat with the latest start time, can be null
	 */
	public ReadableInstant whenIHaveToGo(String origin, String destination, TravelMode mode, DateTime arrivalTime) {
		DirectionsRoute[] routes;
		DirectionsRoute route;
		switch (mode) {
		case DRIVING:
		case BICYCLING:
			routes = this.calls.fromToWithDepartureTime(origin, destination, mode, DateTime.now());
			route = findBestRoute(routes);
			if (route != null && route.legs[0].durationInTraffic != null && arrivalTime.getMillis() > DateTime.now()
					.plusSeconds(Math.toIntExact(route.legs[0].durationInTraffic.inSeconds)).getMillis()) {
				return new DateTime(arrivalTime.minusSeconds(Math.toIntExact(route.legs[0].durationInTraffic.inSeconds))
						.getMillis());
			} else if (route != null && route.legs[0].duration != null && arrivalTime.getMillis() > DateTime.now()
					.plusSeconds(Math.toIntExact(route.legs[0].duration.inSeconds)).getMillis()) {
				return new DateTime(
						arrivalTime.minusSeconds(Math.toIntExact(route.legs[0].duration.inSeconds)).getMillis());
			}
			break;
		case TRANSIT:
			routes = this.calls.fromToWithArrivalTime(destination, origin, mode, arrivalTime);
			if (routes != null && routes[0] != null) {
				return routes[0].legs[0].departureTime;
			}
			break;
		default:
			break;
		}

		return null;
	}

	/**
	 * this find the best transport type out of driving, transit and bicycling
	 * 
	 * @param origin
	 * @param destination
	 * @param departureTime
	 * @return a data structure with the best route and the transport type
	 */
	public BestTransportResult getBestTransportInTime(String origin, String destination,
			ReadableInstant departureTime) {
		EnumMap<TravelMode, DirectionsRoute> routesOfTravelModes = new EnumMap<>(TravelMode.class);
		routesOfTravelModes.put(TravelMode.DRIVING, findBestRoute(
				this.calls.fromToWithDepartureTime(origin, destination, TravelMode.DRIVING, departureTime)));
		routesOfTravelModes.put(TravelMode.TRANSIT, findBestArrivalTime(
				this.calls.fromToWithDepartureTime(origin, destination, TravelMode.TRANSIT, departureTime)));
		routesOfTravelModes.put(TravelMode.BICYCLING, findBestRoute(
				this.calls.fromToWithDepartureTime(origin, destination, TravelMode.BICYCLING, departureTime)));
		long bestTime = Long.MAX_VALUE;
		TravelMode bestTravelMode = null;
		long time;
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
	private DirectionsRoute findBestRoute(DirectionsRoute[] routes) {
		long shortestTime = Long.MAX_VALUE;
		DirectionsRoute shortestRoute = null;
		if (routes != null) {
			for (DirectionsRoute route : routes) {
				if (route.legs[0] != null && route.legs[0].durationInTraffic != null) {
					if (route.legs[0].durationInTraffic.inSeconds < shortestTime) {
						shortestTime = route.legs[0].durationInTraffic.inSeconds;
						shortestRoute = route;
					}
				} else if (route.legs[0] != null && route.legs[0].duration.inSeconds < shortestTime) {
					shortestTime = route.legs[0].duration.inSeconds;
					shortestRoute = route;
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

	/**
	 * transfer a string to the apt TravelMode of the api
	 * 
	 * @param mode
	 *            supported Strings: driving, car, bicycling, transit, public transport, transport, walking, walk, etc.
	 * @return apt travelMode
	 */
	public TravelMode getTravelMode(String mode) {
		switch (mode.toLowerCase()) {
		case "driving":
		case "car":
			return TravelMode.DRIVING;
		case "bicycling":
		case "bicycle":
		case "bike":
			return TravelMode.BICYCLING;
		case "transit":
		case "public transport":
		case "public transit":
		case "transport":
			return TravelMode.TRANSIT;
		case "walking":
		case "walk":
			return TravelMode.WALKING;
		default:
			return null;
		}
	}

	String getStaticAPIKey() {
		return this.calls.getStaticAPIKey();
	}

}
