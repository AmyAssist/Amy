/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * REST Resource for the system time
 * 
 * @author Muhammed Kaya
 */
@Path("systemtime")
public class SystemTimeResource {
	
	@Reference
	private SystemTimeLogic logic;
	
	/**
	 * get the current system time
	 * 
	 * @return current time (hour minute second) in a string
	 */
	@GET
	@Path("time")
	public String getTime() {
		return this.logic.getTime();
	}
	
	/**
	 * get the current system date
	 * 
	 * @return current date (day month year) in a string
	 */
	@GET
	@Path("date")
	public String getDate() {
		return this.logic.getDate();
	}
	

}
