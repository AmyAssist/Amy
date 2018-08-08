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

package de.unistuttgart.iaas.amyassist.amy.core.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment;
import de.unistuttgart.iaas.amyassist.amy.core.audio.environment.LocalAudioEnvironment;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;

/**
 * The real implementation of the {@link AudioManager} ( by implementing {@link InternalAudioManager} )
 * 
 * @author Tim Neumann
 */
@Service(InternalAudioManager.class)
public class InternalAudioManagerService implements InternalAudioManager, RunnableService {

	private static final String CONFIG_NAME = "audio.config";
	private static final String PROPERTY_LOCAL_AUDIO = "enableLocalAudio";

	@Reference
	private Logger logger;

	@Reference
	private ConfigurationManager configurationManager;

	private Properties config;

	private Map<UUID, AudioEnvironment> registry;

	private boolean running = false;

	@PostConstruct
	private void init() {
		loadAndCheckProperties();

		this.registry = new ConcurrentHashMap<>();

		if (Boolean.parseBoolean(this.config.getProperty(PROPERTY_LOCAL_AUDIO))) {
			try {
				this.registerAudioEnvironment(
						new LocalAudioEnvironment(getDefaultInputAudioFormat(), getDefaultOutputAudioFormat()));
			} catch (LineUnavailableException e) {
				this.logger.error("Could not initialize local audio environment.", e);
			}
		}

	}

	private void loadAndCheckProperties() {
		this.config = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
		if (this.config == null)
			throw new IllegalStateException("Config for audio manager missing.");
		if (this.config.getProperty(PROPERTY_LOCAL_AUDIO) == null)
			throw new IllegalStateException("Property " + PROPERTY_LOCAL_AUDIO + " missing in audio manager config.");
	}

	@Override
	public void registerAudioEnvironment(AudioEnvironment environment) {
		UUID aei = environment.getAudioEnvironmentIdentifier();

		synchronized (this.registry) {
			if (this.registry.containsKey(aei))
				throw new IllegalStateException("An audio environment with the same identifier is already registered!");
			if (this.running) {
				environment.start();
			}
			this.registry.put(aei, environment);
		}
	}

	@Override
	public void unregisterAudioEnvironment(UUID identifier) {
		synchronized (this.registry) {
			AudioEnvironment ae = this.registry.remove(identifier);
			if (ae == null) {
				this.logger.debug("Audio Enviroment to be unregistered is not registered: {}", identifier);
			} else {
				ae.stop();
			}
		}
	}

	@Override
	public void playAudio(UUID identifier, AudioInputStream audioToPlay, OutputBehavior behavior) {
		AudioEnvironment ae = safelyGetEnv(identifier);

		if (!AudioSystem.isConversionSupported(ae.getOutputFormat(), audioToPlay.getFormat()))
			throw new IllegalArgumentException("The format of the input stream is not supported.");

		AudioOutput ao = new AudioOutput(AudioSystem.getAudioInputStream(ae.getOutputFormat(), audioToPlay));

		ae.playAudio(ao, checkBehavior(behavior));
	}

	@Override
	public void stopAudioOutput(UUID identifier) {
		AudioEnvironment ae = safelyGetEnv(identifier);
		ae.stopOutput();
	}

	@Override
	public List<UUID> getAllRegisteredAudioEnvironments() {
		synchronized (this.registry) {
			return new ArrayList<>(this.registry.keySet());
		}
	}

	@Override
	public boolean isAudioEnvironmentCurrentlyOutputting(UUID identifier) {
		AudioEnvironment ae = safelyGetEnv(identifier);
		return ae.isCurrentlyOutputting();
	}

	@Override
	public AudioInputStream getInputStreamOfAudioEnvironment(UUID identifier) {
		AudioEnvironment ae = safelyGetEnv(identifier);
		return ae.getAudioInputStream();
	}

	@Override
	public void start() {
		synchronized (this.registry) {
			for (AudioEnvironment ae : this.registry.values()) {
				ae.start();
			}
			this.running = true;
		}
	}

	@Override
	public void stop() {
		synchronized (this.registry) {
			this.running = false;
			for (AudioEnvironment ae : this.registry.values()) {
				ae.stop();
			}
		}
	}

	/**
	 * Checks if the behavior is possible and changes it if necessary
	 * 
	 * @param behavior
	 *            The behavior to check
	 * @return The resulting behavior
	 */
	private OutputBehavior checkBehavior(OutputBehavior behavior) {
		OutputBehavior ret = behavior;

		if (!this.running && behavior != OutputBehavior.QUEUE_PRIORITY && behavior != OutputBehavior.QUEUE) {
			ret = OutputBehavior.QUEUE;
			this.logger.warn("Audio manager not started yet. Switching behaviour to QUEUE.");
		}

		return ret;
	}

	/**
	 * Get the audio environment corresponding to that identifier. Throw a Exception if that environment is not
	 * registered.
	 * 
	 * @param identifier
	 *            The identifier of the environment
	 * @return The environment
	 */
	private AudioEnvironment safelyGetEnv(UUID identifier) {
		synchronized (this.registry) {
			if (!this.registry.containsKey(identifier))
				throw new IllegalArgumentException("This audio environment is not registered in this audio manager.");
			return this.registry.get(identifier);
		}
	}

	@Override
	public AudioFormat getDefaultInputAudioFormat() {
		final float sampleRate = 16000.0f;
		final int sampleSizeInBits = 16;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

	@Override
	public AudioFormat getDefaultOutputAudioFormat() {
		final float sampleRate = 16000.0f;
		final int sampleSizeInBits = 16;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

}
