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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
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

	private static final File ALARMSOUND = new File("src/main/resources/alarmsound.wav");

	/**
	 * Reads out the given alarm per Text-to-speech. e.g.: "This alarm rings
	 * tomorrow/today at 15:15"
	 * 
	 * @param alarmParams
	 *
	 * @return
	 */
	protected static String alarmOutput(String[] alarmParams) {
		if (alarmParams[2].equals("true"))
			return "This alarm will ring at " + alarmParams[0] + ":" + alarmParams[1] + ".";

		return "This alarm is set for " + alarmParams[0] + ":" + alarmParams[1] + " but will not ring.";
	}

	/**
	 * Reads out the given delay per Text-to-speech. e.g.: "This timer rings in 5
	 * minutes"
	 * 
	 * @param timer
	 * @return
	 */
	protected static String timerOutput(String timer) {
		return "This timer was set on " + timer.split(";")[1] + " minutes";
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
					while (this.storage.get("alarm" + alarmNumber).split(";")[2].equals("true")) {
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
					while (this.storage.get("timer" + timerNumber).split(";")[1].equals("true")) {
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
	 *            String array with two integers. First entry is hour second is
	 *            minute
	 *
	 * @return true, if everything went well
	 */
	protected String setAlarm(String[] alarmTime) {
		int counter = Integer.parseInt(this.storage.get(ALARMCOUNTER));
		counter++;
		this.storage.put(ALARMCOUNTER, Integer.toString(counter));
		this.storage.put("alarm" + counter, alarmTime[0] + ";" + alarmTime[1] + ";" + "true");

		Runnable alarmRunnable = createAlarmRunnable(counter);
		Calendar current = Calendar.getInstance();
		Calendar alarmCalendar = Calendar.getInstance();
		alarmCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmTime[0]));
		alarmCalendar.set(Calendar.MINUTE, Integer.parseInt(alarmTime[0]));
		if (alarmCalendar.before(current)) {
			alarmCalendar.add(Calendar.DATE, 1);
		}

		this.taskScheduler.schedule(alarmRunnable, alarmCalendar.getTime());
		return "Alarm " + ALARMCOUNTER + " set for " + alarmTime[0] + ":" + alarmTime[1];
	}

	/**
	 * Sets new timer and schedules it
	 * 
	 * @param delay
	 *            delay until alarm in milliseconds
	 * @return true if everything went well
	 */
	protected String setAlarm(int delay) {
		int counter = Integer.parseInt(this.storage.get(TIMERCOUNTER));
		counter++;
		this.storage.put(TIMERCOUNTER, Integer.toString(counter));
		this.storage.put("timer" + counter, "" + delay + ";" + "true");
		Runnable timerRunnable = createTimerRunnable(counter);
		Calendar alarmTime = Calendar.getInstance();
		alarmTime.add(Calendar.MILLISECOND, delay * 60000);
		this.taskScheduler.schedule(timerRunnable, alarmTime.getTime());
		return "Timer " + TIMERCOUNTER + " set on " + delay + " minutes";
	}

	/**
	 * Delete all alarms and reset alarmCounter
	 *
	 * @return true if everything went well
	 */
	protected String resetAlarms() {
		int amount = Integer.parseInt(this.storage.get(ALARMCOUNTER));
		this.storage.put(ALARMCOUNTER, "0");
		for (int i = 1; i <= amount; i++) {
			String key = "alarm" + i;
			if (this.storage.has(key))
				this.storage.delete(key);
		}
		return "Alarms reset";
	}

	/**
	 * Delete all timers and reset timerCounter
	 * 
	 * @return true if everything went well
	 */
	protected String resetTimers() {
		int amount = Integer.parseInt(this.storage.get(TIMERCOUNTER));
		this.storage.put(TIMERCOUNTER, "0");
		for (int i = 1; i <= amount; i++) {
			String key = "timer" + i;
			if (this.storage.has(key))
				this.storage.delete(key);
		}
		return "Timers reset";
	}

	/**
	 * Delete one alarm
	 * 
	 * @param alarmNumber
	 *            alarmNumber in the storage
	 * @return true if everything went well
	 */
	protected String deleteAlarm(int alarmNumber) {
		if (this.storage.has("alarm" + alarmNumber)) {
			this.storage.delete("alarm" + alarmNumber);
			return "Alarm number " + alarmNumber + " deleted";
		}
		return "alarm number " + alarmNumber + " not found";
	}

	/**
	 * Deactivates specific alarm so it will not go off
	 * 
	 * @param specificAlarm
	 *            alarm name
	 * @return true or false, depending on if there were complications
	 */
	protected String deactivateAlarm(String specificAlarm) {
		if (this.storage.has(specificAlarm)) {
			String alarm = this.storage.get(specificAlarm);
			String[] params = alarm.split(";");
			try {
				this.storage.put(specificAlarm, params[0] + ";" + params[1] + ";" + "false");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Something went wrong!");
				return "something went wrong";
			}
			return "alarm " + specificAlarm + " deactivated";
		}
		return "alarm " + specificAlarm + " not found";
	}

	/**
	 * Read out one alarm
	 * 
	 * @param alarmNumber
	 *            number of the alarm in the storage
	 *
	 * @return true if everything went well
	 */
	protected String getAlarm(int alarmNumber) {
		if (this.storage.has("alarm" + alarmNumber)) {
			alarmOutput(this.storage.get("alarm" + alarmNumber).split(";"));
			return this.storage.get("alarm" + alarmNumber);
		}
		return "alarm not found!";
	}

	/**
	 * @param timerNumber
	 *            number of the timer in storage
	 * @return
	 */
	protected String getTimer(int timerNumber) {
		if (this.storage.has("timer" + timerNumber)) {
			timerOutput(this.storage.get("timer" + timerNumber));
			return this.storage.get("timer" + timerNumber);
		}
		return "timer not found!";
	}

	/**
	 * Read out all alarms
	 *
	 * @return true if everything went well
	 */
	protected String[] getAllAlarms() {
		String[] allAlarms = new String[Integer.parseInt(this.storage.get(ALARMCOUNTER))];
		for (int i = 1; i <= Integer.parseInt(this.storage.get(ALARMCOUNTER)); i++) {
			if (this.storage.has("alarm" + i)) {
				alarmOutput(this.storage.get("alarm" + i).split(";"));
				allAlarms[i] = this.storage.get("alarm" + i);
			}
		}
		return allAlarms;
	}

	/**
	 * Edit a specific Alarm
	 *
	 * @param alarmNumber
	 *            name of the alarm
	 * @param alarmTime
	 *            new alarm time
	 * @return true if everything went well
	 */
	protected String editAlarm(int alarmNumber, String[] alarmTime) {
		if (this.storage.has("alarm" + alarmNumber)) {
			deleteAlarm(alarmNumber);
			setAlarm(alarmTime);
			return "alarm " + alarmNumber + " changed to ";
		}
		return "alarm not found";
	}

	/**
	 *
	 * Initialization method for logic class. TODO call die method from DI
	 */
	@PostConstruct
	public void init() {
		if (!this.storage.has(ALARMCOUNTER))
			this.storage.put(ALARMCOUNTER, "0");
		if (!this.storage.has(TIMERCOUNTER))
			this.storage.put(TIMERCOUNTER, "0");
	}

}
