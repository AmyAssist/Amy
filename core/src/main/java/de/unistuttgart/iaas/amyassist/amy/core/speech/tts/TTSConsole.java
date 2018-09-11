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

package de.unistuttgart.iaas.amyassist.amy.core.speech.tts;

import asg.cliche.Command;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output;

/**
 * Console Tool to test the TextToSpeech service
 * 
 * @author Leon Kiefer
 */
public class TTSConsole {
	@Reference
	private Output output;

	@Command(name = "TextToSpeech", abbrev = "tts", description = "Let the TextToSpeech Service output text as speech")
	public void textToSpeech(String... text) {
		this.output.voiceOutput(String.join(" ", text));
	}

	@Command(name = "StopTextToSpeech", abbrev = "tts:stop",
			description = "stop the current and all queued output of the TextToSpeech Service")
	public void stopTextToSpeech() {
		this.output.stopOutput();
	}
}
