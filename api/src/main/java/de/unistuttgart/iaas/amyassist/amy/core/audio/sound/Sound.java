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

import javax.sound.sampled.AudioFormat;

/**
 * The representation of a sound, which can be played.
 * 
 * @author Tim Neumann
 */
public interface Sound {
	/**
	 * Get a sound player that will play this audio once.
	 * 
	 * @return The {@link SoundPlayer}.
	 */
	SoundPlayer getSinglePlayer();

	/**
	 * Get a sound player that will play this audio the given amount of times.
	 * 
	 * @param loopCount
	 *            How often to loop. Must be > 0
	 * @return The {@link SoundPlayer}
	 */
	SoundPlayer getLoopPlayer(int loopCount);

	/**
	 * Get a sound player that will play this audio for ever.
	 * 
	 * @return The {@link SoundPlayer}
	 */
	SoundPlayer getInfiniteLoopPlayer();

	/**
	 * Get's the length of the sound, expressed in sample frames rather than bytes.
	 * 
	 * @return the length in sample frames
	 */
	int getFrameLength();

	/**
	 * Get's the {@link AudioFormat} of this sound
	 * 
	 * @return the format
	 */
	AudioFormat getFormat();

}
