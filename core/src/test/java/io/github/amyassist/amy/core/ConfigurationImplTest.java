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

package io.github.amyassist.amy.core;

import static io.github.amyassist.amy.core.pluginloader.PluginMockFactory.plugin;
import static io.github.amyassist.amy.core.pluginloader.PluginMockFactory.withUniqueName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.arrayWithSize;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.pluginloader.IPlugin;
import io.github.amyassist.amy.core.pluginloader.PluginManager;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Tests for the ConfigurationImpl
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
class ConfigurationImplTest {

	@Reference
	private TestFramework framework;
	private ConfigurationImpl configurationImpl;

	@BeforeEach
	public void setup() {
		PluginManager mockService = this.framework.mockService(PluginManager.class);
		List<IPlugin> hashSet = new ArrayList<>();
		hashSet.add(plugin(withUniqueName("Plugin1")));
		hashSet.add(plugin(withUniqueName("Plugin2")));
		hashSet.add(plugin(withUniqueName("io.github.amyassist.amy.plugin.example")));
		Mockito.when(mockService.getPlugins()).thenReturn(hashSet);

		this.configurationImpl = this.framework.setServiceUnderTest(ConfigurationImpl.class);
	}

	@Test
	void test() {
		assertThat(this.configurationImpl.getInstalledPlugins(),
				arrayContainingInAnyOrder("Plugin1", "Plugin2", "io.github.amyassist.amy.plugin.example"));
		assertThat(this.configurationImpl.getInstalledPlugins(), arrayWithSize(3));
	}

}
