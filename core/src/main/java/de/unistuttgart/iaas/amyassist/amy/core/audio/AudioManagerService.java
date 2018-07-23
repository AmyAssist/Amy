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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;

/**
 * The implementation of {@link AudioManager}
 * 
 * @author Tim Neumann
 */
@Service
public class AudioManagerService implements AudioManager, RunnableService {

	private static final String CONFIG_NAME = "audio.config";
	private static final String PROPERTY_LOCAL_AUDIO = "enableLocalAudio";

	@Reference
	private Logger logger;

	@Reference
	private ConfigurationLoader configurationLoader;

	private Properties config;

	private Map<AudioEnvironmentIdetifier, AudioEnvironmentState> registry;

	private ExecutorService workerThreads;

	private boolean started = false;

	@PostConstruct
	private void init() {
		loadAndCheckProperties();

		this.registry = new HashMap<>();

		if (Boolean.parseBoolean(this.config.getProperty(PROPERTY_LOCAL_AUDIO))) {
			try {
				this.registerAudioEnvironment(
						new LocalAudioEnvironment(getDefaultInputAudioFormat(), getDefaultOutputAudioFormat()));
			} catch (LineUnavailableException e) {
				this.logger.error("Could not initialize local audio environment.", e);
			}
		}

		this.workerThreads = Executors.newCachedThreadPool();
	}

	private void loadAndCheckProperties() {
		this.config = this.configurationLoader.load(CONFIG_NAME);
		if (this.config == null)
			throw new IllegalStateException("Config for audio manager missing.");
		if (this.config.getProperty(PROPERTY_LOCAL_AUDIO) == null)
			throw new IllegalStateException("Property " + PROPERTY_LOCAL_AUDIO + " missing in audio manager config.");
	}

	/**
	 * Registers an audio environment in the audio manager.
	 * 
	 * @param environment
	 *            The audio environment to register.
	 * @throws IllegalStateException
	 *             When an audio environment with the same identifier is already registered.
	 */
	protected void registerAudioEnvironment(AudioEnvironment environment) {
		AudioEnvironmentIdetifier aei = environment.getAudioEnvironmentIdentifier();

		if (this.registry.containsKey(aei))
			throw new IllegalStateException("An audio environment with the same identifier is already registered!");
		this.registry.put(aei, new AudioEnvironmentState(environment));
	}

	/**
	 * Removes the audio environment described by the given identifier from the audio manager.
	 * 
	 * @param identifier
	 *            The identifier of the audio environment to remove.
	 */
	protected synchronized void unregisterAudioEnvironment(AudioEnvironmentIdetifier identifier) {
		// TODO: Shutdown any workers for this environment
		this.registry.remove(identifier);
	}

	@Override
	public synchronized void playAudioGlobaly(AudioInputStream audioToPlay, OutputBehavior behavior) {

		if (!AudioSystem.isConversionSupported(getDefaultOutputAudioFormat(), audioToPlay.getFormat()))
			throw new IllegalArgumentException("The format of the input stream is not supported.");
		AudioOutput ao = new AudioOutput(AudioSystem.getAudioInputStream(getDefaultOutputAudioFormat(), audioToPlay),
				true);
		OutputBehavior realBehavior = checkBehavior(behavior);

		List<AudioEnvironmentState> states = new ArrayList<>();

		for (AudioEnvironmentIdetifier aei : this.registry.keySet()) {
			if (!aei.shouldPlayGlobalSound()) {
				continue;
			}
			AudioEnvironmentState state = this.registry.get(aei);
			states.add(state);
			switch (realBehavior) {
			case INTERRUPT_ALL:
				state.getOutputQueue().clear();
				interruptWorker(state);
				finanlizeWorker(state, true);
				break;
			case INTERRUPT_CURRENT:
				interruptWorker(state);
				finanlizeWorker(state, true);
				break;
			case QUEUE:
				state.getOutputQueue().add(ao);
				break;
			case QUEUE_PRIORITY:
				state.getOutputQueue().addFirst(ao);
				break;
			case SUSPEND:
				interruptWorker(state);
				finanlizeWorker(state, false);
				break;
			default:
				throw new IllegalStateException("Unknown behavior");
			}
		}

		if (realBehavior == OutputBehavior.INTERRUPT_ALL || realBehavior == OutputBehavior.INTERRUPT_CURRENT
				|| realBehavior == OutputBehavior.SUSPEND) {
			startGlobalWorker(states, ao);
		}

	}

