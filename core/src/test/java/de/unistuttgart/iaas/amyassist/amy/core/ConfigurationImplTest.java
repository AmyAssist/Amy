/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;

/**
 * Tests for the ConfigurationImpl
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtention.class)
class ConfigurationImplTest {

	@Reference
	private TestFramework framework;
	private ConfigurationImpl configurationImpl;

	@BeforeEach
	public void setup() {
		PluginLoader mockService = this.framework.mockService(PluginLoader.class);
		Set<String> hashSet = new HashSet<>();
		hashSet.add("Plugin1");
		hashSet.add("Plugin2");
		hashSet.add("de.unistuttgart.iaas.amyassist.amy.plugin.example");
		Mockito.when(mockService.getPluginNames()).thenReturn(hashSet);

		this.configurationImpl = this.framework.setServiceUnderTest(ConfigurationImpl.class);
	}

	@Test
	void test() {
		assertThat(this.configurationImpl.getInstalledPlugins(),
				arrayContainingInAnyOrder("Plugin1", "Plugin2", "de.unistuttgart.iaas.amyassist.amy.plugin.example"));
		assertThat(this.configurationImpl.getInstalledPlugins(), arrayWithSize(3));
	}

}
