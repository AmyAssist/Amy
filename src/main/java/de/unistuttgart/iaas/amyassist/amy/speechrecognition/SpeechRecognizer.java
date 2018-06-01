/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.speechrecognition;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

/**
 * TODO: Description
 *
 * @author Tim Neumann, Patrick Gebhardt, Patrick Singer, Florian Bauer, Kai
 *         Menzel
 */
public class SpeechRecognizer {
	// wakeup-sleep-shutdown
	private String wakeUp;
	private String goSleep;
	private String shutdown;

	// All different Recognizers (each can have a different Grammparserar)
	private StreamSpeechRecognizer mainGrammar_recognizer;
	private StreamSpeechRecognizer noGrammar_recognizer;
	private final String mainGrammar = "MAIN_GRAMMAR";

	// Logger
	private Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Constructor
	 */
	public SpeechRecognizer(String p_grammarPath, String p_wakeUp, String p_sleep, String p_shutdown, InputStream p_is1,
			InputStream p_is2) {

		this.wakeUp = p_wakeUp;
		this.goSleep = p_sleep;
		this.shutdown = p_shutdown;

		// Loading Message
		this.logger.log(Level.INFO, "Loading Speech Recognizer...\n");

		// Configuration
		Configuration mainGrammar_configuration = new Configuration();
		Configuration noGrammar_configuration = new Configuration();

		// Load model from the jar
		mainGrammar_configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		noGrammar_configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		mainGrammar_configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		noGrammar_configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");

		mainGrammar_configuration.setGrammarPath(p_grammarPath);
		mainGrammar_configuration.setUseGrammar(true);
		noGrammar_configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

		// with Grammar
		mainGrammar_configuration.setGrammarName("grammar");

		try {
			this.mainGrammar_recognizer = new StreamSpeechRecognizer(mainGrammar_configuration);
			this.noGrammar_recognizer = new StreamSpeechRecognizer(noGrammar_configuration);
		} catch (IOException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}

		Thread mainThread = new Thread(new Runnable() {
			@Override
			public void run() {
				SpeechRecognizer.this.mainGrammar_recognizer.startRecognition(p_is1);
				SpeechRecognizer.this.logger.log(Level.INFO, "You can start to speak...\n");
				recognize_main();
			}
		});
		Thread noGrammarThread = new Thread(new Runnable() {
			@Override
			public void run() {
				SpeechRecognizer.this.noGrammar_recognizer.startRecognition(p_is2);
				SpeechRecognizer.this.logger.log(Level.INFO, "No grammar initialized.\n");
				while (true) {
					SpeechResult speechResult = null;
					while (speechResult == null) {
						speechResult = SpeechRecognizer.this.noGrammar_recognizer.getResult();
					}
					// TODO: Das result vom zweiten recoginzer auch für
					// irgendwas verwenden.
				}
			}
		});
		mainThread.start();
		noGrammarThread.start();
	}

	/**
	 * The main recognizer
	 */
	public void recognize_main() {
		boolean listening = false;

		while (true) {
			if (!listening) {
				System.out.println("Wake up the system: \"" + this.wakeUp + "\"");
			} else {
				System.out.println("Choose a command!");
				System.out.println("send the system sleeping: \"" + this.goSleep + "\"");
				System.out.println("exit Programm: \"" + this.shutdown + "\"");
			}

			/*
			 * This method will return when the end of speech is reached. Note
			 * that the end pointer will determine the end of speech.
			 */
			SpeechResult speechResult = null;
			while (speechResult == null) {
				speechResult = this.mainGrammar_recognizer.getResult();
			}

			// Get the hypothesis
			String result = speechResult.getHypothesis();
			System.out.println("Result:" + result);

			if (!listening) {
				// check if we have to wake up
				if (result.equals(this.wakeUp)) {
					// wakeup
					listening = true;
					this.logger.log(Level.INFO, "Listening");
				} else if (result.startsWith(this.wakeUp)) {
					// wakeup
					listening = true;

					// Call the appropriate method
					makeDecision(result, speechResult.getWords());
				} else {
					this.logger.log(Level.INFO, "Ingoring Speech Recognition Results...");
				}
			} else if (result.equals(this.goSleep)) {
				listening = false;
				this.logger.log(Level.INFO, "Going to sleep.");
			} else {
				// Call the appropriate method
				makeDecision(result, speechResult.getWords());
			}

		}
	}

	/**
	 * Takes a decision based on the given result
	 *
	 * @param speechWords
	 */
	public void makeDecision(String speech, List<WordResult> speechWords) {

		if (speech.replace(" ", "").equals("") || speech.equals("<unk>")) {
			this.logger.log(Level.INFO, "I can't understand what you said.\n");
			return;
		}

		// You said?
		System.out.println("You said: [" + speech + "]\n");

		if (speech.startsWith(this.wakeUp)) {
			speech.replaceFirst(this.wakeUp + " ", "");
			speechWords.remove(0);
		}

		if (speech.equals(this.shutdown)) {
			System.exit(0);
		}

		SpeechRecognitionMain.handleInput(speech.split(" "));

	}
}
