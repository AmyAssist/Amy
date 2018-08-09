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

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.UUID;

import javax.sound.sampled.LineUnavailableException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment;
import de.unistuttgart.iaas.amyassist.amy.core.audio.environment.LocalAudioEnvironment;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Implementation of {@link LocalAudio}
 * 
 * @author Tim Neumann
 */
@Service(LocalAudio.class)
public class LocalAudioService implements LocalAudio {

	private static final String CONFIG_NAME = "localAudio.config";
	private static final String PROPERTY_ENABLE = "enable";

	@Reference
	private Logger logger;

	@Reference
	private ConfigurationManager configurationManager;

	@Reference
	private InternalAudioManager am;

	private Properties config;

	private UUID identifier;

	@PostConstruct
	private void init() {
		loadAndCheckProperties();
		this.identifier = null;

		if (Boolean.parseBoolean(this.config.getProperty(PROPERTY_ENABLE))) {
			try {
				AudioEnvironment localAe = new LocalAudioEnvironment(this.am.getDefaultInputAudioFormat(),
						this.am.getDefaultOutputAudioFormat());
				this.am.registerAudioEnvironment(localAe);
				this.identifier = localAe.getAudioEnvironmentIdentifier();
			} catch (LineUnavailableException e) {
				this.logger.error("Could not initialize local audio environment.", e);
			}
		}
	}

	private void loadAndCheckProperties() {
		this.config = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
		if (this.config == null)
			throw new IllegalStateException("Config for audio manager missing.");
		if (this.config.getProperty(PROPERTY_ENABLE) == null)
			throw new IllegalStateException("Property " + PROPERTY_ENABLE + " missing in audio manager config.");
	}

	@Override
	public UUID getLocalAudioEnvironmentIdentifier() {
		if (!this.isLocalAudioAvailable())
			throw new NoSuchElementException("The local audio environment is not available");
		return this.identifier;
	}

	@Override
	public boolean isLocalAudioAvailable() {
		return this.identifier != null;
	}

}
