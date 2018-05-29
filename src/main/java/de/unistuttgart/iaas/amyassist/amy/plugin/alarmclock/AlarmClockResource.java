/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * REST Resource for alarmclock
 * 
 * @author Christian Bräuner
 */
@Path("alarmclock")
public class AlarmClockResource {
	
	@Reference
	private AlarmClockLogic logic;

	@GET
	public String getAlarm() {
		return this.logic.getAlarm();
	}
	
//	@POST
//	public void setAlarm(String alarmTime) {
//		this.logic.setAlarm(alarmTime);
//	}
//	
//	@DELETE
//	public void deleteAlarm(String specificAlarm) {
//		this.logic.deleteAlarm(specificAlarm);
//	}
	
	
}
