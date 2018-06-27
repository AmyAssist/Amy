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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * This class controls the alarm sound file, which is used for the alarm clock
 * 
 * @author Patrick Gebhardt
 */
@Service
public class AlarmBeepService {

	@Reference
	private Logger logger;

	private Clip clip;

	private boolean isBeeping = false;

	private static final File ALARMSOUND = new File("resources/alarmsound.wav");

	private List<Alarm> alarmList = new ArrayList<>();

	private List<Timer> timerList = new ArrayList<>();

	@PostConstruct
	private void init() {
		try {
			this.clip = AudioSystem.getClip();
			this.clip.open(AudioSystem.getAudioInputStream(ALARMSOUND));
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			this.logger.error("Cant play alarm sound", e);
		}
	}

	/**
	 * @param alarm
	 *            alarm from the alarm class
	 */
	public void beep(Alarm alarm) {
		this.alarmList.add(alarm);
		startBeeping();
	}

	/**
	 * @param timer
	 *            timer from the timer class
	 */
	public void beep(Timer timer) {
		this.timerList.add(timer);
		startBeeping();
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
	public void stopBeeping() {
		if (this.isBeeping) {
			this.clip.stop();
			this.isBeeping = false;
		}
	}

}
