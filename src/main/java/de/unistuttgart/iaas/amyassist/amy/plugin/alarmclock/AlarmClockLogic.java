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
import java.util.Calendar;
import java.util.Date;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;

/**
 * TODO: Description
 *
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@Service
public class AlarmClockLogic {

	@Reference
	private IStorage storage;

	@Reference
	private TaskSchedulerAPI taskScheduler;

	private static final String ALARMCOUNTER = "alarmCounter";
	private static final String TIMERCOUNTER = "timerCounter";

	/**
	 * Reads out the chosen alarm per Text-to-speech
	 *
	 * @param alarm
	 * @return
	 */
	protected String alarmOutput(String alarm) {

		return null;
	}

	/**
	 * Stops alarm or timer, that's currently going off
	 * 
	 * @return
	 */
	protected boolean stopAlarm() {
		// TODO: stop alarm with TaskScheduler (maybe with help of distinct
		// alarm id)
		return true;
	}

	/**
	 * Creates a Runnable that plays the alarm sound License: Attribution 3.0
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel
	 * Simion
	 * 
	 * @return
	 *
	 */
	private Runnable createAlarmRunnable() {
		return () -> {
			try {
				// not sure if this URL is working
				AudioClip clip = Applet
						.newAudioClip(new URL("src/alarmsound.wav"));
				clip.play();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		};
	}

	/**
	 * Set new alarm and schedule it
	 *
	 * @param alarmTime
	 *
	 * @return true, if everything went well
	 */
	public boolean setAlarm(String alarmTime) {
		int time = Integer.parseInt(alarmTime);
		int counter = Integer.parseInt(this.storage.get(ALARMCOUNTER));
		counter++;
		this.storage.put(ALARMCOUNTER, Integer.toString(counter));
		this.storage.put("alarm" + counter, alarmTime);

		Runnable alarmRunnable = createAlarmRunnable();
		Calendar current = Calendar.getInstance();
		Calendar alarmCalendar = Calendar.getInstance();
		alarmCalendar.set(Calendar.HOUR_OF_DAY, time);
		if (alarmCalendar.before(current)) {
			alarmCalendar.add(Calendar.DATE, 1);
		}

		this.taskScheduler.schedule(alarmRunnable, alarmCalendar.getTime());
		return true;
	}

	/**
	 * 
	 * @param delay
	 *            delay until alarm in milliseconds
	 * @return
	 */
	protected boolean setTimer(long delay) {
		int counter = Integer.parseInt(this.storage.get(TIMERCOUNTER));
		counter++;
		this.storage.put(TIMERCOUNTER, Integer.toString(counter));
		this.storage.put("timer" + counter, "" + delay);

		Runnable alarmRunnable = createAlarmRunnable();
		Calendar alarmTime = Calendar.getInstance();
		alarmTime.add(Calendar.MILLISECOND, (int) delay);
		this.taskScheduler.schedule(alarmRunnable, alarmTime.getTime());
		return true;
	}

	/**
	 * Delete all alarms and reset alarmCounter
	 *
	 * @return true if everything went well
	 */
	protected boolean resetAlarms() {
		int amount = Integer.parseInt(this.storage.get(ALARMCOUNTER));
		this.storage.put(ALARMCOUNTER, "0");
		for (int i = 1; i <= amount; i++) {
			String key = "alarm" + i;
			if (this.storage.has(key))
				this.storage.delete(key);
		}
		return true;
	}

	/**
	 * Delete all timers and reset timerCounter
	 * 
	 * @return true if everything went well
	 */
	protected boolean resetTimers() {
		int amount = Integer.parseInt(this.storage.get(TIMERCOUNTER));
		this.storage.put(TIMERCOUNTER, "0");
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
		if (this.storage.has("alarm" + specificAlarm)) {
			alarmOutput(this.storage.get("alarm" + specificAlarm));
			return this.storage.get("alarm" + specificAlarm);
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
		for (int i = 1; i <= Integer
				.parseInt(this.storage.get(ALARMCOUNTER)); i++) {
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
	 * @param alarmTime
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
		if (!this.storage.has(ALARMCOUNTER))
			this.storage.put(ALARMCOUNTER, "0");
		if (!this.storage.has(TIMERCOUNTER))
			this.storage.put(TIMERCOUNTER, "0");
	}

}