	@Override
	public synchronized void playAudio(AudioEnvironmentIdetifier identifier, AudioInputStream audioToPlay,
			OutputBehavior behavior) {
		if (!AudioSystem.isConversionSupported(getDefaultOutputAudioFormat(), audioToPlay.getFormat()))
			throw new IllegalArgumentException("The format of the input stream is not supported.");

		AudioEnvironmentState state = safelyGetState(identifier);
		AudioOutput ao = new AudioOutput(AudioSystem.getAudioInputStream(getDefaultOutputAudioFormat(), audioToPlay),
				false);

		switch (checkBehavior(behavior)) {
		case INTERRUPT_ALL:
			state.getOutputQueue().clear();
			interruptWorker(state);
			finanlizeWorker(state, true);
			startSingleWorker(state, ao);
			break;
		case INTERRUPT_CURRENT:
			interruptWorker(state);
			finanlizeWorker(state, true);
			startSingleWorker(state, ao);
			break;
		case QUEUE:
			state.getOutputQueue().add(ao);
			checkWorkerHasWork(state);
			break;
		case QUEUE_PRIORITY:
			state.getOutputQueue().addFirst(ao);
			checkWorkerHasWork(state);
			break;
		case SUSPEND:
			interruptWorker(state);
			finanlizeWorker(state, false);
			startSingleWorker(state, ao);
			break;
		default:
			throw new IllegalStateException("Unknown behavior");
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager#interruptAudio(de.unistuttgart.iaas.amyassist.amy.core.audio.AudioEnvironmentIdetifier)
	 */
	@Override
	public synchronized void interruptAudio(AudioEnvironmentIdetifier identifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<AudioEnvironmentIdetifier> getAllRegisteredAudioEnvironments() {
		return new ArrayList<>(this.registry.keySet());
	}

	@Override
	public boolean isAudioEnvironmentCurrentlyOutputting(AudioEnvironmentIdetifier identifier) {
		return safelyGetState(identifier).getCurrentOutputWorker() != null;
	}

	@Override
	public AudioInputStream getInputStreamOfAudioEnvironment(AudioEnvironmentIdetifier identifier) {
		if (this.registry.get(identifier).isMicrophoneLineConsumed())
			throw new IllegalStateException(
					"There is already a input stream. Multiple input streams are currently not supported.");
		this.registry.get(identifier).setMicrophoneLineConsumed(true);
		return new AudioInputStream(this.registry.get(identifier).getAe().getMicrophoneInputLine());
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	/**
	 * A callback for when a worker finishes. This finalizes the worker and checks if a new worker needs to be started.
	 * 
	 * @param identifier
	 *            The identifier of the audio environment the worker was working for.
	 */
	protected synchronized void onWorkerFinished(AudioEnvironmentIdetifier identifier) {
		if (this.registry.containsKey(identifier)) {
			AudioEnvironmentState state = this.registry.get(identifier);
			finanlizeWorker(state, false);
			checkWorkerHasWork(state);
		}
	}

	/**
	 * A callback for when a global worker finishes. This finalizes the worker and checks if a new workers need to be
	 * started.
	 * 
	 * @param identifiers
	 *            The identifiers of the global audio environments
	 */
	protected synchronized void onGlobalWorkerFinished(List<AudioEnvironmentIdetifier> identifiers) {
		for (AudioEnvironmentIdetifier identifier : identifiers) {
			AudioEnvironmentState state = this.registry.get(identifier);
			if (state == null)
				throw new IllegalStateException("AudioEnvironmet not registered anymore.");
			state.setCurrentOutputWorker(null);
			if (!state.getCurrentStream().isFinished()) {
				state.getOutputQueue().addFirst(state.getCurrentStream());
			}
			state.setCurrentStream(null);
			checkWorkerHasWork(state);
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
		if (!this.started) {
			if (behavior != OutputBehavior.QUEUE_PRIORITY) {
				ret = OutputBehavior.QUEUE;
				this.logger.warn("Audio manager not started yet. Switching behaviour to QUEUE.");
			}
		}
		return ret;
	}

	/**
	 * Get the state of the given audio environment. Throw a Exception if that Environment is not registered.
	 * 
	 * @param identifier
	 *            The identifier of the environment
	 * @return The state.
	 */
	private AudioEnvironmentState safelyGetState(AudioEnvironmentIdetifier identifier) {
		if (!this.registry.containsKey(identifier))
			throw new IllegalArgumentException("This audio environment is not registered in this audio manager.");
		return this.registry.get(identifier);
	}

	/**
	 * Finalizes the finished worker for the given audio environment
	 * 
	 * @param state
	 *            The state of the audio environment
	 * @param discardStream
	 *            Whether to discard the stream or re inject it into the queue if it is not finished.
	 */
	private void finanlizeWorker(AudioEnvironmentState state, boolean discardStream) {
		if (!state.getCurrentOutputWorker().isDone())
			throw new IllegalStateException("Can't finalize a worker that is not done.");
		if (state.getCurrentStream().isGlobal()) // TODO: Fix for global worker
			throw new IllegalStateException("Can't finalize a global worker.");
		state.setCurrentOutputWorker(null);
		if (!state.getCurrentStream().isFinished()) {
			if (discardStream) {
				try {
					state.getCurrentStream().getInpuStream().close();
				} catch (IOException e) {
					this.logger.warn("IO Exception while closing input stream", e);
				}
			} else {
				state.getOutputQueue().addFirst(state.getCurrentStream());
			}
		}
		state.setCurrentStream(null);
	}

	/**
	 * Checks of the worker for the given environment is currently not working and has new work to do. If that is the
	 * case this method will start a worker
	 * 
	 * @param state
	 *            The state of the audio environment
	 */
	private void checkWorkerHasWork(AudioEnvironmentState state) {
		if (state.getCurrentOutputWorker() != null) {
			if (!state.getCurrentOutputWorker().isDone())
				return;
			finanlizeWorker(state, false);
		}

		if (state.getOutputQueue().isEmpty())
			return;

		if (state.getOutputQueue().peek().isGlobal()) {
			checkIfGlobalOutputCanStart(state.getOutputQueue().peek());
		} else {
			startSingleWorker(state, state.getOutputQueue().poll());
		}
	}

	private void checkIfGlobalOutputCanStart(AudioOutput ao) {
		List<AudioEnvironmentState> states = new ArrayList<>();
		for (AudioEnvironmentIdetifier aei : this.registry.keySet()) {
			AudioEnvironmentState state = this.registry.get(aei);
			if (!state.getOutputQueue().contains(ao)) {
				continue;
			}
			states.add(state);

			if (state.getCurrentOutputWorker() != null)
				return;
			if (!state.getOutputQueue().peek().equals(ao))
				return;
		}

		startGlobalWorker(states, ao);
	}

	/**
	 * Interrupts the current worker of the given environment
	 * 
	 * @param state
	 *            The state of the audio environment
	 */
	private void interruptWorker(AudioEnvironmentState state) {
		if (state.getCurrentOutputWorker() != null && !state.getCurrentOutputWorker().isDone()) {
			state.getCurrentOutputWorker().cancel(true);
		}
	}

	/**
	 * Starts a single output worker
	 * 
	 * @param ao
	 *            The audio output to do
	 * @param state
	 *            The state of the audio environment
	 */
	private void startSingleWorker(AudioEnvironmentState state, AudioOutput ao) {
		if (ao.isGlobal())
			throw new IllegalArgumentException("The audio output is global");
		if (state.getCurrentOutputWorker() != null)
			throw new IllegalStateException("The audio environment still has a running worker");

		SingleOutputWorker worker = new SingleOutputWorker(ao, state.getAe().getSpeakerOutputLine());
		CompletableFuture<Void> future = CompletableFuture.runAsync(worker, this.workerThreads);
		future.whenComplete((nu1, nu2) -> onWorkerFinished(state.getAe().getAudioEnvironmentIdentifier()));

		state.setCurrentStream(ao);
		state.setCurrentOutputWorker(future);
	}

	/**
	 * Starts a global output worker.
	 * 
	 * @param states
	 *            The states of the global audio environments
	 * @param ao
	 *            The
	 */
	private void startGlobalWorker(List<AudioEnvironmentState> states, AudioOutput ao) {
		if (!ao.isGlobal())
			throw new IllegalArgumentException("The audio output is not global");

		for (AudioEnvironmentState state : states) {
			if (state.getCurrentOutputWorker() != null)
				throw new IllegalStateException("One audio environment still has a running worker");
		}

		List<SourceDataLine> sdls = states.stream().map(state -> state.getAe().getSpeakerOutputLine())
				.collect(Collectors.toList());

		List<AudioEnvironmentIdetifier> ids = states.stream()
				.map(state -> state.getAe().getAudioEnvironmentIdentifier()).collect(Collectors.toList());

		MultiOutputWorker worker = new MultiOutputWorker(ao, sdls);
		CompletableFuture<Void> future = CompletableFuture.runAsync(worker, this.workerThreads);
		future.whenComplete((nu1, nu2) -> onGlobalWorkerFinished(ids));

		for (AudioEnvironmentState state : states) {
			state.setCurrentStream(ao);
			state.setCurrentOutputWorker(future);
		}
	}

	/**
	 * @return The default audio format used for input.
	 */
	public AudioFormat getDefaultInputAudioFormat() {
		final float sampleRate = 16000.0f;
		final int sampleSizeInBits = 16;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

	/**
	 * @return The default audio format used for input.
	 */
	public AudioFormat getDefaultOutputAudioFormat() {
		final float sampleRate = 16000.0f;
		final int sampleSizeInBits = 16;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

}
