/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
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
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PreDestroy;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * This class controls the alarm sound file, which is used for the alarm clock
 * 
 * @author Patrick Gebhardt
 */
@Service
public class AlarmBeepService {

	private static final String ALARMSOUND = "plugins/alarmclock/res/alarmsound.wav";

	@Reference
	private Logger logger;
	@Reference
	private Environment env;

	private Clip clip;

	private boolean isBeeping = false;

	private Set<Integer> alarmList = new HashSet<>();

	private Set<Integer> timerList = new HashSet<>();

	@PostConstruct
	private void init() {
		Path resolve = this.env.getWorkingDirectory().resolve(ALARMSOUND);
		try {
			this.clip = AudioSystem.getClip();
			this.clip.open(AudioSystem.getAudioInputStream(resolve.toFile()));
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			this.logger.error("Cant play alarm sound", e);
		}
	}

	/**
	 * @param alarm
	 *            alarm from the alarm class
	 */
	public void beep(Alarm alarm) {
		this.alarmList.add(alarm.getId());
		this.update();
	}

	/**
	 * @param timer
	 *            timer from the timer class
	 */
	public void beep(Timer timer) {
		this.timerList.add(timer.getId());
		this.update();
	}

	/**
	 * @param alarm
	 *            alarm from the alarm class
	 */
	public void stopBeep(Alarm alarm) {
		this.alarmList.remove(alarm.getId());
		this.update();
	}

	/**
	 * @param timer
	 *            timer from the timer class
	 */
	public void stopBeep(Timer timer) {
		this.timerList.remove(timer.getId());
		this.update();
	}

	private void update() {
		if (!this.alarmList.isEmpty() || !this.timerList.isEmpty()) {
			this.startBeeping();
		} else {
			this.stopBeeping();
		}
	}

	/**
	 * Starts the audio file, when the timer / alarm should ring and sets the beeping variable on true
	 */
	private void startBeeping() {
		if (!this.isBeeping) {
			this.isBeeping = true;
			this.clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	/**
	 * Stops the audio file, when the timer / alarm is deactivated and sets the beeping variable on false
	 */
	private void stopBeeping() {
		if (this.isBeeping) {
			this.clip.stop();
			this.clip.setFramePosition(0);
			this.isBeeping = false;
		}
	}

	@PreDestroy
	private void preDestroy() {
		this.clip.close();
	}

}
