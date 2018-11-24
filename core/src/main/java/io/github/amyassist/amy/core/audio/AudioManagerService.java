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

import java.util.List;
import java.util.UUID;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * A wrapper around {@link InternalAudioManagerService} which only implements {@link AudioManager}.
 * 
 * @author Tim Neumann
 */
@Service(AudioManager.class)
public class AudioManagerService implements AudioManager {

	@Reference
	private InternalAudioManager iam;

	@Override
	public void playAudio(UUID identifier, AudioInputStream audioToPlay, OutputBehavior behavior) {
		this.iam.playAudio(identifier, audioToPlay, behavior);
	}

	@Override
	public void stopAudioOutput(UUID identifier) {
		this.iam.stopAudioOutput(identifier);
	}

	@Override
	public List<UUID> getAllRegisteredAudioEnvironments() {
		return this.iam.getAllRegisteredAudioEnvironments();
	}

	@Override
	public boolean isAudioEnvironmentCurrentlyOutputting(UUID identifier) {
		return this.iam.isAudioEnvironmentCurrentlyOutputting(identifier);
	}

	@Override
	public AudioInputStream getInputStreamOfAudioEnvironment(UUID identifier) {
		return this.iam.getInputStreamOfAudioEnvironment(identifier);
	}

	@Override
	public AudioFormat getDefaultOutputAudioFormat() {
		return this.iam.getDefaultOutputAudioFormat();
	}

	@Override
	public AudioFormat getDefaultInputAudioFormat() {
		return this.iam.getDefaultInputAudioFormat();
	}

}
