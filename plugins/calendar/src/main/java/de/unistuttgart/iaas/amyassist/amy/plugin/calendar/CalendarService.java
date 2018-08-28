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

import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * This class authorizes Amy to connect to the google calendar, this is a modified version from
 * https://developers.google.com/calendar/quickstart/java
 *
 * @author Florian Bauer
 */
@Service
public class CalendarService {
	private static final String APPLICATION_NAME = "Amy Google Calendar API";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

	@Reference
	private Properties configuration;
	@Reference
	private Environment environment;
	@Reference
	private Logger logger;

	private Calendar service;

	private String primary = "primary";
	private String orderBy = "startTime";

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param xHTTPTRANSPORT
	 *            The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException
	 *             If there is no client_secret.
	 */
	private Credential getCredentials(final NetHttpTransport xHTTPTRANSPORT) throws IOException {
		// Load client secrets
		String clientSecretIn = this.configuration.getProperty("JSON");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(clientSecretIn));
		// Build flow and trigger user authorization request
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(xHTTPTRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(this.environment.getWorkingDirectory()
								.resolve("temp/calendarauth").toAbsolutePath().toFile()))
						.setAccessType("offline").build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	/**
	 * This method is to authorize the Google account with the calendar
	 */
	@PostConstruct
	public void authorize() {
		try {
			// Build a new authorized API client service.
			final NetHttpTransport yHTTPTRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			this.service = new Calendar.Builder(yHTTPTRANSPORT, JSON_FACTORY, getCredentials(yHTTPTRANSPORT))
					.setApplicationName(APPLICATION_NAME).build();
		} catch (IOException | GeneralSecurityException e) {
			this.logger.error("Sorry, an error occured during the authorization", e);
		}
	}

	/**
	 * @param min
	 *            the time at which the calendar starts to check for events
	 * @param number
	 *            the number of events it should return
	 * @return the next events after the min time
	 */
	public Events getEvents(DateTime min, int number) {
		Events events = new Events();
		try {
			events = this.service.events().list(this.primary).setMaxResults(number).setTimeMin(min)
					.setOrderBy(this.orderBy).setSingleEvents(true).execute();
		} catch (IOException e) {
			this.logger.error("getEvents() failed with an IOException for DataTime: " + min.toString(), e);
		}
		return events;
	}

	/**
	 * @param min
	 *            the time at which the calendar starts to check for events
	 * @param max
	 *            the time at which the calendar stops looking for events
	 * @return the events between min and max
	 */
	public Events getEvents(DateTime min, DateTime max) {
		Events events = new Events();
		try {
			events = this.service.events().list(this.primary).setTimeMin(min).setTimeMax(max).setOrderBy(this.orderBy)
					.setSingleEvents(true).execute();
		} catch (IOException e) {
			this.logger.error("getEvents() failed with an IOException for DateTime min: " + min.toString()
					+ " and max: " + max.toString(), e);
		}
		return events;
	}

	/**
	 * This method adds an event to the calendar
	 *
	 * @param calId
	 *            the ID of the calendar the event should be added to
	 * @param event
	 *            the event that should be added
	 */
	public void addEvent(String calId, Event event) {
		try {
			this.service.events().insert(calId, event).execute();
		} catch (IOException e) {
			this.logger.error("addEvent() failed with an IOException for callId: " + calId
					+ " and event: " + event.getSummary(), e);
		}
	}

}
