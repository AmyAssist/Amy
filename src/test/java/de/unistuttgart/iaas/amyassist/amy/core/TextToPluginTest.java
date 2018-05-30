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
import java.util.List;

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
		ArrayList<String> grammars = new ArrayList<>();
		grammars.add("count count count [count]");
		grammars.add("say (hello|test) [xx|yy] (or|term)");
		grammars.add("play #");
		grammars.add("get devices");
		
		ArrayList<String> keywords = new ArrayList<>();
		keywords.add("testGrammar");
		keywords.add("testiGrammar");
		
		ArrayList<String> grammars2 = new ArrayList<>();
		grammars2.add("amy says [great|bad] (bluuub|blub) things");
		grammars2.add("search # in (artist|track|playlist|album)");
		grammars2.add("this [grammar|(is | really | (bad | hehe))]");
		
		ArrayList<String> keywords2 = new ArrayList<>();
		keywords2.add("keyword2");
		keywords2.add("keyword3");
		
		PluginGrammarInfo info1 = new PluginGrammarInfo(keywords, grammars);
		PluginGrammarInfo info2 = new PluginGrammarInfo(keywords2, grammars2);
		
		List<PluginGrammarInfo> infos = new ArrayList<PluginGrammarInfo>();
		infos.add(info1);
		infos.add(info2);
		
		TextToPlugin test = new TextToPlugin(infos);
		
		assertThat(test.pluginActionFromText("keyword2 amy says blub things")[1], equalTo("amy says [great|bad] (bluuub|blub) things"));
		assertThat(test.pluginActionFromText("keyword3 amy says blub things")[0], equalTo("keyword3"));

		assertThat(test.pluginActionFromText("jdwpojdpa keyword2 amy says blub things jdwopajd")[1], equalTo("amy says [great|bad] (bluuub|blub) things"));
		assertThat(test.pluginActionFromText("jdwpojdpa keyword2 amy says blub things jdwopajd")[0], equalTo("keyword2"));

		assertThat(test.pluginActionFromText("amy says great blub things"), equalTo(null));
		assertThat(test.pluginActionFromText("keyword3 amy says bluuub things")[1], equalTo("amy says [great|bad] (bluuub|blub) things"));

		assertThat(test.pluginActionFromText("testGrammar play 380213910")[1], equalTo("play #"));
		assertThat(test.pluginActionFromText("testiGrammar play "),  equalTo(null));
		assertThat(test.pluginActionFromText("testiGrammar play one two three")[1],  equalTo("play #"));
		assertThat(test.pluginActionFromText("testGrammar play one two three")[0],  equalTo("testGrammar"));

		assertThat(test.pluginActionFromText("testGrammar get devices")[1], equalTo("get devices"));

		assertThat(test.pluginActionFromText("blah testiGrammar count count count count count blah")[1], equalTo("count count count [count]"));
		
		assertThat(test.pluginActionFromText("keyword2 this bad")[1], equalTo("this [grammar|(is | really | (bad | hehe))]"));
		assertThat(test.pluginActionFromText("keyword3 this")[1], equalTo("this [grammar|(is | really | (bad | hehe))]"));
		
		assertThat(test.stringToNumber("sixty two"), equalTo(62));
		assertThat(test.stringToNumber("fifty"), equalTo(50));
		assertThat(test.stringToNumber("one"), equalTo(1));
		assertThat(test.stringToNumber("ninety nine"), equalTo(99));
		assertThat(test.stringToNumber("hdaiohd dwhaiodh a  adwa twenty two  dad  dwa a wa"), equalTo(22));
		assertThat(test.stringToNumber("nananananana zero jdiwapja jwpoa nanananan"), equalTo(0));


	}

}
