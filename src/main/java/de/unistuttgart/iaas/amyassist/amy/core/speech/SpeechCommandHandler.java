/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.AnnotationReader;
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
	}

	/**
	 * Call this after all registerSpeechCommand and before handleSpeechInput
	 */
	public void completeSetup() {
		this.textToPlugin = new TextToPlugin(this.grammarInfos.keySet());
	}

	public String handleSpeechInput(String input) {
		String[] pluginActionFromText = this.textToPlugin.pluginActionFromText(input);
		if (pluginActionFromText == null) {
			throw new IllegalArgumentException(input);
		}
		return this.call(this.speechCommands.get(pluginActionFromText[1]), input);
	}

	private String call(SpeechCommand command, String input) {
		Class<?> speechCommandClass = command.getSpeechCommandClass();
		Object speechCommandClassInstance = this.serviceLocator.create(speechCommandClass);
		return command.call(speechCommandClassInstance, input);
	}
}
