/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import java.util.Calendar;

import de.unistuttgart.iaas.amyassist.amy.core.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.SpeechCommand;

/**
 * A plugin which tells time and date
 * 
 * @author Florian Bauer
 */

@SpeechCommand({ "what is", "tell me" })
public class SystemTime {
	private Calendar cal = Calendar.getInstance();

	@Grammar("the time")
	public void time() {
		Calendar.getInstance().
	}

}
