/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import de.unistuttgart.iaas.amyassist.amy.core.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.Init;
import de.unistuttgart.iaas.amyassist.amy.core.SpeechCommand;

/**
 * TODO: Description
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */

@SpeechCommand({ "Alarm", "Alarm clock" })
public class AlarmClockSpeech {

	private AlarmClockLogic logic;

	/**
	 * Sets new alarm at for now 10 o' clock
	 * 
	 * @return true if everything went well
	 */
	@Grammar("set alarm")
	private boolean setAlarm() {
		return this.logic.setAlarm();
	}

	@Grammar("reset all")
	private boolean resetAlarms() {
		return this.logic.resetAlarms();
	}

	/**
	 * Init method
	 * 
	 * @param core
	 *            The core
	 */
	@Init
	public void init(ICore core) {
		this.logic = new AlarmClockLogic();
		this.logic.init(core);
	}

}
