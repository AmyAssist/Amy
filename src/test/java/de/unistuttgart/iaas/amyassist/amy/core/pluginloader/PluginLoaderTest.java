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

package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for PluginLoader
 * 
 * @author Leon Kiefer
 */
class PluginLoaderTest {

	private PluginLoader pluginLoader;

	@BeforeEach
	void setup() {
		this.pluginLoader = new PluginLoader();
	}

	@Test
	void test() {
		this.pluginLoader.loadPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example", "amy.plugin.example",
				"de.unistuttgart.iaas.amyassist", "0.0.1");
		assertThat(this.pluginLoader.getPlugins(), hasSize(1));
		assertThat(this.pluginLoader.getPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example"), notNullValue());
	}

	@Test
	void testClasses() {
		this.pluginLoader.loadPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example", "amy.plugin.example",
				"de.unistuttgart.iaas.amyassist", "0.0.1");
		Plugin plugin = this.pluginLoader.getPlugin("de.unistuttgart.iaas.amyassist.amy.plugin.example");
		assertThat(plugin.getClasses(), is(not(empty())));
	}
}
