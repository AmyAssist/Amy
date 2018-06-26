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

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommandHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;

/**
 * Implementation of SpeechInputHandler
 * 
 * @author Leon Kiefer
 */
@Service
public class NaturalLanaguageInputHandlerService implements SpeechInputHandler {
	@Reference
	private Core core;

	@Reference
	private SpeechCommandHandler speechCommandHandler;

	private ScheduledExecutorService singleThreadScheduledExecutor;

	@PostConstruct
	private void setup() {
		this.singleThreadScheduledExecutor = this.core.getScheduledExecutor();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler#handle(java.lang.String)
	 */
	@Override
	public CompletableFuture<String> handle(String speechInput) {
		return CompletableFuture.supplyAsync(() -> this.speechCommandHandler.handleSpeechInput(speechInput),
				this.singleThreadScheduledExecutor);
	}

}
