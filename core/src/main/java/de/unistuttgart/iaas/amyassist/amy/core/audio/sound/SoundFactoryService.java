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

package de.unistuttgart.iaas.amyassist.amy.core.audio.sound;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Implementation of {@link SoundFactory}
 * 
 * @author Tim Neumann
 */
@Service
public class SoundFactoryService implements SoundFactory {

	@Override
	public Sound loadSound(AudioInputStream data) throws IOException {
		return new BasicSound(data);
	}

	@Override
	public Sound loadSound(URL location) throws UnsupportedAudioFileException, IOException {
		return loadSound(AudioSystem.getAudioInputStream(location));
	}

	@Override
	public Sound loadSound(Path location) throws UnsupportedAudioFileException, IOException {
		return loadSound(AudioSystem.getAudioInputStream(location.toFile()));
	}

}
