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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
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
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Method;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Parameter;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Types;

/**
 * REST Resource for alarmclock
 * 
 * @author Christian Bräuner
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
	private UriInfo info;

	/**
	 * returns all alarms
	 * 
	 * @return all alarms
	 */
	@GET
	@Path("alarms")
	@Produces(MediaType.APPLICATION_JSON)
	public Timestamp[] getAllAlarms() {
		List<Alarm> alarms = this.logic.getAllAlarms();
		Timestamp[] timestamps = new Timestamp[alarms.size()];
		for (int i = 0; i < alarms.size(); i++) {
			if (alarms.get(i) != null) {
				timestamps[i] = new Timestamp(alarms.get(i));
				timestamps[i].setLink(createAlarmPath(alarms.get(i).getId()));
			}
		}
		return timestamps;
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
	public Timestamp getAlarm(@PathParam("pathid") int alarmnumber) {
		Alarm alarm;
		try {
			alarm = this.logic.getAlarm(alarmnumber);
		} catch (NoSuchElementException e) {
			throw new WebApplicationException("there is no alarm" + alarmnumber, e, Status.NOT_FOUND);
		}

		Timestamp ts = new Timestamp(alarm);
		ts.setLink(createAlarmPath(alarmnumber));
		ts.setMethods(createSingleAlarmMethods(alarmnumber));
		return ts;
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
	 * sets an alarm to a given timestamp
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
		return this.info.getBaseUriBuilder().path(AlarmClockResource.class).path(AlarmClockResource.class, "getAlarm")
				.build(Integer.valueOf(id));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceEntity getPluginDescripion() {
		ResourceEntity resource = new ResourceEntity();
		resource.setName("AlarmClock Plugin");
		resource.setDescription("A Plugin which provides an alarm and timer functionality. "
				+ "This plugin makes it possible to set, delete, activate, deactivate and edit alarms and timers.");
		resource.setMethods(this.getPluginMethods());
		resource.setLink(this.info.getBaseUriBuilder().path(AlarmClockResource.class).build());
		return resource;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginMethods()
	 */
	@Override
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method[] getPluginMethods() {
		Method[] methods = new Method[3];
		methods[0] = createGetAllAlarmsMethod();
		methods[1] = createNewAlarmMethod();
		methods[2] = createResetAlarmsMethod();
		return methods;
	}

	/**
	 * returns the method describing the getAllAlarms method
	 * 
	 * @return the describing method object
	 */
	@Path("alarms")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetAllAlarmsMethod() {
		Method getAll = new Method();
		getAll.setName("Get all Alarms");
		getAll.setDescription("Returns all alarms");
		getAll.setLink(this.info.getBaseUriBuilder().path(AlarmClockResource.class)
				.path(AlarmClockResource.class, "getAllAlarms").build());
		getAll.setType(Types.GET);
		return getAll;
	}

	/**
	 * returns the method describing the methods of a single alarm instance
	 * 
	 * @param pathid id of the single alaram
	 * 
	 * @return the describing method object
	 */
	@Path("alarms/{pathid}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method[] createSingleAlarmMethods(@PathParam("pathid") int pathid) {
		final String edit = "editAlarm";
		final String mode = "mode";
		Method[] methods = new Method[4];
		Method getAlarm = new Method();
		getAlarm.setName("Get Alarm");
		getAlarm.setDescription("Returns a specific alarm");
		getAlarm.setLink(this.info.getBaseUriBuilder().path(AlarmClockResource.class)
				.path(AlarmClockResource.class, "getAlarm").build(pathid));
		getAlarm.setType(Types.GET);
		getAlarm.setParameters(getGetAlarmParameters());
		Method editAlarm = new Method();
		editAlarm.setName("Edit Alarm");
		editAlarm.setDescription("Changes the properties of an alarm");
		editAlarm.setLink(this.info.getBaseUriBuilder().path(AlarmClockResource.class)
				.path(AlarmClockResource.class, edit).queryParam(mode, "edit").build(pathid));
		editAlarm.setType(Types.POST);
		editAlarm.setParameters(getEditAlarmParameters());
		Method deleteAlarm = new Method();
		deleteAlarm.setName("Delete Alarm");
		deleteAlarm.setDescription("Deletes an alarm");
		deleteAlarm.setLink(this.info.getBaseUriBuilder().path(AlarmClockResource.class)
				.path(AlarmClockResource.class, edit).queryParam(mode, "delete").build(pathid));
		deleteAlarm.setType(Types.POST);
		deleteAlarm.setParameters(new Parameter[] {getPathIdAsParameter()});
		Method acitvateAlarm = new Method();
		acitvateAlarm.setName("Activate Alarm");
		acitvateAlarm.setDescription("Activates an alarm");
		acitvateAlarm.setLink(this.info.getBaseUriBuilder().path(AlarmClockResource.class)
				.path(AlarmClockResource.class, edit).queryParam(mode, "activate").build(pathid));
		acitvateAlarm.setType(Types.POST);
		acitvateAlarm.setParameters(new Parameter[] {getPathIdAsParameter()});
		Method deacitvateAlarm = new Method();
		deacitvateAlarm.setName("Deactivate Alarm");
		deacitvateAlarm.setDescription("Deactivates an alarm");
		deacitvateAlarm.setLink(this.info.getBaseUriBuilder().path(AlarmClockResource.class)
				.path(AlarmClockResource.class, edit).queryParam(mode, "deactivate").build(pathid));
		deacitvateAlarm.setType(Types.POST);
		deacitvateAlarm.setParameters(new Parameter[] {getPathIdAsParameter()});
		methods[0] = getAlarm;
		methods[1] = editAlarm;
		methods[2] = deleteAlarm;
		methods[3] = this.logic.getAlarm(pathid).isActive() ? deacitvateAlarm : acitvateAlarm;
		return methods;
	}

	/**
	 * returns the method describing the newAlarm method
	 * 
	 * @return the describing method object
	 */
	@Path("alarms/new")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createNewAlarmMethod() {
		Method newAlarm = new Method();
		newAlarm.setName("New Alarm");
		newAlarm.setDescription("Sets an alarm to a given timestamp");
		newAlarm.setLink(this.info.getBaseUriBuilder().path(AlarmClockResource.class)
				.path(AlarmClockResource.class, "newAlarm").build());
		newAlarm.setType(Types.POST);
		newAlarm.setParameters(getTimestampAsParameter());
		return newAlarm;
	}

	/**
	 * returns the method describing the resetAlarms method
	 * 
	 * @return the describing method object
	 */
	@Path("alarms/reset")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createResetAlarmsMethod() {
		Method resetAlarms = new Method();
		resetAlarms.setName("Delete all Alarms");
		resetAlarms.setDescription("Deletes all alarms");
		resetAlarms.setLink(this.info.getBaseUriBuilder().path(AlarmClockResource.class)
				.path(AlarmClockResource.class, "resetAlarms").build());
		resetAlarms.setType(Types.POST);
		return resetAlarms;
	}

	private Parameter[] getGetAlarmParameters() {
		Parameter[] params = new Parameter[1];
		// pathid
		params[0] = new Parameter();
		params[0].setName("PathID");
		params[0].setRequired(true);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.INTEGER);
		return params;
	}

	private Parameter[] getEditAlarmParameters() {		
		List<Parameter> parameter = new ArrayList<>();
		parameter.add(getPathIdAsParameter());
		parameter.addAll(Arrays.asList(getTimestampAsParameter()));
		return parameter.toArray(new Parameter[4]);
	}

	
	private Parameter getPathIdAsParameter() {
		Parameter params = new Parameter();
		params.setName("PathID");
		params.setRequired(true);
		params.setParamType(Types.PATH);
		params.setValueType(Types.INTEGER);
		return params;
	}

	private Parameter[] getTimestampAsParameter() {
		Parameter[] params = new Parameter[2];
		// hour
		params[0] = new Parameter();
		params[0].setName("hour");
		params[0].setRequired(true);
		params[0].setParamType(Types.BODY);
		params[0].setValueType(Types.INTEGER);
		params[0].setDescription("Hour of the alarm");
		// minute
		params[1] = new Parameter();
		params[1].setName("minute");
		params[1].setRequired(true);
		params[1].setParamType(Types.BODY);
		params[1].setValueType(Types.INTEGER);
		params[1].setDescription("Minute of the alarm");
		return params;
	}

}
