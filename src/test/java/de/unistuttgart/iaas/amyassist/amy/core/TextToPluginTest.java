/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test class for TextToPlugin
 * mainly used to test the regex expressions
 * feel free to test your plugin grammar here
 * 
 * @author Felix Burk
 */
public class TextToPluginTest {

	@Test
	void test() {
		AnnotationReader reader = new AnnotationReader();
		ArrayList<String> grammars = new ArrayList<>();
		grammars.add("count count count [count]");
		grammars.add("say (hello|test) [xx|yy] (or|term)");
		grammars.add("amy says [great|bad] (bluuub|blub) things");
		grammars.add("search # in (artist|track|playlist|album)");
		grammars.add("this [grammar|(is | really | (bad | hehe))]");
		grammars.add("play #");
		grammars.add("get devices");
		
		TextToPlugin test = new TextToPlugin(grammars);
		assertThat(test.pluginActionFromText("amy says blub things"), equalTo("amy says [great|bad] (bluuub|blub) things"));
		assertThat(test.pluginActionFromText("jdwpojdpa amy says blub things jdwopajd"), equalTo("amy says [great|bad] (bluuub|blub) things"));
		assertThat(test.pluginActionFromText("amy says great blub things"), equalTo("amy says [great|bad] (bluuub|blub) things"));
		assertThat(test.pluginActionFromText("amy says bluuub things"), equalTo("amy says [great|bad] (bluuub|blub) things"));

		assertThat(test.pluginActionFromText("play 380213910"), equalTo("play #"));
		assertThat(test.pluginActionFromText("play "),  equalTo(null));
		
		assertThat(test.pluginActionFromText("get devices"), equalTo("get devices"));

		assertThat(test.pluginActionFromText("blah count count count count count blah"), equalTo("count count count [count]"));
		
		assertThat(test.pluginActionFromText("this bad"), equalTo("this [grammar|(is | really | (bad | hehe))]"));
		assertThat(test.pluginActionFromText("this"), equalTo("this [grammar|(is | really | (bad | hehe))]"));

	}

}
