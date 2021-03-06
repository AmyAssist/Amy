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

package io.github.amyassist.amy.core.natlang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.natlang.Dialog;
import io.github.amyassist.amy.natlang.NLProcessingManager;
import io.github.amyassist.amy.natlang.NLProcessingManagerImpl;
import io.github.amyassist.amy.natlang.aim.XMLAIMIntent;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

@ExtendWith(FrameworkExtension.class)
public class NLProcessingManagerTest {

	@Reference
	private TestFramework testFramework;

	private NLProcessingManager manager;

	private List<String> failedToUnderstand = new ArrayList<>();

	private List<String> quitIntent = new ArrayList<>();

	String check;

	@BeforeEach
	public void setup() {
		Environment env = this.testFramework.mockService(Environment.class);
		ConfigurationManager loader = this.testFramework.mockService(ConfigurationManager.class);

		Properties prop = new Properties();
		prop.setProperty("enableStemmer", "false");

		when(loader.getConfigurationWithDefaults(Mockito.anyString())).thenReturn(prop);

		this.failedToUnderstand.add("I did not understand that");
		this.failedToUnderstand.add("Sorry, could you repeat that?");
		this.failedToUnderstand.add("I don't know what you mean");
		this.failedToUnderstand.add("No idea what you are talking about");
		this.failedToUnderstand.add("My plugin developers did not teach me this yet");

		this.quitIntent.addAll(Arrays.asList(new String[] { "ok", "sure", "what else can i do for you?" }));

		this.manager = this.testFramework.setServiceUnderTest(NLProcessingManagerImpl.class);

	}

	@Test
	public void test() {
		Dialog dialog = new Dialog(this::consumerMethodForDialog);
		XMLAIMIntent intent = new XMLAIMIntent();
		this.manager.decideIntent(dialog, "test");
		assertEquals(true, this.failedToUnderstand.contains(this.check));

		this.manager.processIntent(dialog, "never mind");
		assertEquals(true, this.quitIntent.contains(this.check));
	}

	private void consumerMethodForDialog(Response response) {
		this.check = response.getText();
	}

}
