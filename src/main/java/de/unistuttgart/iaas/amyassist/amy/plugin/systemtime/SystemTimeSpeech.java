/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

	private DateFormat dateFormat = new SimpleDateFormat("dd MM yy");
	private DateFormat timeFormat = new SimpleDateFormat("HH mm");

	/**
	 * A method which returns the current time
	 * 
	 * @return
	 */
	@Grammar("the time")
	public String time() {
		Calendar cal = Calendar.getInstance();
		String timeOut = this.timeFormat.format(cal.getTime()).toString();
		return timeOut;
	}

	/**
	 * A method which returns the current date
	 * 
	 * @return
	 */
	@Grammar("the date")
	public String date() {
		Calendar cal = Calendar.getInstance();
		String dateOut = this.dateFormat.format(cal.getTime()).toString();
		return dateOut;
	}

	/**
	 * Init method
	 */
	@Init
	public void init() {
		this.logic = new SystemTimeLogic();
	}

}
