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

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest.Timestamp;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;

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

		return alarm;
	}

	/**
	 * changes the properties of an alarm
	 * 
	 * @param alarmNumber
	 *            the number of the alarm
	 * @param mode
	 *            what to do: allowed paramters: edit, activate, delete, deactivate
	 * @param alarmTime
	 *            the new time
	 * @return the new alarmtime or null
	 */
	@POST
	@Path("alarms/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Timestamp editAlarm(@PathParam("pathid") int alarmNumber,
			@QueryParam("mode") @DefaultValue("edit") String mode, Timestamp alarmTime) {
		switch (mode) {
		case "edit":
			if (alarmTime == null || !alarmTime.isValid()) {
				throw new WebApplicationException("The given time wasn't a valid time", Status.BAD_REQUEST);
			}
			Alarm alarm;
			try {
				alarm = this.logic.editAlarm(alarmNumber, alarmTime.getHour(), alarmTime.getMinute());
			} catch (NoSuchElementException e) {
				throw new WebApplicationException("there is no alarm" + alarmNumber, e, Status.NOT_FOUND);
			}
			Timestamp ts = new Timestamp(alarm);
			ts.setLink(createAlarmPath(alarm.getId()));
			return ts;
		case "activate":
			this.logic.activateAlarm(alarmNumber);
			break;
		case "delete":
			this.logic.deleteAlarm(alarmNumber);
			break;
		case "deactivate":
			this.logic.deactivateAlarm(alarmNumber);
			break;
		default:
			throw new WebApplicationException(500);
		}
		return null;
	}

	/**
	 * sets a alarm to a given timestamp
	 * 
	 * @param alarmTime
	 *            the timestamp for the alarm
	 * @return the newly created alarm
	 */
	@POST
	@Path("alarms/new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Timestamp newAlarm(Timestamp alarmTime) {
		if (alarmTime.isValid()) {
			Alarm result = this.logic.setAlarm(alarmTime.getHour(), alarmTime.getMinute());
			alarmTime.setLink(createAlarmPath(result.getId()));
			return alarmTime;
		}
		throw new WebApplicationException("The given time wasn't a valid time", Status.BAD_REQUEST);
	}

	/**
	 * deletes all alarms
	 */
	@POST
	@Path("alarms/reset")
	public void resetAlarms() {
		this.logic.resetAlarms();
	}

	private URI createAlarmPath(int id) {
		return this.uri.getBaseUriBuilder().path(AlarmClockResource.class).path(AlarmClockResource.class, "getAlarm")
				.build(Integer.valueOf(id));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		return null;
	}
}
