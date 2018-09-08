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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.userinteraction;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.PluginMockFactory;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.natlang.aim.XMLAmyInteractionModel;
import de.unistuttgart.iaas.amyassist.amy.natlang.userinteraction.LoadAIMService;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for the amy interaction model service class
 * 
 * @author Lars Buttgereit, Felix Burk
 */
@ExtendWith(FrameworkExtension.class)
class TestAIMService {

	@Reference
	private TestFramework testFramework;

	private LoadAIMService loadAIMService;
	private XMLAmyInteractionModel interactionModel;

	@BeforeEach
	void init() throws FileNotFoundException {
		this.testFramework.mockService(PluginManager.class);
		this.testFramework.mockService(NLProcessingManager.class);

		this.loadAIMService = this.testFramework.setServiceUnderTest(LoadAIMService.class);
		InputStream stream = this.getClass().getResourceAsStream("HelloWorldSpeech.aim.xml");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		this.interactionModel = this.loadAIMService.extractModel(reader.lines().collect(Collectors.joining()),
				"HelloWorldSpeech.aim.xml");

	}

	@Test
	void testMetaFile() {
		
		//correct meta file
		IPlugin p = PluginMockFactory.plugin();
		InputStream streamMeta = this.getClass().getResourceAsStream("testPlugin.natlangMeta");

		try {
			assertEquals(false, this.loadAIMService.readMetaFile(streamMeta, p).isEmpty());
		} catch (Exception e) {
			fail("exception was thrown " + e.getMessage());
		}
	}
	
	@ParameterizedTest
	@MethodSource("badMetaFileContent")
	void testBadMetaFile(String content) {
		IPlugin p = PluginMockFactory.plugin();
		
		InputStream streamMetaBad = new ByteArrayInputStream(content.getBytes());
		assertEquals(true ,this.loadAIMService.readMetaFile(streamMetaBad, p).isEmpty());
	}
	
	static Stream<String> badMetaFileContent() {
		return Stream.of("", " ", " # blub", " .aims: \\n", ".bad: \\n wrong.aim.xml", "something bad");
	}

	@Test
	void testPrintSelf() {
		StringBuilder builder = new StringBuilder();
		builder = builder.append(
				"\n Intent ref=de.unistuttgart.iaas.amyassist.amy.plugin.example.HelloWorldSpeech.sayHelloXTimes"
						+ " gram= say {greeting} {number} \n ")
				.append("Entity id=number\n ").append("Entity id=greeting grammar= hello\n ")
				.append("Prompt text= how many times should i say {greeting}?  gram= {number} [times] ");
		assertThat(this.interactionModel.printSelf(), equalTo(builder.toString()));
	}

	@Test
	void testReference() {
		assertThat(this.interactionModel.getIntents().get(0).getReference(),
				equalTo("de.unistuttgart.iaas.amyassist.amy.plugin.example.HelloWorldSpeech.sayHelloXTimes"));
	}

	@Test
	void testGram() {
		assertThat(this.interactionModel.getIntents().get(0).getGram().trim(), equalTo("say {greeting} {number}"));
	}

	@Test
	void testTemplateEntityId() {
		assertThat(this.interactionModel.getIntents().get(0).getTemplates().get(0).getEntityId(), equalTo("number"));
	}

	@Test
	void testPromptText() {
		assertThat(this.interactionModel.getIntents().get(0).getPrompts().get(0).getText(),
				equalTo(" how many times should i say {greeting}? "));
	}

	@Test
	void testPromptGram() {
		assertThat(this.interactionModel.getIntents().get(0).getPrompts().get(0).getGram(),
				equalTo(" {number} [times] "));
	}

}
