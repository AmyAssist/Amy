/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Init;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * TODO: Description
 *
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@Service(AlarmClockSpeech.class)
@SpeechCommand({ "Alarm", "Alarm clock" })
public class AlarmClockSpeech {

	@Reference
	private AlarmClockLogic logic;

	/**
	 * Sets new alarm at for now 10 o' clock
	 *
	 * @return true if everything went well
	 */
	@Grammar("set alarm")
	private boolean setAlarm() {
		return this.logic.setAlarm("10");
	}

	@Grammar("delete")
	private boolean deleteAlarm() {
		return this.logic.deleteAlarm("1");
	}

	@Grammar("reset all")
	private boolean resetAlarms() {
		return this.logic.resetAlarms();
	}

	@Grammar("get")
	private String getAlarm() {
		return this.logic.getAlarm();
	}

	@Grammar("get all")
	private String[] getAllAlarms() {
		return this.logic.getAllAlarms();
	}

	@Grammar("edit")
	private boolean editAlarm() {
		return this.logic.editAlarm("1", "10");
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
