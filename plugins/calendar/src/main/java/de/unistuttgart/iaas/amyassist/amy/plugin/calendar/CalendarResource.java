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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

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
	 * This method creates an event for the connected google calendar
	 *
	 * @param calendarEvent
	 *            the event which will be created in the google calendar
	 */
	@POST
	@Path("events/set")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setEvent(CalendarEvent calendarEvent) {
		if (calendarEvent != null) {
			this.logic.setEvent(calendarEvent);
		} else {
			throw new WebApplicationException("Enter valid event information", Status.CONFLICT);
		}
	}

	/**
	 * This method contains the logic to show the calendar events on a specific date as natural language output
	 *
	 * @param ldt
	 *            LocalDateTime variable
	 * @return the events of the chosen day
	 */
	@GET
	@Path("eventsAt/{ldt}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public List<CalendarEvent> getEventsAt(@PathParam("ldt") LocalDateTime ldt) {
		return this.logic.getEventsAt(ldt);
	}

}
