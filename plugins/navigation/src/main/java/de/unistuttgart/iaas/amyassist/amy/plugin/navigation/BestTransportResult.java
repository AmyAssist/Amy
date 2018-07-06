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

import javax.xml.bind.annotation.XmlRootElement;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

/**
 * This class is needed to store the best route and the transport type
 * 
 * @author Lars Buttgereit
 */
@XmlRootElement
public class BestTransportResult {
	private TravelMode mode;
	private DirectionsRoute route;
	
	private static final String THE_ROUTE_IS = "The route is ";
	private static final String LONG_AND_NEED = " long and need ";

	/**
	 * constructor
	 * 
	 * @param mode
	 * @param route
	 */
	public BestTransportResult(TravelMode mode, DirectionsRoute route) {
		this.mode = mode;
		this.route = route;
	}

	/**
	 * Get's {@link #mode mode}
	 * 
	 * @return mode
	 */
	public TravelMode getMode() {
		return this.mode;
	}

	/**
	 * Get's {@link #route route}
	 * 
	 * @return route
	 */
	public DirectionsRoute getRoute() {
		return this.route;
	}

	/**
	 * Not implemented yet
	 * get a string with the detailed steps and times. 
	 * @return
	 */
	public String routeToLongString() {
		return null;
	}

	public String routeToShortString() {
		if (this.route != null && this.route.legs != null) {
			DirectionsLeg leg = this.route.legs[0];
			switch (this.mode) {
			case DRIVING:
				if (leg.durationInTraffic != null) {
					return THE_ROUTE_IS.concat(leg.distance.humanReadable)
							.concat(LONG_AND_NEED.concat(leg.durationInTraffic.humanReadable))
							.concat(" time in Traffic");
				}
				return "THE_ROUTE_IS".concat(leg.distance.humanReadable)
						.concat(LONG_AND_NEED.concat(leg.duration.humanReadable)).concat(" time");
			case TRANSIT:
				return "Departure time is ".concat(leg.departureTime.toString("HH:mm")).concat(", arrival time is ")
						.concat(leg.arrivalTime.toString("HH:mm"));
			case BICYCLING:
				return "THE_ROUTE_IS".concat(leg.distance.humanReadable)
						.concat(LONG_AND_NEED.concat(leg.duration.humanReadable)).concat(" time");
			default:
				break;

			}
		}
		return "";

	}

}
