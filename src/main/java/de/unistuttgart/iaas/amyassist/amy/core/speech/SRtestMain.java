/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import org.omg.CORBA.PRIVATE_MEMBER;

import de.unistuttgart.iaas.amyassist.amy.core.AnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.core.GrammarParser;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;

/**
 * TODO: Description
 * @author Kai Menzel
 */
public class SRtestMain {

	private static final File grammarFile = new File("src/main/resources", "/sphinx-grammars/grammar.gram");
	private static final String wakeUp = "amy";
	private static final String goSleep = "sleep";
	private static final String shutDown = wakeUp + " shutdown";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		GrammarParser generator = new GrammarParser(SRtestMain.grammarFile.getName().replace(".gram", ""), SRtestMain.wakeUp, SRtestMain.goSleep, SRtestMain.shutDown);
		
//		TODO: for each Plugin
//		generator.addRule(ruleName, keyword, grammar);
		
		String grammar = generator.getGrammar();

		SRtestMain.grammarFile.getParentFile().mkdirs();
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(SRtestMain.grammarFile))) {
			bw.write(grammar);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		(new Thread(new SpeechRecognizer(SRtestMain.wakeUp, SRtestMain.goSleep, SRtestMain.shutDown, SRtestMain.grammarFile.getParent(), SRtestMain.grammarFile.getName().replace(".gram", ""), null))).start();

	}

}
