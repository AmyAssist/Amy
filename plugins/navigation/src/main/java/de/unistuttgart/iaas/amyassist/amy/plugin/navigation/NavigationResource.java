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

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

import java.util.Calendar;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableInstant;

/**
 * Rest Resource for navigation
 * 
 * @author Muhammed Kaya
 */
@Path(NavigationResource.PATH)
public class NavigationResource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "navigation";

	@Reference
	private DirectionApiLogic logic;

	/**
	 * find the best route from A to B with or without a given departure time
	 * 
	 * @param origin
	 *            where to start
	 * @param destination
	 *            where to end
	 * @param travelMode
	 *            How to travel: Allowed parameters: driving, car, bicycling, bike, transit, public transport,
	 *            transport, walking, walk
	 * @param minute
	 *            in which minute to start: allowed parameters: from 0 to 60, can be null
	 * @param hour
	 *            in which hour to start: allowed parameters: from 0 to 24, can be null
	 * @return a data structure with the best route and the transport type of the query
	 */
	@POST
	@Path("fromTo")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public BestTransportResult routeFromTo(@QueryParam("origin") String origin,
			@QueryParam("destination") String destination, @QueryParam("travelMode") String travelMode,
			@QueryParam("minute") @DefaultValue("-1") int minute, @QueryParam("hour") @DefaultValue("-1") int hour) {
		if (!checkOriginDestination(origin, destination)) {
			throw new WebApplicationException("Missing origin and/or destination input.", Status.CONFLICT);
		}
		if (!checkTravelMode(travelMode)) {
			throw new WebApplicationException("Enter correct a correct travel mode", Status.CONFLICT);
		}
		TravelMode mode = this.logic.getTravelMode(travelMode);
		BestTransportResult bestRoute;
		if (minute == -1 && hour == -1) {
			bestRoute = this.logic.fromTo(origin, destination, mode);
		} else {
			bestRoute = this.logic.fromToWithDeparture(origin, destination, mode, formatTimes(minute, hour));
		}
		if (bestRoute != null) {
			return bestRoute;
		}
		throw new WebApplicationException("No route found.", Status.NOT_FOUND);
	}

	/**
	 * this method says when you have to go from one place to another place with the planned time
	 * 
	 * @param origin
	 *            where to start
	 * @param destination
	 *            where to end
	 * @param travelMode
	 *            How to travel: Allowed parameters: driving, car, bicycling, bike, transit, public transport,
	 *            transport, walking, walk
	 * @param minute
	 *            in which minute to start: allowed parameters: from 0 to 60
	 * @param hour
	 *            in which hour to start: allowed parameters: from 0 to 24
	 * @return a ReadableInstant with the latest start time
	 */
	@POST
	@Path("when")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String whenIHaveToGo(@QueryParam("origin") @DefaultValue("") String origin,
			@QueryParam("destination") @DefaultValue("") String destination,
			@QueryParam("travelMode") String travelMode, @QueryParam("minute") int minute,
			@QueryParam("hour") int hour) {
		if (!checkOriginDestination(origin, destination)) {
			throw new WebApplicationException("Missing origin and/or destination input.", Status.CONFLICT);
		}
		if (!checkTravelMode(travelMode)) {
			throw new WebApplicationException("Enter correct a correct travel mode", Status.CONFLICT);
		}
		TravelMode mode = this.logic.getTravelMode(travelMode);
		ReadableInstant time = this.logic.whenIHaveToGo(origin, destination, mode, formatTimes(minute, hour));
		if (time != null) {
			return String.valueOf(time.get(DateTimeFieldType.hourOfDay())).concat(":")
					.concat(String.valueOf(time.get(DateTimeFieldType.minuteOfHour())));
		}
		throw new WebApplicationException("No latest starttime found.", Status.NOT_FOUND);
	}

	/**
	 * this find the best transport type out of driving, transit and bicycling
	 * 
	 * @param origin
	 *            where to start
	 * @param destination
	 *            where to end
	 * @param minute
	 *            in which minute to start: allowed parameters: from 0 to 60
	 * @param hour
	 *            in which hour to start: allowed parameters: from 0 to 24
	 * @return data structure with the best route and the transport type
	 */
	@POST
	@Path("best")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public BestTransportResult getBestTransportInTime(@QueryParam("origin") @DefaultValue("") String origin,
			@QueryParam("destination") @DefaultValue("") String destination, @QueryParam("minute") int minute,
			@QueryParam("hour") int hour) {
		if (!checkOriginDestination(origin, destination)) {
			throw new WebApplicationException("Missing origin and/or destination input.", Status.CONFLICT);
		}
		BestTransportResult bestTransport = this.logic.getBestTransportInTime(origin, destination,
				formatTimes(minute, hour));
		if (bestTransport != null) {
			return bestTransport;
		}
		throw new WebApplicationException("No best transport time found.", Status.NOT_FOUND);
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
		return (origin != null && origin != "" && destination != null && destination != "");
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
		return (travelMode != null);
	}

	/**
	 * checks if minute and hour input is correct
	 * 
	 * @param minute
	 *            from 0 to 60
	 * @param hour
	 *            from 0 to 24
	 * @return true if mode input is correct
	 */
	private DateTime formatTimes(int minute, int hour) {
		if ((hour >= 0 && hour < 24) && (minute >= 0 && minute < 60)) {
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			return new DateTime(calendar.getTime());
		}
		throw new WebApplicationException("Enter correct times.", Status.CONFLICT);
	}

}
