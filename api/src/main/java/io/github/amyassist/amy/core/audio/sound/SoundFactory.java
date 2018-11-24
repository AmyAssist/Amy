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

package io.github.amyassist.amy.core.audio.sound;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A factory to produce a Sound object
 * 
 * @author Tim Neumann
 */
public interface SoundFactory {

	/**
	 * Creates a new sound and reads the data for it from the given {@link AudioInputStream}
	 * 
	 * @param data
	 *            The stream delivering the audio data for this sound
	 * @return The loaded {@link Sound} object.
	 * @throws IOException
	 *             if an input or output error occurs while reading from the stream.
	 */
	public Sound loadSound(AudioInputStream data) throws IOException;

	/**
	 * Creates a new sound from the file at the given URL.
	 * 
	 * @param location
	 *            The URL of the file to load
	 * @return The loaded {@link Sound} object.
	 * @throws UnsupportedAudioFileException
	 *             When the file at the URL is not in a supported format
	 * @throws IOException
	 *             if an input or output error occurs while reading the file
	 */
	public Sound loadSound(URL location) throws UnsupportedAudioFileException, IOException;

	/**
	 * Creates a new sound from the file at the given Path.
	 * 
	 * @param location
	 *            The Path of the file to load
	 * @return The loaded {@link Sound} object.
	 * @throws UnsupportedAudioFileException
	 *             When the file at the Path is not in a supported format
	 * @throws IOException
	 *             if an input or output error occurs while reading the file
	 */
	public Sound loadSound(Path location) throws UnsupportedAudioFileException, IOException;
}
