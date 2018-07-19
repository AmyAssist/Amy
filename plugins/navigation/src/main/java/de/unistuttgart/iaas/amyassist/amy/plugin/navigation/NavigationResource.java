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
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Method;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Parameter;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Types;

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
	
	@Context
	private UriInfo info;

	/**
	 * Find the best route from A to B without a given departure time
	 * 
	 * @param route the route data given by the client
	 * @return a data structure with the best route and the transport type of the query
	 */
	@POST
	@Path("fromTo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public BestTransportResult routeFromTo(Route route) {
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public ZonedDateTime whenIHaveToGo(Route route) {
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public BestTransportResult getBestTransportInTime(Route route) {
		checkRoute(route);
		BestTransportResult bestTransport = this.logic.getBestTransportInTime(route.getOrigin(), route.getDestination(),
				convert(route.getTime()));
		if (bestTransport != null) {
			return bestTransport;
		}
		throw new WebApplicationException("No best transport type found.", Status.NOT_FOUND);
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
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceEntity getPluginDescripion() {
		ResourceEntity res = new ResourceEntity();
		res.setName("Navigation Plugin");
		res.setDescription("A Plugin to use basic methods of navigation");
		res.setMethods(this.getPluginMethods());
		res.setLink(this.info.getBaseUriBuilder().path(NavigationResource.class).build());
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginMethods()
	 */
	@Override
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method[] getPluginMethods() {
		Method[] methods = new Method[3];
		methods[0] = getFromToMethod();
		methods[1] = getWhenMethod();
		methods[2] = getBestMethod();
		return methods;
	}

	/**
	 * returns the method describing the getBestTranportInTime method
	 * 
	 * @return the describing method object
	 */
	@Path("best")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method getBestMethod() {
		Method best = new Method();
		best.setName("Best transport type");
		best.setDescription("Returns the best way of transport for a given route and a given arrival time");
		best.setType(Types.POST);
		best.setLink(this.info.getBaseUriBuilder().path(NavigationResource.class)
				.path(NavigationResource.class,"getBestTransportInTime").build());
		best.setParameters(getRouteAsParameter());
		return best;
	}

	/**
	 * returns the method describing the whenIHaveToGo method
	 * 
	 * @return the describing method object
	 */
	@Path("when")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method getWhenMethod() {
		Method when = new Method();
		when.setName("DepartureTime");
		when.setDescription("Returns the time you should leave for a given route and a given arrival time");
		when.setType(Types.POST);
		when.setLink(this.info.getBaseUriBuilder().path(NavigationResource.class)
				.path(NavigationResource.class,"getBestTransportInTime").build());
		when.setParameters(getRouteAsParameter());
		return when;
	}

	/**
	 * returns the method describing the routeFromTo method
	 * 
	 * @return the describing method object
	 */
	@Path("fromTo")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method getFromToMethod() {
		Method fromTo = new Method();
		fromTo.setName("Route from A to B");
		fromTo.setDescription("Returns the best route for a given origin and destination");
		fromTo.setType(Types.POST);
		fromTo.setLink(this.info.getBaseUriBuilder().path(NavigationResource.class)
				.path(NavigationResource.class,"getBestTransportInTime").build());
		fromTo.setParameters(getRouteAsParameter());
		fromTo.getParameters()[3].setRequired(false);
		return fromTo;
	}
	
	private Parameter[] getRouteAsParameter() {
		Parameter[] params = new Parameter[4];
		//Origin
		params[0] = new Parameter();
		params[0].setName("Origin");
		params[0].setRequired(true);
		params[0].setParamType(Types.BODY);
		params[0].setValueType(Types.STRING);
		//Destination
		params[1] = new Parameter();
		params[1].setName("Destination");
		params[1].setRequired(true);
		params[1].setParamType(Types.BODY);
		params[1].setValueType(Types.STRING);
		//Travelmode
		params[2] = new Parameter();
		params[2].setName("Travelmode");
		params[2].setRequired(true);
		params[2].setParamType(Types.BODY);
		params[2].setValueType(Types.STRING);
		//Time
		params[3] = new Parameter();
		params[3].setName("Time");
		params[3].setRequired(true);
		params[3].setParamType(Types.BODY);
		params[3].setValueType(Types.DATE);
		return params;
	}

}
