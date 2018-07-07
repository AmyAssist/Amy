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

import org.slf4j.Logger;

import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Timestamp;

import java.util.Calendar;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.IllegalFieldValueException;
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

	private Logger logger;

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
	 * @param timestamp
	 *            time object which sets the departure time, can be null
	 * @return a data structure with the best route and the transport type of the query
	 */
	@POST
	@Path("fromTo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public BestTransportResult routeFromTo(@QueryParam("origin") @DefaultValue("") String origin,
			@QueryParam("destination") @DefaultValue("") String destination,
			@QueryParam("travelMode") String travelMode, Timestamp timestamp) {
		checkOriginDestination(origin, destination);
		checkTravelMode(travelMode);
		TravelMode mode = this.logic.getTravelMode(travelMode);
		BestTransportResult bestRoute;
		if (timestamp == null) {
			bestRoute = this.logic.fromTo(origin, destination, mode);
		} else {
			bestRoute = this.logic.fromToWithDeparture(origin, destination, mode, timestampToDateTime(timestamp));
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
	 * @param timestamp
	 *            time you plan to arrive at the destination
	 * @return a Timestamp object with the latest start time
	 */
	@POST
	@Path("when")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Timestamp whenIHaveToGo(@QueryParam("origin") @DefaultValue("") String origin,
			@QueryParam("destination") @DefaultValue("") String destination,
			@QueryParam("travelMode") String travelMode, Timestamp timestamp) {
		checkOriginDestination(origin, destination);
		checkTravelMode(travelMode);
		TravelMode mode = this.logic.getTravelMode(travelMode);
		ReadableInstant rtime = this.logic.whenIHaveToGo(origin, destination, mode, timestampToDateTime(timestamp));
		if (rtime != null) {
			return new Timestamp(rtime.get(DateTimeFieldType.year()), rtime.get(DateTimeFieldType.monthOfYear()),
					rtime.get(DateTimeFieldType.dayOfMonth()), rtime.get(DateTimeFieldType.hourOfDay()),
					rtime.get(DateTimeFieldType.minuteOfHour()), rtime.get(DateTimeFieldType.secondOfMinute()));
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
	 * @param timestamp
	 *            time object which sets the departure time
	 * @return data structure with the best route and the transport type
	 */
	@POST
	@Path("best")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public BestTransportResult getBestTransportInTime(@QueryParam("origin") @DefaultValue("") String origin,
			@QueryParam("destination") @DefaultValue("") String destination, Timestamp timestamp) {
		checkOriginDestination(origin, destination);
		BestTransportResult bestTransport = this.logic.getBestTransportInTime(origin, destination,
				timestampToDateTime(timestamp));
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
	 * converts Timestamp to DateTime, missing date values will set automatically to today, missing time values will set
	 * to 0
	 * 
	 * @param timestamp
	 *            checks if the timestamp parameter is correct if yes then it will be converted to a DateTime object
	 * @return a DateTime object with values of the timestamp parameter
	 */
	private DateTime timestampToDateTime(Timestamp timestamp) {
		Calendar.getInstance(TimeZone.getDefault());
		if (timestamp.getYear() == 0) {
			timestamp.setYear(Calendar.getInstance().get(Calendar.YEAR));
		}
		if (timestamp.getMonth() == 0) {
			timestamp.setMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
		}
		if (timestamp.getDay() == 0) {
			timestamp.setDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		}
		try {
			return new DateTime(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay(), timestamp.getHour(),
					timestamp.getMinute(), timestamp.getSecond());
		} catch (IllegalFieldValueException e) {
			this.logger.error("Input values are not in range.", e);
			throw new WebApplicationException("Enter correct times.", Status.CONFLICT);
		}
	}

}
