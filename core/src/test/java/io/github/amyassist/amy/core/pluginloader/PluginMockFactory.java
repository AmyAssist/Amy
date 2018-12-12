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

package io.github.amyassist.amy.core.pluginloader;

import java.nio.file.Path;
import java.util.function.Consumer;

import org.mockito.Mockito;

/**
 * Factory methods to create Plugin mocks with different properties. Minimal effort and maximal readable in the factory
 * user code.
 * 
 * @author Leon Kiefer
 */
public class PluginMockFactory {
	static IPlugin plugin() {
		return Mockito.mock(IPlugin.class);
	}

	public static IPlugin plugin(Path path) {
		IPlugin plugin = plugin();
		Mockito.when(plugin.getPath()).thenReturn(path);
		return plugin;
	}

	public static IPlugin plugin(ClassLoader classLoader) {
		IPlugin plugin = plugin();
		Mockito.when(plugin.getClassLoader()).thenReturn(classLoader);
		return plugin;
	}

	@SafeVarargs
	public static IPlugin plugin(Consumer<IPlugin>... modifiers) {
		IPlugin plugin = plugin();
		for (Consumer<IPlugin> modifier : modifiers) {
			modifier.accept(plugin);
		}

		return plugin;
	}

	public static Consumer<IPlugin> withUniqueName(String uniqueName) {
		return plugin -> Mockito.when(plugin.getUniqueName()).thenReturn(uniqueName);
	}
}
