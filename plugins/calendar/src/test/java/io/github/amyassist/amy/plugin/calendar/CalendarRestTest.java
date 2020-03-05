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

package io.github.amyassist.amy.plugin.calendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static io.github.amyassist.amy.test.matcher.rest.ResponseMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

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
	 * {@link io.github.amyassist.amy.plugin.calendar.CalendarResource#setEvent(CalendarEvent)}.
	 */
	@Disabled("TODO must be fixed")
	@Test
	void testSetEvents() {
		Entity<CalendarEvent> entity = Entity.entity(this.setEvent, MediaType.APPLICATION_JSON);
		try (Response response = this.target.path("events/set").request().post(entity)) {
			assertThat(response, status(204));
			Mockito.verify(this.logic).setEvent(this.setEvent);
		}

		try (Response response = this.target.path("events/set").request().post(null)) {
			String actualMsg = response.readEntity(String.class);
			assertThat(actualMsg, equalTo("Enter valid event information"));
			assertThat(response, status(409));
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.calendar.CalendarResource#getEventsAt(LocalDateTime)}.
	 */
	@Test
	void testGetEventsAt() {
		Mockito.when(this.logic.getEventsAt(this.ldt)).thenReturn(this.eventList);
		try (Response response = this.target.path("eventsAt/2015-05-28T08:00:00").request().get()) {
			assertThat(response, status(200));
		}
		
        Mockito.when(this.logic.getEventsAt(this.ldt)).thenReturn(null);
        try (Response response = this.target.path("eventsAt/2015-05-28T08:00:00").request().get()) {
            String actualMsg = response.readEntity(String.class);
            assertThat(actualMsg, equalTo("Couldn't get events"));
            assertEquals(404, response.getStatus());
        }

        Mockito.verify(this.logic, Mockito.times(2)).getEventsAt(this.ldt);

	}

}
