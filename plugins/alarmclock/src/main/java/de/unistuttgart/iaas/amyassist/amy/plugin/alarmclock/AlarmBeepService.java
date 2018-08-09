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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.Sound;
import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.SoundFactory;
import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.SoundPlayer;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PreDestroy;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * This class controls the alarm sound file, which is used for the alarm clock
 * 
 * @author Patrick Gebhardt, Tim Neumann
 */
@Service
public class AlarmBeepService {

	private static final String ALARMSOUND = "alarmsound.wav";

	@Reference
	private Logger logger;
	@Reference
	private Environment env;

	@Reference
	private SoundFactory sf;

	@Reference
	private AudioManager am;

	private Sound beepSound;
	private SoundPlayer beepPlayer;

	private Set<Integer> alarmList = new HashSet<>();

	private Set<Integer> timerList = new HashSet<>();

	@PostConstruct
	private void init() {
		InputStream resolve = this.getClass().getResourceAsStream(ALARMSOUND);
		try (InputStream bufferedIn = new BufferedInputStream(resolve);
				AudioInputStream soundIn = AudioSystem.getAudioInputStream(bufferedIn);) {
			this.beepSound = this.sf.loadSound(soundIn);
		} catch (IOException | UnsupportedAudioFileException e) {
			this.logger.error("Cant load alarm sound", e);
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
		if (this.beepPlayer == null || !this.beepPlayer.isRunning()) {
			this.beepPlayer = this.beepSound.getInfiniteLoopPlayer();
			if (this.am.hasLocalAudioEnvironment()) {
				this.am.playAudio(this.am.getLocalAudioEnvironmentIdentifier(), this.beepPlayer.getAudioStream(),
						AudioManager.OutputBehavior.SUSPEND);
			}
			this.beepPlayer.start();
		}
	}

	/**
	 * Stops the audio file, when the timer / alarm is deactivated and sets the beeping variable on false
	 */
	private void stopBeeping() {
		if (this.beepPlayer != null && this.beepPlayer.isRunning()) {
			this.beepPlayer.stop();
		}
	}

	@PreDestroy
	private void preDestroy() {
		this.stopBeeping();
	}

}
