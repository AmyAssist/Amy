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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
	 * gets a alarm
	 * 
	 * @return alarm1 or null if there is no alarm1
	 */
	@GET
	@Path("alarms/1")
	public String getAlarm() {
		return this.logic.getAlarm(1);
	}

	/**
	 * returns all alrams
	 * 
	 * @return all alarms
	 */
	@GET
	@Path("alarms")
	public String[] getAllAlarms() {
		return this.logic.getAllAlarms();
	}

	/**
	 * sets a alarm to a given timestamp
	 * 
	 * @param alarmTime
	 *            the timestamp for the alarm
	 * @return HTTP Response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("newalarm")
	public Response setAlarm(Timestamp alarmTime) {
		Response r;
		if (alarmTime.isValid()) {
			this.logic.setAlarm(new String[] { "" + alarmTime.hour, "" + alarmTime.minute });
			r = Response.ok().build();
		} else {
			r = Response.status(Status.BAD_REQUEST).build();
		}
		return r;
	}

	/**
	 * @param alarmNumber
	 */
	@DELETE
	public void deleteAlarm(int alarmNumber) {
		this.logic.deleteAlarm(alarmNumber);
	}

}
