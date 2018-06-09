/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest.Timestamp;

/**
 * REST Resource for alarmclock
 * 
 * @author Christian Bräuner
 */
@Path("clock")
public class AlarmClockResource {

	@Reference
	private AlarmClockLogic logic;

	/**
	 * returns all alarms
	 * 
	 * @return all alarms
	 */
	@GET
	@Path("alarms")
	@Produces(MediaType.APPLICATION_JSON)
	public Timestamp[] getAllAlarms() {
		String[] alarms = this.logic.getAllAlarms();
		Timestamp[] timestamps = new Timestamp[alarms.length];
		for(int i = 0; i < alarms.length; i++) {
			if(alarms[i] != null && alarms[i] != "") {
				String[] split = alarms[i].split(";");
				timestamps[i] = new Timestamp(split[0]);
			}
		}
		return timestamps;
	}

	/**
	 * returns a specific alarm
	 * 	
	 * @param alarmnumber the requested alarm
	 * @return the specific alarm
	 */
	@GET
	@Path("alarms/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Timestamp getAlarm(@PathParam("pathid") int alarmnumber) {
		String alarm = this.logic.getAlarm(alarmnumber);
		if(alarm == null) {
			throw new WebApplicationException("there is no alarm" + alarmnumber, Status.NOT_FOUND);
		}
		String[] split = alarm.split(";");
		Timestamp ts = new Timestamp(split[0]);
		return ts;	
	}

	@POST
	@Path("alarms/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Timestamp editAlarm(@PathParam("pathid") int alarmnumber, Timestamp alarmTime) {
		if (!alarmTime.isValid()) {
			throw new WebApplicationException("The given time wasn't a valid time", Status.BAD_REQUEST);
		}
		if (!this.logic.editAlarm(alarmnumber, new String[] { "" + alarmTime.hour, "" + alarmTime.minute }).equals("alarm not found")) {
			return alarmTime;
		}
		throw new WebApplicationException("there is no alarm" + alarmnumber, Status.NOT_FOUND);
	
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
	public Timestamp setAlarm(Timestamp alarmTime) {
		if (alarmTime.isValid()) {
			this.logic.setAlarm(new String[] { "" + alarmTime.hour, "" + alarmTime.minute });
			return alarmTime;
		}
		throw new WebApplicationException("The given time wasn't a valid time", Status.BAD_REQUEST);
	}

	/**
	 * deletes an alarm
	 * 
	 * @param alarmNumber the alarm to delete
	 */
	@DELETE
	@Path("alarms/{pathid}")
	public void deleteAlarm(@PathParam("pathid") int alarmNumber) {
		this.logic.deleteAlarm(alarmNumber);
	}

}
