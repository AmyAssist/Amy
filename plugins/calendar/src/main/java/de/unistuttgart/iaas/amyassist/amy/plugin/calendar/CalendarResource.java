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

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.google.api.services.calendar.model.Event;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * REST Resource for calendar
 * 
 * @author Muhammed Kaya
 */
@Path(CalendarResource.PATH)
public class CalendarResource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "calendar";

	@Reference
	private CalendarLogic logic;

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
	@POST
	@Path("events/{number}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String getEvents(@PathParam("number") int number) {
		String events = this.logic.getEvents(number);
		checkEvents(events);
		return events;
	}

	/**
	 * This method contains the logic to show the calendar events today
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
	 * This method contains the logic to show the calendar events tomorrow
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
	 * This method contains the logic to show the calendar events on a specific date as a list of Events
	 *
	 * @param ldt
	 *            LocalDateTime variable
	 * @return the events of the chosen day as a List<Event>
	 */
	@POST
	@Path("eventsAtString/{ldt}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String getEventsAtAsString(@PathParam("ldt") LocalDateTime ldt) {
		String events = this.logic.getEventsAtAsString(ldt);
		if (events.contains("There are no events on the ")) {
			throw new WebApplicationException(events, Status.NOT_FOUND);
		}
		return events;
	}

	/**
	 * This method contains the logic to show the calendar events on a specific date as natural language output
	 *
	 * @param ldt
	 *            LocalDateTime variable
	 * @return the events of the chosen day
	 */
	@POST
	@Path("eventsAt/{ldt}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getEventsAt(@PathParam("ldt") LocalDateTime ldt) {
		List<Event> events = this.logic.getEventsAt(ldt);
		if (!events.isEmpty()) {
			return events;
		}
		throw new WebApplicationException(this.errorLogger, Status.CONFLICT);
	}

	/**
	 * This method checks if and how an event is to be displayed in the output.
	 *
	 * @param dayToCheck
	 *            the day from which we want to know how the current event belongs to it
	 * @param event
	 *            the current chosen event
	 * @param withDate
	 *            if the date should be displayed (or only the time)
	 * @return the event as natural language text
	 */
	@POST
	@Path("checkDay/{dayToCheck}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String checkDay(@PathParam("dayToCheck") LocalDateTime dayToCheck, Event event,
			@QueryParam("withDate") @DefaultValue("true") boolean withDate) {
		return this.logic.checkDay(dayToCheck, event, withDate);
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

}
