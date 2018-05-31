/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Init;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * A plugin which tells time and date
 * 
 * @author Florian Bauer, Patrick Gebhardt
 */
@Service
@SpeechCommand({ "what is", "tell me" })
public class SystemTimeSpeech {

	@Reference
	private SystemTimeLogic logic;

	/**
	 * A method which returns the current time
	 * 
	 * @return current time (hour minute second) in a string
	 */
	@Grammar("the time")
	public String time() {
		return this.logic.getTime();
	}

	/**
	 * A method which returns the current date
	 * 
	 * @return current date (day month year) in a string
	 */
	@Grammar("the date")
	public String date() {
		return this.logic.getDate();
	}

	/**
	 * Init method
	 */
	@Init
	public void init() {
		this.logic = new SystemTimeLogic();
	}

}
