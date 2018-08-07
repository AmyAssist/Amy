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

package de.unistuttgart.iaas.amyassist.amy.plugin.calendar;

import java.time.LocalDateTime;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Method;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Parameter;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Types;

/**
 * REST Resource for calendar
 * 
 * @author Muhammed Kaya
 */
@Path(CalendarResource.PATH)
public class CalendarResource implements Resource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "calendar";

	@Reference
	private CalendarLogic logic;

	@Context
	private UriInfo info;

	/**
	 * Natural language response of Amy when the event list is empty.
	 */
	String noEventsFound = "No upcoming events found.";

	/**
	 * Natural language response of Amy when the event list of today is empty.
	 */
	private String noEventsToday = "There are no events today.";

	/**
	 * Natural language response of Amy when the event list of tomorrow is empty.
	 */
	private String noEventsTomorrow = "There are no events tomorrow.";

	/**
	 * Output of the logger
	 */
	private String errorLogger = "An error occurred.";

	/**
	 * This method lists the next events from the calendar
	 *
	 * @param number
	 *            number of events the user wants to get
	 * @return event summary
	 */
	@GET
	@Path("events/{number}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getEvents(@PathParam("number") int number) {
		String events = this.logic.getEvents(number);
		checkEvents(events);
		return events;
	}

	/**
	 * This method shows the calendar events today
	 *
	 * @return the events of today
	 */
	@GET
	@Path("events/today")
	@Produces(MediaType.TEXT_PLAIN)
	public String getEventsToday() {
		String events = this.logic.getEventsToday();
		checkEvents(events);
		return events;
	}

	/**
	 * This method shows the calendar events tomorrow
	 *
	 * @return the events of tomorrow
	 */
	@GET
	@Path("events/tomorrow")
	@Produces(MediaType.TEXT_PLAIN)
	public String getEventsTomorrow() {
		String events = this.logic.getEventsTomorrow();
		checkEvents(events);
		return events;
	}

	/**
	 * This method shows the calendar events on a specific date as a list of Events
	 *
	 * @param ldt
	 *            LocalDateTime variable
	 * @return the events of the chosen day as a List<Event>
	 */
	@GET
	@Path("eventsAtString/{ldt}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getEventsAtAsString(@PathParam("ldt") LocalDateTime ldt) {
		String events = this.logic.getEventsAtAsString(ldt);
		if (events.contains("There are no events on the ")) {
			throw new WebApplicationException(events, Status.NOT_FOUND);
		}
		return events;
	}

	/**
	 * This method shows the calendar events on a specific date as a list with event objects
	 *
	 * @param ldt
	 *            LocalDateTime variable
	 * @return the events of the chosen day
	 */
	@GET
	@Path("eventsAt/{ldt}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CalendarEvent> getEventsAt(@PathParam("ldt") LocalDateTime ldt) {
		List<CalendarEvent> events = this.logic.getEventsAt(ldt);
		if (!events.isEmpty()) {
			return events;
		}
		throw new WebApplicationException(this.errorLogger, Status.CONFLICT);
	}

	private boolean checkEvents(String events) {
		switch (events) {
		case "No upcoming events found.":
			throw new WebApplicationException(this.noEventsFound, Status.NOT_FOUND);
		case "There are no events today.":
			throw new WebApplicationException(this.noEventsToday, Status.NOT_FOUND);
		case "There are no events tomorrow.":
			throw new WebApplicationException(this.noEventsTomorrow, Status.NOT_FOUND);
		case "An error occurred.":
			throw new WebApplicationException(this.errorLogger, Status.CONFLICT);
		default:
			return true;
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceEntity getPluginDescripion() {
		ResourceEntity resource = new ResourceEntity();
		resource.setName("Calendar Plugin");
		resource.setDescription("A Plugin to show upcoming events in the Google Calendar");
		resource.setMethods(this.getPluginMethods());
		resource.setLink(this.info.getBaseUriBuilder().path(CalendarResource.class).build());
		return resource;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginMethods()
	 */
	@Override
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method[] getPluginMethods() {
		Method[] methods = new Method[5];
		methods[0] = createGetEventsMethod();
		methods[1] = createGetEventsTodayMethod();
		methods[2] = createGetEventsTomorrowMethod();
		methods[3] = createGetEventsAtAsStringMethod();
		methods[4] = createGetEventsAtMethod();
		return methods;
	}

	/**
	 * returns the method describing the getEvents method
	 * 
	 * @return the describing method object
	 */
	@Path("events/{number}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetEventsMethod() {
		Method events = new Method();
		events.setName("Upcoming Events");
		events.setDescription("Returns the upcoming events");
		events.setLink(this.info.getBaseUriBuilder().path(CalendarResource.class)
				.path(CalendarResource.class, "getEvents").build());
		events.setType(Types.GET);
		events.setParameters(getGetEventsParameters());
		return events;
	}

	/**
	 * returns the method describing the getEventsToday method
	 * 
	 * @return the describing method object
	 */
	@Path("events/today")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetEventsTodayMethod() {
		Method today = new Method();
		today.setName("Events of Today");
		today.setDescription("Returns the upcoming events of today");
		today.setLink(this.info.getBaseUriBuilder().path(CalendarResource.class)
				.path(CalendarResource.class, "getEventsToday").build());
		today.setType(Types.GET);
		return today;
	}

	/**
	 * returns the method describing the getEventsTomorrow method
	 * 
	 * @return the describing method object
	 */
	@Path("events/tomorrow")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetEventsTomorrowMethod() {
		Method tomorrow = new Method();
		tomorrow.setName("Events of Tomorrow");
		tomorrow.setDescription("Returns the upcoming events of tomorrow");
		tomorrow.setLink(this.info.getBaseUriBuilder().path(CalendarResource.class)
				.path(CalendarResource.class, "getEventsTomorrow").build());
		tomorrow.setType(Types.GET);
		return tomorrow;
	}

	/**
	 * returns the method describing the getEventsAtAsString method
	 * 
	 * @return the describing method object
	 */
	@Path("eventsAtString/{ldt}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetEventsAtAsStringMethod() {
		Method atString = new Method();
		atString.setName("Events as String");
		atString.setDescription("Returns events on a specific date as String");
		atString.setLink(this.info.getBaseUriBuilder().path(CalendarResource.class)
				.path(CalendarResource.class, "getEventsAtAsString").build());
		atString.setType(Types.GET);
		atString.setParameters(getGetEventsAtParameters());
		return atString;
	}

	/**
	 * returns the method describing the getEventsAt method
	 * 
	 * @return the describing method object
	 */
	@Path("eventsAt/{ldt}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetEventsAtMethod() {
		Method at = new Method();
		at.setName("Events as List");
		at.setDescription("Returns events on a specific date as List containing events");
		at.setLink(this.info.getBaseUriBuilder().path(CalendarResource.class)
				.path(CalendarResource.class, "getEventsAt").build());
		at.setType(Types.GET);
		at.setParameters(getGetEventsAtParameters());
		return at;
	}

	private Parameter[] getGetEventsParameters() {
		Parameter[] params = new Parameter[1];
		// number
		params[0] = new Parameter();
		params[0].setName("Number");
		params[0].setRequired(false);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.INTEGER);
		return params;
	}

	private Parameter[] getGetEventsAtParameters() {
		Parameter[] params = new Parameter[1];
		// ldt
		params[0] = new Parameter();
		params[0].setName("LocalDateTime");
		params[0].setRequired(true);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.DATE);
		return params;
	}

}
