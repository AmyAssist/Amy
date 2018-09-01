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

package de.unistuttgart.iaas.amyassist.amy.core.speech.output;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.LocalAudio;
import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.Sound;
import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.SoundFactory;
import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.SoundPlayer;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;
import de.unistuttgart.iaas.amyassist.amy.core.speech.tts.TextToSpeech;

/**
 * This class outputs
 * 
 * @author Tim Neumann, Kai Menzel
 */
@Service(Output.class)
public class OutputImpl implements Output {

	@Reference
	private Logger logger;
	@Reference
	private TextToSpeech tts;
	@Reference
	private AudioManager am;
	@Reference
	private LocalAudio la;
	@Reference
	private SoundFactory sf;

	private Map<Sounds, Sound> soundData = new EnumMap<>(Sounds.class);

	private Queue<SoundPlayer> players = new ConcurrentLinkedQueue<>();

	@PostConstruct
	private void init() {
		for (Sounds s : Sounds.values()) {
			try {
				this.soundData.put(s, this.sf.loadSound(s.getUrl()));
			} catch (UnsupportedAudioFileException | IOException e) {
				throw new IllegalStateException("Error loading sounds.", e);
			}
		}
	}

	private void playAudio(SoundPlayer player, Runnable callback) {
		if (this.la.isLocalAudioAvailable()) {

			this.am.playAudio(this.la.getLocalAudioEnvironmentIdentifier(), player.getAudioStream(),
					AudioManager.OutputBehavior.QUEUE);
			synchronized (this.players) {
				this.players.add(player);
			}
			player.setOnStopHook(r -> {
				synchronized (this.players) {
					this.players.remove(player);
				}
				if (callback != null) {
					callback.run();
				}
			});
			player.start();
		}
	}

	@Override
	public void voiceOutput(String s) {
		voiceOutput(s, null);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output#voiceOutput(java.lang.String,
	 *      java.lang.Runnable)
	 */
	@Override
	public void voiceOutput(String s, Runnable callback) {
		this.logger.info("saying: {}", s);
		SoundPlayer player;
		try {
			player = this.sf.loadSound(this.tts.getMaryAudio(s)).getSinglePlayer();
		} catch (IOException e) {
			throw new IllegalStateException("IO error while reading from mary stream", e);
		}
		playAudio(player, callback);

	}

	@Override
	public void soundOutput(Sounds sound) {
		soundOutput(sound, null);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output#soundOutput(de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds,
	 *      java.lang.Runnable)
	 */
	@Override
	public void soundOutput(Sounds sound, Runnable callback) {
		playAudio(this.soundData.get(sound).getSinglePlayer(), callback);
	}

	@Override
	public void stopOutput() {
		synchronized (this.players) {
			for (SoundPlayer player : this.players) {
				player.stop();
			}
		}
	}

	@Override
	public boolean isCurrentlyOutputting() {
		boolean anyPlaying = false;
		synchronized (this.players) {
			for (SoundPlayer player : this.players) {
				if (player.isRunning()) {
					anyPlaying = true;
					break;
				}
			}
		}
		return anyPlaying;
	}
}
