/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * TODO: Description
 *
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
public class AlarmClockLogic {

	private IStorage storage;
	private static final String KEY = "alarmCounter";

	/**
	 * Reads out the chosen alarm
	 *
	 * @param alarm
	 * @return
	 */
	protected String alarmOutput(String alarm) {

		return null;
	}

	/**
	 * Plays the alarm sound
	 */
	protected void playAlarm() {
		try {
			AudioClip clip = Applet.newAudioClip(new URL("src/alarmsound.wav"));
			clip.play();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set new alarm.
	 *
	 * @param alarmTime
	 *
	 * @return true, if everything went well
	 */
	protected boolean setAlarm(String alarmTime) {
		int counter = Integer.parseInt(this.storage.get(KEY));
		counter++;
		this.storage.put(KEY, Integer.toString(counter));
		this.storage.put("alarm" + Integer.toString(counter), alarmTime);
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
			String key = "alarm" + i;
			if (this.storage.has(key))
				this.storage.delete(key);
		}
		return true;
	}

	/**
	 * Delete one alarm
	 *
	 * @param specificAlarm
	 *
	 * @return true if everything went well
	 */
	protected boolean deleteAlarm(String specificAlarm) {
		if (this.storage.has(specificAlarm)) {
			this.storage.delete(specificAlarm);
			return true;
		}
		return false;
	}

	/**
	 * Read out one alarm
	 *
	 * @return true if everything went well
	 */
	protected String getAlarm() {
		String specificAlarm = Integer.toString(1);
		if (this.storage.has(specificAlarm)) {
			alarmOutput(this.storage.get("alarm" + specificAlarm));
			return alarmOutput(this.storage.get("alarm" + specificAlarm));
		}
		return null;
	}

	/**
	 * Read out all alarms
	 *
	 * @return true if everything went well
	 */
	protected String[] getAllAlarms() {
		String[] allAlarms = {};
		for (int i = 1; i <= Integer.parseInt(this.storage.get(KEY)); i++) {
			if (this.storage.has("alarm" + i)) {
				alarmOutput(this.storage.get("alarm" + i));
				allAlarms[i] = "alarm" + i;
			}
		}
		return allAlarms;
	}

	/**
	 * Edit a specific Alarm
	 *
	 * @param specificAlarm
	 * @return true if everything went well
	 */
	protected boolean editAlarm(String specificAlarm, String alarmTime) {
		if (this.storage.has(specificAlarm)) {
			deleteAlarm(specificAlarm);
			setAlarm(alarmTime);
			return true;
		}
		return false;
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
