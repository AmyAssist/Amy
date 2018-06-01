/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.speechrecognition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import de.unistuttgart.iaas.amyassist.amy.core.GrammarParser;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin;

/**
 * TODO: Description
 *
 * @author Tim Neumann, Patrick Gebhardt, Patrick Singer, Florian Bauer, Kai
 *         Menzel
 */
public class SpeechRecognitionMain {

	private static HashMap<String, Plugin> plugins = new HashMap<>();
	private static HashMap<String, Plugin> keywords = new HashMap<>();

	private static String grammar = "grammar";
	private static String wakeup = "amy";
	private static String sleep = "sleep";
	private static String stop = "stop";

	private static File grammarFile = new File(new File(System.getProperty("java.io.tmpdir")),
			"/sphinx-grammars/grammar.gram");

	private static Scanner s;

	static MyInputStream microphoneInputStream1;
	static MyInputStream microphoneInputStream2;

	public static AudioFormat getFormat() {
		float sampleRate = 16000.0f;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

	/**
	 * Starts the speech recognition
	 */
	public static void start() {
		GrammarParser generator = new GrammarParser(SpeechRecognitionMain.grammar, SpeechRecognitionMain.wakeup,
				SpeechRecognitionMain.sleep, SpeechRecognitionMain.stop);
		for (Entry<String, Plugin> e : SpeechRecognitionMain.plugins.entrySet()) {
			generator.addPluginGrammar(e.getKey(), e.getValue().getKeywords(), e.getValue().getActions(),
					e.getValue().getParameters());
		}
		String grammar = generator.getGrammar();

		SpeechRecognitionMain.grammarFile.getParentFile().mkdirs();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(SpeechRecognitionMain.grammarFile))) {
			bw.write(grammar);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Thread in = new Thread(new InputThread());
		in.start();

		while (SpeechRecognitionMain.microphoneInputStream1 == null) {
			Thread.yield();
		}

		SpeechRecognizer sr = new SpeechRecognizer(SpeechRecognitionMain.grammarFile.getParent(),
				SpeechRecognitionMain.wakeup, SpeechRecognitionMain.sleep, SpeechRecognitionMain.stop,
				SpeechRecognitionMain.microphoneInputStream1, SpeechRecognitionMain.microphoneInputStream2);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		SpeechRecognitionMain.s = new Scanner(System.in);

		System.out.println("Willkommen bei AMY. Bitte geben Sie ein befehl ein.");

		while (true) {
			if (SpeechRecognitionMain.s.hasNextLine()) {
				String line = SpeechRecognitionMain.s.nextLine();
				SpeechRecognitionMain.handleInput(line.split(" "));
			}
		}
	}

	/**
	 * Handle a speech input
	 *
	 * @param p_words
	 *            The words that were heard.
	 */
	public static void handleInput(String... p_words) {
		try {
			Request r = new Request(p_words);
			if (!SpeechRecognitionMain.keywords.containsKey(r.getKeyword())) {
				System.err.println("Unknown Keyword!!!");
			} else {

				SpeechRecognitionMain.keywords.get(r.getKeyword()).handleRequest(r);

			}
		} catch (IllegalArgumentException e) {
			System.out.println("Need a keyword and a action.");
		}

	}

	/**
	 * Register a plugin.
	 *
	 * @param plugin
	 *            The plugin to register.
	 * @param unique_name
	 *            The unique name of this plugin.
	 */
	public static void registerPlugin(Plugin plugin, String unique_name) {
		SpeechRecognitionMain.plugins.put(unique_name, plugin);
		for (String keyword : plugin.getKeywords()) {
			SpeechRecognitionMain.keywords.put(keyword, plugin);
		}
	}

	private static class InputThread implements Runnable {

		/**
		 *
		 */
		public InputThread() {
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			System.out.println("Start input");
			try {
				TargetDataLine microphone = AudioSystem.getTargetDataLine(SpeechRecognitionMain.getFormat());

				System.out.println("OK");

				microphone.open(SpeechRecognitionMain.getFormat());
				microphone.start();

				System.out.println("Start capture");
				AudioInputStream ais = new AudioInputStream(microphone);

				ArrayBlockingQueue<Integer> bytes1 = new ArrayBlockingQueue<>(100);
				ArrayBlockingQueue<Integer> bytes2 = new ArrayBlockingQueue<>(100);

				SpeechRecognitionMain.microphoneInputStream1 = new MyInputStream(bytes1);
				SpeechRecognitionMain.microphoneInputStream2 = new MyInputStream(bytes2);

				System.out.println("Start recording");

				byte[] bs = new byte[SpeechRecognitionMain.getFormat().getFrameSize()];

				while (true) {
					int count = ais.read(bs, 0, SpeechRecognitionMain.getFormat().getFrameSize());

					if (count < 0) {
						System.out.println("NORMAL");
					}

					for (byte b : bs) {
						try {
							bytes1.put(new Integer(b));
							bytes2.put(new Integer(b));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
