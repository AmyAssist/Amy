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

package io.github.amyassist.amy.plugin.alarmclock;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.audio.AudioManager;
import io.github.amyassist.amy.core.audio.LocalAudio;
import io.github.amyassist.amy.core.audio.sound.Sound;
import io.github.amyassist.amy.core.audio.sound.SoundFactory;
import io.github.amyassist.amy.core.audio.sound.SoundPlayer;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.PreDestroy;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.messagehub.MessageHub;

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

	@Reference
	private LocalAudio la;

	@Reference
	private MessageHub messageHub;

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
	 * @return returns the list of alarms
	 */
	public Set<Integer> beep(Alarm alarm) {
		this.alarmList.add(alarm.getId());
		this.update();
		return this.alarmList;
	}

	/**
	 * @param alarm
	 *            alarm from the alarm class
	 * @return returns the list of alarms
	 */
	public Set<Integer> stopBeep(Alarm alarm) {
		this.alarmList.remove(alarm.getId());
		this.update();
		return this.alarmList;
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
			if (this.la.isLocalAudioAvailable()) {
				this.am.playAudio(this.la.getLocalAudioEnvironmentIdentifier(), this.beepPlayer.getAudioStream(),
						AudioManager.OutputBehavior.SUSPEND);
			}
			this.beepPlayer.start();
		}
	}

	/**
	 * Stops the audio file, when the timer / alarm is deactivated and sets the beeping variable on false
	 */
	void stopBeeping() {
		if (this.beepPlayer != null && this.beepPlayer.isRunning()) {
			this.beepPlayer.stop();
		}
	}

	@PreDestroy
	private void preDestroy() {
		this.stopBeeping();
	}
}
