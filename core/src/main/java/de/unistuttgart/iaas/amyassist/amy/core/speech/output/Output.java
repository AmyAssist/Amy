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

import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;

/**
 * A class used by the speech system to output audio.
 * 
 * @author Kai Menzel, Tim Neumann
 */
public interface Output {

	/**
	 * Method to Voice and Log output the input String
	 * 
	 * @param s
	 *            String that shall be said
	 */
	void voiceOutput(String s);

	/**
	 * Method to Voice and Log output the input String
	 * 
	 * @param s
	 *            String that shall be said
	 * @param callback
	 *            A callback being called when the output is done
	 */
	void voiceOutput(String s, Runnable callback);

	/**
	 * Method that outputs a sound
	 * 
	 * @param sound
	 *            to output
	 */
	void soundOutput(Sounds sound);

	/**
	 * Method that outputs a sound
	 * 
	 * @param sound
	 *            to output
	 * @param callback
	 *            A callback being called when the output is done
	 */
	void soundOutput(Sounds sound, Runnable callback);

	/**
	 * stop the OutputClip
	 */
	void stopOutput();

	/**
	 * @return whether this output is currently outputting information.
	 */
	boolean isCurrentlyOutputting();
}
