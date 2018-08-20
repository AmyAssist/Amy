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

import javax.ws.rs.*;
import static javax.ws.rs.core.MediaType.*;

import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;

/**
 * Rest Resource for navigation
 * 
 * @author Muhammed Kaya
 * @author Benno Krauß
 */
@Path(NavigationResource.PATH)
public class NavigationResource implements Resource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "navigation";

	@Reference
	private DirectionApiLogic logic;

	@Reference
	private RegistryConnection registryConnection;


	/**
	 * Find the best route from A to B without a given departure time
	 * 
	 * @param route the route data given by the client
	 * @return a data structure with the best route and the transport type of the query
	 */
	@POST
	@Path("fromTo")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public BestTransportResult routeFromTo(Route route) {
		resolveLocationTags(route);
		checkRoute(route);
		checkTravelMode(route.getTravelmode());
		TravelMode mode = this.logic.getTravelMode(route.getTravelmode());
		BestTransportResult bestRoute;
		if(route.getTime() == null) {
			bestRoute = this.logic.fromTo(route.getOrigin(), route.getDestination(), mode);			
		} else {
			bestRoute = this.logic.fromToWithDeparture(route.getOrigin(), route.getDestination(), mode,
					convert(route.getTime()));
		}
		if (bestRoute != null) {
			return bestRoute;
		}
		throw new WebApplicationException("No route found.", Status.NOT_FOUND);
	}

	/**
	 * This method says when you have to go from one place to another place with the planned time
	 * 
	 * @param route the route data send by the client
	 * @return ZonedDateTime object with the latest start time
	 */
	@POST
	@Path("when")
	@Consumes(APPLICATION_JSON)
	@Produces(TEXT_PLAIN)
	public ZonedDateTime whenIHaveToGo(Route route) {
		resolveLocationTags(route);
		checkRoute(route);
		checkTravelMode(route.getTravelmode());
		TravelMode mode = this.logic.getTravelMode(route.getTravelmode());
		ReadableInstant rtime = this.logic.whenIHaveToGo(route.getOrigin(), route.getDestination(), 
				mode, convert(route.getTime()));
		if (rtime != null) {
			return ZonedDateTime.parse(rtime.toString());
		}
		throw new WebApplicationException("No latest starttime found.", Status.NOT_FOUND);
	}

	/**
	 * this method finds the best transport type out of driving, transit and bicycling
	 * 
	 * @param route the route information send by the client
	 * @return data structure with the best route and the transport type
	 */
	@POST
	@Path("best")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public BestTransportResult getBestTransportInTime(Route route) {
		resolveLocationTags(route);
		checkRoute(route);
		BestTransportResult bestTransport = this.logic.getBestTransportInTime(route.getOrigin(), route.getDestination(),
				convert(route.getTime()));
		if (bestTransport != null) {
			return bestTransport;
		}
		throw new WebApplicationException("No best transport type found.", Status.NOT_FOUND);
	}

	@GET
	@Path("tags")
	@Produces(APPLICATION_JSON)
	public String[] getTags() {
		return registryConnection.getAllLocationTags();
	}

	/**
	 * checks if origin and destination inputs are correct
	 * 
	 * @param route the route to check
	 * @return true if origin and destination inputs are correct
	 */
	private boolean checkRoute(Route route) {
		if (route != null && route.getOrigin() != null && !route.getOrigin().equals("") 
				&& route.getDestination() != null && !route.getDestination().equals("")) {
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

	private void resolveLocationTags(Route r) {
		if (r == null) {
			return;
		}

		if ((r.getDestination() == null || r.getDestination().isEmpty()) && r.getDestinationTag() != null) {
			r.setDestination(registryConnection.getAddress(r.getDestinationTag()));
		}

		if ((r.getOrigin() == null || r.getOrigin().isEmpty()) && r.getOriginTag() != null) {
			r.setOrigin(registryConnection.getAddress(r.getOriginTag()));
		}
	}

	/**
	 * @param zDateTime
	 *            a ZonedDateTime object to convert
	 * @return converted DateTime
	 */
	private DateTime convert(ZonedDateTime zDateTime) {
		return new DateTime(zDateTime.toInstant().toEpochMilli());
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		return null;
	}

}
