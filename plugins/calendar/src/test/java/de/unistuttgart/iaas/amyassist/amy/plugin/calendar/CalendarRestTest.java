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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test for the rest resource of calendar
 *
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
class CalendarRestTest {

	@Reference
	private TestFramework testFramework;

	private CalendarLogic logic;

	private WebTarget target;

	private static final String NO_EVENTS_FOUND = "No upcoming events found.";
	private static final String NO_EVENTS_TODAY = "There are no events today.";
	private static final String NO_EVENTS_TOMORROW = "There are no events tomorrow.";
	private static final String NO_EVENTS_AT_DATE = "There are no events on the 2015-05-28T08:00:00.";
	private static final String EVENT_UPCOMING = "You have following upcoming " + 1 + " events: Meeting with Amy.";
	private static final String EVENT_TODAY = "You have following events today: Meeting with Amy.";
	private static final String EVENT_TOMORROW = "You have following events tomorrow: Meeting with Amy.";
	private static final String EVENT_AT_DATE = "You have following events on the 2015-05-28T08:00:00: Meeting with Amy.";

	private LocalDateTime ldt = LocalDateTime.parse("2015-05-28T08:00:00");
	private CalendarEvent event;
	private CalendarEvent setEvent;
	private List<CalendarEvent> eventList;

	/**
	 * setUp
	 */
	@BeforeEach
	public void setUp() {
		this.target = this.testFramework.setRESTResource(CalendarResource.class);
		this.logic = this.testFramework.mockService(CalendarLogic.class);

		createEvent();
	}

	/**
	 * inits all needed inputs
	 */
	private void createEvent() {
		this.event = new CalendarEvent("1", LocalDateTime.parse("2015-05-28T08:00:00"),
				LocalDateTime.parse("2015-05-28T10:00:00"), "summary", "work", "meeting", false);
		this.eventList = new ArrayList<>();
		this.eventList.add(this.event);

		this.setEvent = new CalendarEvent(LocalDateTime.parse("2015-05-28T09:00:00"),
				LocalDateTime.parse("2015-05-28T11:00:00"), "summary", "home", "homework", "type", 0, "", false);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarResource#setEvent(CalendarEvent)}.
	 */
	@Test
	void testSetEvents() {
		Entity<CalendarEvent> entity = Entity.entity(this.setEvent, MediaType.APPLICATION_JSON);
		try (Response response = this.target.path("events/set").request().post(entity)) {
			assertThat(response.getStatus(), is(204));
			Mockito.verify(this.logic).setEvent(this.setEvent);
		}

		try (Response response = this.target.path("events/set").request().post(null)) {
			String actualMsg = response.readEntity(String.class);
			assertThat(actualMsg, is("Enter valid event information"));
			assertThat(response.getStatus(), is(409));
		}
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarResource#getEvents(int)}.
	 */
	@Test
	void testGetEvents() {
		Mockito.when(this.logic.getEvents(1)).thenReturn(EVENT_UPCOMING);
		Mockito.when(this.logic.getEvents(2)).thenReturn(NO_EVENTS_FOUND);
		try (Response response = this.target.path("events/1").request().get();
				Response response2 = this.target.path("events/2").request().get()) {

			String actual = response.readEntity(String.class);
			assertThat(actual, is(EVENT_UPCOMING));
			assertThat(response.getStatus(), is(200));
			Mockito.verify(this.logic).getEvents(1);

			actual = response2.readEntity(String.class);
			assertThat(actual, is(NO_EVENTS_FOUND));
			assertThat(response2.getStatus(), is(404));
			Mockito.verify(this.logic).getEvents(2);
		}
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarResource#getEventsToday()}.
	 */
	@Test
	void testGetEventsToday() {
		Mockito.when(this.logic.getEventsToday()).thenReturn(EVENT_TODAY);
		try (Response response = this.target.path("events/today").request().get()) {
			String actual = response.readEntity(String.class);
			assertThat(actual, is(EVENT_TODAY));
			assertThat(response.getStatus(), is(200));
		}

		Mockito.when(this.logic.getEventsToday()).thenReturn(NO_EVENTS_TODAY);
		try (Response response = this.target.path("events/today").request().get()) {
			String actual = response.readEntity(String.class);
			assertThat(actual, is(NO_EVENTS_TODAY));
			assertThat(response.getStatus(), is(404));
		}

		Mockito.verify(this.logic, Mockito.times(2)).getEventsToday();
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarResource#getEventsTomorrow()}.
	 */
	@Test
	void testGetEventsTomorrow() {
		Mockito.when(this.logic.getEventsTomorrow()).thenReturn(EVENT_TOMORROW);
		try (Response response = this.target.path("events/tomorrow").request().get()) {
			String actual = response.readEntity(String.class);
			assertThat(actual, is(EVENT_TOMORROW));
			assertThat(response.getStatus(), is(200));
		}

		Mockito.when(this.logic.getEventsTomorrow()).thenReturn(NO_EVENTS_TOMORROW);
		try (Response response = this.target.path("events/tomorrow").request().get()) {
			String actual = response.readEntity(String.class);
			assertThat(actual, is(NO_EVENTS_TOMORROW));
			assertThat(response.getStatus(), is(404));
		}

		Mockito.verify(this.logic, Mockito.times(2)).getEventsTomorrow();
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarResource#getEventsAtAsString(java.time.LocalDateTime)}.
	 */
	@Test
	void testGetEventsAtAsString() {
		Mockito.when(this.logic.getEventsAtAsString(this.ldt)).thenReturn(EVENT_AT_DATE);
		try (Response response = this.target.path("eventsAtString/2015-05-28T08:00:00").request().get()) {
			String actual = response.readEntity(String.class);
			assertThat(actual, is(EVENT_AT_DATE));
			assertThat(response.getStatus(), is(200));
		}

		Mockito.when(this.logic.getEventsAtAsString(this.ldt)).thenReturn(NO_EVENTS_AT_DATE);
		try (Response response = this.target.path("eventsAtString/2015-05-28T08:00:00").request().get()) {
			String actual = response.readEntity(String.class);
			assertThat(actual, is(NO_EVENTS_AT_DATE));
			assertThat(response.getStatus(), is(404));
		}

		Mockito.verify(this.logic, Mockito.times(2)).getEventsAtAsString(this.ldt);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.calendar.CalendarResource#getEventsAt(LocalDateTime)}.
	 */
	@Test
	void testGetEventsAt() {
		Mockito.when(this.logic.getEventsAt(this.ldt)).thenReturn(this.eventList);
		try (Response response = this.target.path("eventsAt/2015-05-28T08:00:00").request().get()) {
			assertThat(response.getStatus(), is(200));
			List<CalendarEvent> actualEvents = response.readEntity(List.class);
			// assertThat(actualEvents, is(this.eventList));
		}

		Mockito.when(this.logic.getEventsAt(this.ldt)).thenReturn(new ArrayList<>());
		try (Response response = this.target.path("eventsAt/2015-05-28T08:00:00").request().get()) {
			String actualMsg = response.readEntity(String.class);
			assertThat(actualMsg, is(NO_EVENTS_FOUND));
			assertThat(response.getStatus(), is(409));
		}

		Mockito.verify(this.logic, Mockito.times(2)).getEventsAt(this.ldt);
	}

}
