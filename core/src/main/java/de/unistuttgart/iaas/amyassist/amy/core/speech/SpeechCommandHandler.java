/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.unistuttgart.iaas.amyassist.amy.core.AnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.core.GrammarParser;
import de.unistuttgart.iaas.amyassist.amy.core.PluginGrammarInfo;
import de.unistuttgart.iaas.amyassist.amy.core.TextToPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Handles incoming SpeechCommand requests
 * 
 * @author Leon Kiefer
 */
@Service
public class SpeechCommandHandler {
	private AnnotationReader annotationReader = new AnnotationReader();
	private TextToPlugin textToPlugin;
	private GrammarParser generator = new GrammarParser("grammar", AudioUserInteraction.wakeUp, AudioUserInteraction.goSleep, AudioUserInteraction.shutdown);

	private Map<PluginGrammarInfo, Class<?>> grammarInfos = new HashMap<>();
	private Map<String, de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechCommand> speechCommands = new HashMap<>();
	@Reference
	private ServiceLocator serviceLocator;

	public void registerSpeechCommand(Class<?> class1) {
		if (!class1.isAnnotationPresent(de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand.class)) {
			throw new RuntimeException();
		}
		String[] speechKeyword = this.annotationReader.getSpeechKeyword(class1);
		Map<String, SpeechCommand> grammars = this.annotationReader.getGrammars(class1);
		PluginGrammarInfo pluginGrammarInfo = new PluginGrammarInfo(Arrays.asList(speechKeyword), grammars.keySet());
		this.grammarInfos.put(pluginGrammarInfo, class1);
		this.speechCommands.putAll(grammars);
		for (Map.Entry<String, SpeechCommand> e : grammars.entrySet()) {
			for (String keyword : speechKeyword)
				this.generator.addRule(UUID.randomUUID().toString(), keyword, e.getKey());
		}

	}

	/**
	 * Call this after all registerSpeechCommand and before handleSpeechInput
	 */
	public void completeSetup() {
		String grammar = this.generator.getGrammar();
		File grammarFile = new File("src/main/resources", "/sphinx-grammars/grammar.gram");
		grammarFile.getParentFile().mkdirs();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(grammarFile))) {
			bw.write(grammar);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		this.textToPlugin = new TextToPlugin(this.grammarInfos.keySet());
	}

	public String handleSpeechInput(String input) {
		List<String> pluginActionFromText = this.textToPlugin.pluginActionFromText(input);
		if (pluginActionFromText == null) {
			throw new IllegalArgumentException(input);
		}
		String[] args = pluginActionFromText.subList(2, pluginActionFromText.size()).toArray(new String[0]);
		return this.call(this.speechCommands.get(pluginActionFromText.get(1)), args);
	}

	private String call(SpeechCommand command, String[] input) {
		Class<?> speechCommandClass = command.getSpeechCommandClass();
		Object speechCommandClassInstance = this.serviceLocator.create(speechCommandClass);
		return command.call(speechCommandClassInstance, input);
	}
}
