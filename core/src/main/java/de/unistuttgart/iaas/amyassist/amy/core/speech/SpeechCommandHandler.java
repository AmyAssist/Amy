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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.PluginGrammarInfo;
import de.unistuttgart.iaas.amyassist.amy.core.TextToPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.JSGFGenerator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.util.NaturalLanguageInterpreterAnnotationReader;

/**
 * Handles incoming SpeechCommand requests
 * 
 * @author Leon Kiefer
 */
@Service
@Deprecated
public class SpeechCommandHandler {
	@Reference
	private Logger logger;
	@Reference
	private Environment environment;

	private TextToPlugin textToPlugin;

	private JSGFGenerator generator = new JSGFGenerator("grammar", Constants.WAKE_UP, Constants.GO_SLEEP,
			Constants.SHUT_UP);

	private Map<PluginGrammarInfo, Class<?>> grammarInfos = new HashMap<>();
	private Map<String, de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommand> speechCommands = new HashMap<>();
	@Reference
	private ServiceLocator serviceLocator;

	/**
	 * Use this to register all SpeechCommand classes
	 * 
	 * @param class1
	 *            the class that should be registered
	 */
	public void registerSpeechCommand(Class<?> class1) {
		if (!class1.isAnnotationPresent(de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand.class))
			throw new IllegalArgumentException();
		String[] speechKeyword = NaturalLanguageInterpreterAnnotationReader.getSpeechKeyword(class1);
		Map<String, SpeechCommand> grammars = NaturalLanguageInterpreterAnnotationReader.getGrammars(class1);
		PluginGrammarInfo pluginGrammarInfo = new PluginGrammarInfo(Arrays.asList(speechKeyword), grammars.keySet());
		this.grammarInfos.put(pluginGrammarInfo, class1);
		this.speechCommands.putAll(grammars);
		for (Map.Entry<String, SpeechCommand> e : grammars.entrySet()) {
			this.generator.addRule(UUID.randomUUID().toString(), speechKeyword, e.getKey());
		}

	}

	/**
	 * Call this after all registerSpeechCommand and before handleSpeechInput
	 */
	public void completeSetup() {
		String grammar = this.generator.generateGrammarFileString();
		try {
			Files.createDirectories(this.getFileToSaveGrammarTo().getParent());
		} catch (IOException e) {
			throw new IllegalStateException("Can't create parent directories of the grammar file", e);
		}
		try (BufferedWriter bw = Files.newBufferedWriter(this.getFileToSaveGrammarTo(), StandardOpenOption.CREATE)) {
			bw.write(grammar);
		} catch (IOException e) {
			throw new IllegalStateException("Can't write grammar file", e);
		}

		this.textToPlugin = new TextToPlugin(this.grammarInfos.keySet());
	}

	public String handleSpeechInput(String input) {
		this.logger.debug("input {}", input);
		List<String> pluginActionFromText = this.textToPlugin.pluginActionFromText(input);
		if (pluginActionFromText == null)
			throw new IllegalArgumentException(input);
		String[] args = pluginActionFromText.subList(2, pluginActionFromText.size()).toArray(new String[0]);
		return this.call(this.speechCommands.get(pluginActionFromText.get(1)), args);
	}

	private String call(SpeechCommand command, String[] input) {
		Class<?> speechCommandClass = command.getSpeechCommandClass();
		Object speechCommandClassInstance = this.serviceLocator.createAndInitialize(speechCommandClass);
		return command.call(speechCommandClassInstance, input);
	}

	/**
	 * Get's the path where the grammar file should be saved
	 * 
	 * @return fileToSaveToGrammar
	 */
	private Path getFileToSaveGrammarTo() {
		return this.environment.getWorkingDirectory().resolve("resources").resolve("sphinx-grammars/grammar.gram");
	}
}
