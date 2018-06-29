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

package de.unistuttgart.iaas.amyassist.amy.core.speech;

/**
 * Class for important Constants for the SpeechRecognition
 * Holds variabel if Recognition System is currently active
 * 
 * @author Leon Kiefer
 */
public class Constants {

	private Constants() {
		// hide constructor
	}

	/**
	 * Command String to wake up amy's SpeechRecogniton System
	 */
	public static final String WAKE_UP = "amy wake up";
	/**
	 * Command String to set the SpeechRecognition inactive, to stop listening to input until waked again
	 */
	public static final String GO_SLEEP = "go to sleep";
	/**
	 * Command String to stop current Voice Output of Amy
	 */
	public static final String SHUT_UP = "amy shut up";

	/**
	 * STATIC VARIABEL
	 * Information for The System to turn down sound true if the SR is currently listening
	 */
	private static boolean isSRListening = false;
	

	/**
	 * Get's {@link #isSRListening sRisListening}
	 * @return  sRisListening
	 */
	public static boolean isSRListening() {
		return isSRListening;
	}

	/**
	 * Set's {@link #isSRListening sRisListening}
	 * @param sRisListening  sRisListening
	 */
	protected static void setSRListening(boolean sRisListening) {
		isSRListening = sRisListening;
	}
	
}
