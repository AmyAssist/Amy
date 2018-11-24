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

package io.github.amyassist.amy.plugin.alarmclock;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.utility.rest.Resource;
import io.github.amyassist.amy.utility.rest.ResourceEntity;

/**
 * REST Resource for alarmclock
 * 
 * @author Christian Bräuner, Patrick Gebhardt
 */
@Path(AlarmClockResource.PATH)
public class AlarmClockResource implements Resource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "clock";

	@Reference
	private AlarmClockLogic logic;
	
	@Reference
	private Environment environment;

	@Context
	private UriInfo uri;

	/**
	 * returns all alarms
	 * 
	 * @return all alarms
	 */
	@GET
	@Path("alarms")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Alarm> getAllAlarms() {

		List<Alarm> alarmlist = this.logic.getAllAlarms();
		for (Alarm a : alarmlist) {
			a.setLink(createAlarmPath(a.getId()));
		}

		return this.logic.getAllAlarms();
	}

	/**
	 * returns a specific alarm
	 * 
	 * @param alarmnumber
	 *            the requested alarm
	 * @return the specific alarm
	 */
	@GET
	@Path("alarms/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Alarm getAlarm(@PathParam("pathid") int alarmnumber) {
		Alarm alarm;
		try {
			alarm = this.logic.getAlarm(alarmnumber);
		} catch (NoSuchElementException e) {
			throw new WebApplicationException("there is no alarm" + alarmnumber, e, Status.NOT_FOUND);
		}
		alarm.setLink(createAlarmPath(alarmnumber));
		return alarm;
	}

	/**
	 * changes the properties of an alarm
	 * 
	 * @param alarmNumber
	 *            the number of the alarm
	 * @param alarmInc
	 *            the incoming alarm
	 * @return the new alarm or null
	 */
	@POST
	@Path("alarms/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Alarm editAlarm(@PathParam("pathid") int alarmNumber, Alarm alarmInc) {
		Alarm alarm = alarmInc;
		int day;
		if (this.environment.getCurrentLocalDateTime().getDayOfMonth() == alarm.getAlarmTime().getDayOfMonth()) {
			day = -1;
		} else {
			day = 1;
		}
		this.logic.editAlarm(alarmNumber, day, alarm.getAlarmTime().getHour(), alarm.getAlarmTime().getMinute());
		return alarm;
	}

	/**
	 * @param alarmNumber
	 *            number of the alarm which should be activated or deactivated
	 * @param alarmInc
	 *            the incoming alarm
	 * @return null
	 */
	@POST
	@Path("alarms/de.activate/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Alarm activatedeactivateAlarm(@PathParam("pathid") int alarmNumber, Alarm alarmInc) {
		Alarm alarm = alarmInc;
		if (!alarm.isActive()) {
			this.logic.activateAlarm(alarmNumber);
		} else if (alarm.isActive()) {
			this.logic.deactivateAlarm(alarmNumber);
		}
		return null;
	}

	/**
	 * @param alarmNumber
	 *            number of the alarm which should be deleted
	 * @return null
	 */
	@POST
	@Path("alarms/delete/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Alarm deleteAlarm(@PathParam("pathid") int alarmNumber) {
		this.logic.deleteAlarm(alarmNumber);
		return null;
	}

	/**
	 * sets a alarm to a given timestamp
	 * 
	 * @param alarm
	 *            the timestamp for the alarm
	 * @return the newly created alarm
	 */
	@POST
	@Path("alarms/new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Alarm newAlarm(Alarm alarm) {
		int day;
		if (this.environment.getCurrentLocalDateTime().getDayOfMonth() == alarm.getAlarmTime().getDayOfMonth()) {
			day = -1;
		} else {
			day = 1;
		}
		alarm.setLink(createAlarmPath(alarm.getId()));
		return this.logic.setAlarm(day, alarm.getAlarmTime().getHour(), alarm.getAlarmTime().getMinute());
	}

	/**
	 * deletes all alarms
	 */
	@POST
	@Path("alarms/reset")
	public void resetAlarms() {
		this.logic.resetAlarms();
	}

	/**
	 * @see io.github.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		return null;
	}

	private URI createAlarmPath(int id) {
		return this.uri.getBaseUriBuilder().path(AlarmClockResource.class).path(AlarmClockResource.class, "getAlarm")
				.build(Integer.valueOf(id));
	}

}
