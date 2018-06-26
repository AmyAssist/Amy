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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Test class for TextToPlugin mainly used to test the regex expressions feel free to test your plugin grammar here
 * 
 * @author Felix Burk
 */
public class TextToPluginTest {

	@Test
	void test() {
		Set<String> grammars = new HashSet<>();
		grammars.add("count count count [count]");
		grammars.add("say (hello|test) [xx|yy] (or|term)");
		grammars.add("play # [#] [#]");
		grammars.add("get devices");

		ArrayList<String> keywords = new ArrayList<>();
		keywords.add("testGrammar");
		keywords.add("testiGrammar");

		Set<String> grammars2 = new HashSet<>();
		grammars2.add("amy says [great|bad] (bluuub|blub) things");
		grammars2.add("search # in (artist|track|playlist|album)");
		grammars2.add("this [grammar|(is | really | (bad | hehe))]");

		ArrayList<String> keywords2 = new ArrayList<>();
		keywords2.add("keyword2");
		keywords2.add("keyword3");

		PluginGrammarInfo info1 = new PluginGrammarInfo(keywords, grammars);
		PluginGrammarInfo info2 = new PluginGrammarInfo(keywords2, grammars2);

		Set<PluginGrammarInfo> infos = new HashSet<>();
		infos.add(info1);
		infos.add(info2);

		TextToPlugin test = new TextToPlugin(infos);

		assertThat(test.pluginActionFromText("keyword2 amy says blub things").get(1),
				equalTo("amy says [great|bad] (bluuub|blub) things"));

		assertThat(test.pluginActionFromText("keyword3 amy says blub things").get(0), equalTo("keyword3"));

		assertThat(test.pluginActionFromText("jdwpojdpa keyword2 amy says blub things jdwopajd").get(1),
				equalTo("amy says [great|bad] (bluuub|blub) things"));
		assertThat(test.pluginActionFromText("jdwpojdpa keyword2 amy says blub things jdwopajd").get(0),
				equalTo("keyword2"));

		assertThat(test.pluginActionFromText("amy says great blub things"), equalTo(null));
		assertThat(test.pluginActionFromText("keyword3 amy says bluuub things").get(1),
				equalTo("amy says [great|bad] (bluuub|blub) things"));

		assertThat(test.pluginActionFromText("testGrammar play 380213910").get(1), equalTo("play # [#] [#]"));
		assertThat(test.pluginActionFromText("testiGrammar play "), equalTo(null));
		assertThat(test.pluginActionFromText("testiGrammar play one two three").get(1), equalTo("play # [#] [#]"));
		assertThat(test.pluginActionFromText("testiGrammar play one two three").get(3), equalTo("6"));

		assertThat(test.pluginActionFromText("testiGrammar play twenty two").get(3), equalTo("22"));

		assertThat(test.pluginActionFromText("testGrammar play one two three").get(0), equalTo("testGrammar"));

		assertThat(test.pluginActionFromText("testGrammar get devices").get(1), equalTo("get devices"));

		assertThat(test.pluginActionFromText("blah testiGrammar count count count count count blah").get(1),
				equalTo("count count count [count]"));

		assertThat(test.pluginActionFromText("keyword2 this bad").get(1),
				equalTo("this [grammar|(is | really | (bad | hehe))]"));
		assertThat(test.pluginActionFromText("keyword3 this").get(1),
				equalTo("this [grammar|(is | really | (bad | hehe))]"));

		assertThat(test.stringToNumber("sixty two djeopsjp two"), equalTo("62 djeopsjp 2"));
		assertThat(test.stringToNumber("fifty"), equalTo("50"));
		assertThat(test.stringToNumber("one"), equalTo("1"));
		assertThat(test.stringToNumber("ninety nine"), equalTo("99"));
		assertThat(test.stringToNumber("hdaiohd dwhaiodh a  adwa twenty two  dad  dwa a wa"),
				equalTo("hdaiohd dwhaiodh a  adwa 22  dad  dwa a wa"));
		assertThat(test.stringToNumber("nananananana zero jdiwapja jwpoa nanananan one"),
				equalTo("nananananana 0 jdiwapja jwpoa nanananan 1"));

	}

}
