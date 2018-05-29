/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * The REST access point for the alarmclock
 * 
 * @author Christian Bräuner
 */
@Path("alarmclock")
public class AlarmclockResource {

	@GET
	public String getTime() {
		return "time";
	}

}
