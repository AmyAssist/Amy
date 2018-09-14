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

package de.unistuttgart.iaas.amyassist.amy.plugin.timer;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;

/**
 * REST Resource for timer
 * 
 * @author Patrick Gebhardt
 */
@Path(TimerResource.PATH)
public class TimerResource implements Resource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "timer";

	@Reference
	private TimerLogic logic;

	@Context
	private UriInfo uri;

	/**
	 * returns all timers
	 * 
	 * @return all timers
	 */
	@GET
	@Path("timers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Timer> getAllTimers() {
		List<Timer> timerList = this.logic.getAllTimers();
		for (Timer t : timerList) {
			t.setLink(createTimerPath(t.getId()));
		}
		return this.logic.getAllTimers();
	}

	/**
	 * returns a specific timer
	 * 
	 * @param timerNumber
	 *            the requested timer
	 * @return the specific timer
	 */
	@GET
	@Path("timers/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Timer getTimer(@PathParam("pathid") int timerNumber) {
		Timer timer;
		try {
			timer = this.logic.getTimer(timerNumber);
		} catch (NoSuchElementException e) {
			throw new WebApplicationException("there is no timer" + timerNumber, e, Status.NOT_FOUND);
		}
		timer.setLink(createTimerPath(timerNumber));
		return timer;
	}

	/**
	 * sets a timer to a given timestamp
	 * 
	 * @param timer
	 *            the timestamp for the timer
	 * @return the newly created timer
	 */
	@POST
	@Path("timers/new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Timer newTimer(Timer timer) {
		timer.setLink(createTimerPath(timer.getId()));
		return this.logic.setTimer(timer.getTimerTime());
	}

	/**
	 * deletes all timers
	 */
	@POST
	@Path("timers/deleteAll")
	public void deleteAllTimers() {
		this.logic.deleteAllTimers();
	}

	/**
	 * @param timerNumber
	 *            number of the timer which should be deleted
	 * @return null
	 */
	@POST
	@Path("timers/delete/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Timer deleteTimer(@PathParam("pathid") int timerNumber) {
		this.logic.deleteTimer(timerNumber);
		return null;
	}

	/**
	 * @param timerNumber
	 *            number of the timer which should be activated or deactivated
	 * @param timerInc
	 *            the incoming timer
	 * @return null
	 */
	@POST
	@Path("timers/de.activate/{pathid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Timer activatedeactivateAlarm(@PathParam("pathid") int timerNumber, Timer timerInc) {
		Timer timer = timerInc;
		if (!timer.isActive()) {
			this.logic.reactivateTimer(timer);
		} else if (timer.isActive()) {
			this.logic.pauseTimer(timer);
		}
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		return null;
	}

	private URI createTimerPath(int id) {
		return this.uri.getBaseUriBuilder().path(TimerResource.class).path(TimerResource.class, "getTimer")
				.build(Integer.valueOf(id));
	}

}
