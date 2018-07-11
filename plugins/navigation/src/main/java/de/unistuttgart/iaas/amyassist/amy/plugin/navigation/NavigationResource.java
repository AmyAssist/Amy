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

import java.time.ZonedDateTime;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;

/**
 * Rest Resource for navigation
 * 
 * @author Muhammed Kaya
 */
@Path(NavigationResource.PATH)
public class NavigationResource implements Resource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "navigation";

	@Reference
	private DirectionApiLogic logic;

	/**
	 * Find the best route from A to B without a given departure time
	 * 
	 * @param origin
	 *            where to start
	 * @param destination
	 *            where to end
	 * @param travelMode
	 *            How to travel: Allowed parameters: driving, car, bicycling, bike, transit, public transport,
	 *            transport, walking, walk
	 * @return a data structure with the best route and the transport type of the query
	 */
	@POST
	@Path("fromTo")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public BestTransportResult routeFromTo(@QueryParam("origin") @DefaultValue("") String origin,
			@QueryParam("destination") @DefaultValue("") String destination,
			@QueryParam("travelMode") String travelMode) {
		checkOriginDestination(origin, destination);
		checkTravelMode(travelMode);
		TravelMode mode = this.logic.getTravelMode(travelMode);
		BestTransportResult bestRoute = this.logic.fromTo(origin, destination, mode);
		if (bestRoute != null) {
			return bestRoute;
		}
		throw new WebApplicationException("No route found.", Status.NOT_FOUND);
	}
	
	/**
	 * Find the best route from A to B with a given departure time
	 * 
	 * @param origin
	 *            where to start
	 * @param destination
	 *            where to end
	 * @param travelMode
	 *            How to travel: Allowed parameters: driving, car, bicycling, bike, transit, public transport,
	 *            transport, walking, walk
	 * @param departureTime
	 *            time you plan to depart at the origin
	 * @return a data structure with the best route and the transport type of the query
	 */
	@POST
	@Path("fromTo/{departureTime}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public BestTransportResult routeFromTo(@QueryParam("origin") @DefaultValue("") String origin,
			@QueryParam("destination") @DefaultValue("") String destination,
			@QueryParam("travelMode") String travelMode, @PathParam("departureTime") ZonedDateTime departureTime) {
		checkOriginDestination(origin, destination);
		checkTravelMode(travelMode);
		TravelMode mode = this.logic.getTravelMode(travelMode);
		BestTransportResult bestRoute = this.logic.fromToWithDeparture(origin, destination, mode, convert(departureTime));
		if (bestRoute != null) {
			return bestRoute;
		}
		throw new WebApplicationException("No route found.", Status.NOT_FOUND);
	}

	/**
	 * This method says when you have to go from one place to another place with the planned time
	 * 
	 * @param origin
	 *            where to start
	 * @param destination
	 *            where to end
	 * @param travelMode
	 *            How to travel: Allowed parameters: driving, car, bicycling, bike, transit, public transport,
	 *            transport, walking, walk
	 * @param arrivalTime
	 *            time you plan to arrive at the destination
	 * @return ZonedDateTime object with the latest start time
	 */
	@POST
	@Path("when/{arrivalTime}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public ZonedDateTime whenIHaveToGo(@QueryParam("origin") @DefaultValue("") String origin,
			@QueryParam("destination") @DefaultValue("") String destination,
			@QueryParam("travelMode") String travelMode, @PathParam("arrivalTime") ZonedDateTime arrivalTime) {
		checkOriginDestination(origin, destination);
		checkTravelMode(travelMode);
		TravelMode mode = this.logic.getTravelMode(travelMode);
		ReadableInstant rtime = this.logic.whenIHaveToGo(origin, destination, mode, convert(arrivalTime));
		if (rtime != null) {
			return ZonedDateTime.parse(rtime.toString());
		}
		throw new WebApplicationException("No latest starttime found.", Status.NOT_FOUND);
	}

	/**
	 * this method finds the best transport type out of driving, transit and bicycling
	 * 
	 * @param origin
	 *            where to start
	 * @param destination
	 *            where to end
	 * @param departureTime
	 *            time you plan to depart at the origin
	 * @return data structure with the best route and the transport type
	 */
	@POST
	@Path("best/{departureTime}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public BestTransportResult getBestTransportInTime(@QueryParam("origin") @DefaultValue("") String origin,
			@QueryParam("destination") @DefaultValue("") String destination,
			@PathParam("departureTime") ZonedDateTime departureTime) {
		checkOriginDestination(origin, destination);
		BestTransportResult bestTransport = this.logic.getBestTransportInTime(origin, destination,
				convert(departureTime));
		if (bestTransport != null) {
			return bestTransport;
		}
		throw new WebApplicationException("No best transport type found.", Status.NOT_FOUND);
	}

	/**
	 * checks if origin and destination inputs are correct
	 * 
	 * @param origin
	 *            where to start
	 * @param destination
	 *            where to end
	 * @return true if origin and destination inputs are correct
	 */
	private boolean checkOriginDestination(String origin, String destination) {
		if (origin != null && !origin.equals("") && destination != null && !destination.equals("")) {
			return true;
		}
		throw new WebApplicationException("Missing origin and/or destination input.", Status.CONFLICT);
	}

	/**
	 * checks if travel mode input is correct
	 * 
	 * @param mode
	 *            How to travel: Allowed parameters: driving, car, bicycling, bike, transit, public transport,
	 *            transport, walking, walk
	 * 
	 * @return true if mode input is correct
	 */
	private boolean checkTravelMode(String mode) {
		TravelMode travelMode = this.logic.getTravelMode(mode);
		if (travelMode != null) {
			return true;
		}
		throw new WebApplicationException("Enter a correct travel mode.", Status.CONFLICT);
	}

	/**
	 * @param zDateTime
	 *            a ZonedDateTime object to convert
	 * @return converted DateTime
	 */
	private DateTime convert(ZonedDateTime zDateTime) {
		DateTime dateTime = new DateTime(zDateTime.toInstant().toEpochMilli());
		return dateTime;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		return null;
	}

}
