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
 * @author Lars Buttgereit, Muhammed Kaya
 */
@XmlRootElement
public class BestTransportResult {
	private TravelMode mode;
	private DirectionsRoute route;

	private static final String THE_ROUTE_IS = "The route is ";
	private static final String LONG_AND_NEED = " long and you will need ";

	/**
	 * constructor
	 */
	public BestTransportResult() {
		// Needed for JSON
	}

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
	 * Not implemented yet get a string with the detailed steps and times.
	 * 
	 * @return
	 */
	public String routeToLongString() {
		return null;
	}

	/**
	 * get a String with informations for example arrival, departure time, duration or distance
	 * 
	 * @return
	 */
	public String routeToShortString() {
		if (this.route != null && this.route.legs != null) {
			DirectionsLeg leg = this.route.legs[0];
			StringBuilder builder = new StringBuilder();
			switch (this.mode) {
			case DRIVING:
				if (leg.durationInTraffic != null) {
					return builder.append(THE_ROUTE_IS).append(leg.distance.humanReadable).append(LONG_AND_NEED)
							.append(leg.durationInTraffic.humanReadable).append(" time in traffic").toString();
				}
				return builder.append(THE_ROUTE_IS).append(leg.distance.humanReadable).append(LONG_AND_NEED)
						.append(leg.duration.humanReadable).append(" time").toString();
			case TRANSIT:
				return builder.append("Departure time is ").append(leg.departureTime.toString("HH:mm")).append(" at ")
						.append(leg.steps[0].transitDetails.departureStop.name).append(" with line ")
						.append(leg.steps[0].transitDetails.line.shortName).append(" to ")
						.append(leg.steps[0].transitDetails.headsign).append(", arrival time is ")
						.append(leg.arrivalTime.toString("HH:mm")).toString();
			case BICYCLING:
				return builder.append(THE_ROUTE_IS).append(leg.distance.humanReadable).append(LONG_AND_NEED)
						.append(leg.duration.humanReadable).append(" time").toString();
			default:
				break;
			}
		}
		return "";
	}

	/**
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BestTransportResult) {
			BestTransportResult best = (BestTransportResult) obj;
			return this.mode.equals(best.mode) && this.route.equals(best.route);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.mode == null) ? 0 : this.mode.hashCode());
		result = prime * result + ((this.route == null) ? 0 : this.route.hashCode());
		return result;
	}

}
