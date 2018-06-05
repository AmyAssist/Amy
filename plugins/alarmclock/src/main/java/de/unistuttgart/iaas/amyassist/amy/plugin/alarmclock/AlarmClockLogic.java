/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.io.File;
import java.util.Calendar;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;

/**
 * This class implements the logic for all the functions that our alarm clock
 * and timer are capable of
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

	private static final File ALARMSOUND = new File(
			"src/main/resources/alarmsound.wav");

	/**
	 * Reads out the given alarm per Text-to-speech
	 *
	 * @param alarm
	 *            the alarm to output
	 */
	protected void alarmOutput(String alarm) {

	}

	/**
	 * Creates a Runnable that plays the alarm sound. License: Attribution 3.0
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel
	 * Simion
	 * 
	 * @return runnable
	 *
	 */
	private Runnable createAlarmRunnable(int alarmNumber) {
		return () -> {
			if (this.storage.has("alarm" + alarmNumber)) {
				try {
					while (this.storage.get("alarm" + alarmNumber).split(";")[2]
							.equals("true")) {
						Clip clip = AudioSystem.getClip();
						clip.open(AudioSystem.getAudioInputStream(ALARMSOUND));
						clip.start();
						Thread.sleep(clip.getMicrosecondLength() / 1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * Creates a Runnable that plays the alarm sound. License: Attribution 3.0
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel
	 * Simion
	 * 
	 * @param timerNumber
	 *            the number of the timer in storage
	 * @return runnable
	 */
	@SuppressWarnings("resource")
	private Runnable createTimerRunnable(int timerNumber) {
		return () -> {
			if (this.storage.has("timer" + timerNumber)) {
				try {
					while (this.storage.get("timer" + timerNumber).split(";")[2]
							.equals("true")) {
						Clip clip = AudioSystem.getClip();
						clip.open(AudioSystem.getAudioInputStream(ALARMSOUND));
						clip.start();
						Thread.sleep(clip.getMicrosecondLength() / 1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

	}

	/**
	 * Set new alarm and schedule it
	 *
	 * @param alarmTime
	 *            the time of alarm (e.g. 12:15)
	 *
	 * @return true, if everything went well
	 */
	public boolean setAlarm(String alarmTime) {
		int hour = Integer.parseInt(alarmTime.substring(0, 2));
		int minute = Integer.parseInt(alarmTime.substring(3, 5));
		int counter = Integer.parseInt(this.storage.get(ALARMCOUNTER));
		counter++;
		this.storage.put(ALARMCOUNTER, Integer.toString(counter));
		this.storage.put("alarm" + counter, alarmTime + ";" + ""
				+ System.currentTimeMillis() + ";" + "true");

		Runnable alarmRunnable = createAlarmRunnable(counter);
		Calendar current = Calendar.getInstance();
		Calendar alarmCalendar = Calendar.getInstance();
		alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
		alarmCalendar.set(Calendar.MINUTE, minute);
		if (alarmCalendar.before(current)) {
			alarmCalendar.add(Calendar.DATE, 1);
		}

		this.taskScheduler.schedule(alarmRunnable, alarmCalendar.getTime());
		return true;
	}

	/**
	 * Sets new timer and schedules it
	 * 
	 * @param delay
	 *            delay until alarm in milliseconds
	 * @return true if everything went well
	 */
	protected boolean setAlarm(long delay) {
		int counter = Integer.parseInt(this.storage.get(TIMERCOUNTER));
		counter++;
		this.storage.put(TIMERCOUNTER, Integer.toString(counter));
		this.storage.put("timer" + counter, "" + delay + ";" + ""
				+ System.currentTimeMillis() + ";" + "true");
		Runnable timerRunnable = createTimerRunnable(counter);
		Calendar alarmTime = Calendar.getInstance();
		alarmTime.add(Calendar.MILLISECOND, (int) delay);
		this.taskScheduler.schedule(timerRunnable, alarmTime.getTime());
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
			String key = "timer" + i;
			if (this.storage.has(key))
				this.storage.delete(key);
		}
		return true;
	}

	/**
	 * Delete one alarm
	 *
	 * @param specificAlarm
	 *            the alarm name
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
	 * Deactivates specific alarm so it will not go off
	 * 
	 * @param specificAlarm
	 *            alarm name
	 * @return true or false, depending on if there were complications
	 */
	protected boolean deactivateAlarm(String specificAlarm) {
		if (this.storage.has(specificAlarm)) {
			String alarm = this.storage.get(specificAlarm);
			String[] params = alarm.split(";");
			try {
				this.storage.put(specificAlarm,
						params[0] + ";" + params[1] + ";" + "false");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Something went wrong!");
				return false;
			}
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
	 *            name of the alarm
	 * @param alarmTime
	 *            new alarm time
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
	 * Init method for logic class. TODO call die method from DI
	 *
	 * @param core
	 *            The core
	 */
	public void init() {
		if (!this.storage.has(ALARMCOUNTER))
			this.storage.put(ALARMCOUNTER, "0");
		if (!this.storage.has(TIMERCOUNTER))
			this.storage.put(TIMERCOUNTER, "0");
	}

}
