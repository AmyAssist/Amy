/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import de.unistuttgart.iaas.amyassist.amy.core.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.IStorage;

/**
 * TODO: Description
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
public class AlarmClockLogic {

	private IStorage storage;
	private static final String KEY = "alarmCounter";

	/**
	 * Set new alarm. TODO: Add parameters
	 * 
	 * @return true, if everything went well
	 */
	protected boolean setAlarm() {
		int counter = Integer.parseInt(this.storage.get(KEY));
		counter++;
		this.storage.put(KEY, Integer.toString(counter));
		this.storage.put("alarm" + Integer.toString(counter), "10");
		return true;
	}

	/**
	 * Delete all alarms and reset alarmCounter
	 * 
	 * @return true if everything went well
	 */
	protected boolean resetAlarms() {
		int amount = Integer.parseInt(this.storage.get(KEY));
		this.storage.put(KEY, "0");
		for (int i = 1; i <= amount; i++) {
			String key = "alarm" + Integer.toString(i);
			if (this.storage.has(key))
				this.storage.delete(key);
		}
		return true;
	}

	/**
	 * Init method for logic class.
	 * 
	 * @param core
	 *            The core
	 */
	public void init(ICore core) {
		this.storage = core.getStorage();
		if (!this.storage.has(KEY))
			this.storage.put(KEY, Integer.toString(0));
	}
}
