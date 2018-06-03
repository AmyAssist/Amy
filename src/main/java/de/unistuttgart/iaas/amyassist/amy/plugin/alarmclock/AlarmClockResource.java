/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
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
import javax.ws.rs.core.Response.StatusType;

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
		return this.logic.getAlarm();
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
	 * @param alarmTime the timestamp for the alarm
	 * @return HTTP Response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("newalarm")
	public Response setAlarm(Timestamp alarmTime) {
		Response r;
		if(alarmTime.isValid()) {
			this.logic.setAlarm(alarmTime.toString());
			r = Response.ok().build();
		} else {
			r = Response.status(Status.BAD_REQUEST).build();
		}
		return r;
	}
	
	@DELETE
	public void deleteAlarm(String specificAlarm) {
		this.logic.deleteAlarm(specificAlarm);
	}
	
	
}
