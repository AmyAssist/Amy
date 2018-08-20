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

package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests the Plugin class
 * 
 * @author Tim Neumann
 */
class PluginTest {

	/**
	 * Create a mock Manifest and stub the Attributes with a modifier
	 * 
	 * @param modifier
	 *            a function that gets called with the Attributes mock and can stub the behavior
	 * @return the manifest
	 */
	static private Manifest manifest(Consumer<Attributes> modifier) {
		Manifest mf = mock(Manifest.class);
		Attributes attributes = mock(Attributes.class);
		when(mf.getMainAttributes()).thenReturn(attributes);
		modifier.accept(attributes);
		return mf;
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#setPath(Path)} and
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getPath()}.
	 */
	@ParameterizedTest
	@MethodSource("paths")
	void testPath(Path path) {
		Plugin p = new Plugin();

		p.setPath(path);
		assertThat("Wrong path", p.getPath(), is(path));
	}

	static Stream<Path> paths() {
		return Stream.of("/", "/test", "C://test", "test").map(path -> Paths.get(path));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#setClassLoader(ClassLoader)}
	 * and {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getClassLoader()}.
	 */
	@Test
	void testClassLoader() {
		Plugin p = new Plugin();
		ClassLoader classLoader = mock(ClassLoader.class);
		p.setClassLoader(classLoader);
		assertThat("Wrong class loader", p.getClassLoader(), is(theInstance(classLoader)));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getUniqueName()}.
	 */
	@ParameterizedTest
	@MethodSource("names")
	void testUniqueName(String pluginId) {
		Plugin p = new Plugin();
		Manifest mf = manifest(attributes -> when(attributes.getValue("PluginID")).thenReturn(pluginId));
		p.setManifest(mf);

		assertThat("Wrong unqiue name", p.getUniqueName(), is(pluginId));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getUniqueName()}.
	 */
	@ParameterizedTest
	@MethodSource("names")
	void testDispalyName(String projectName) {
		Plugin p = new Plugin();
		Manifest mf = manifest(
				attributes -> when(attributes.getValue(Name.IMPLEMENTATION_TITLE)).thenReturn(projectName));
		p.setManifest(mf);

		assertThat("Wrong diplay name", p.getDisplayName(), is(projectName));
	}

	static Stream<String> names() {
		return Stream.concat(new Random().ints(100).mapToObj(number -> "Name" + number),
				Stream.of("my name", "template", "NaN", ""));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getVersion()}.
	 */
	@ParameterizedTest
	@MethodSource("versions")
	void testVersion(String projectVersion) {
		Plugin p = new Plugin();

		Manifest mf = manifest(
				attributes -> when(attributes.getValue(Name.IMPLEMENTATION_VERSION)).thenReturn(projectVersion));

		p.setManifest(mf);

		assertThat("Wrong Version", p.getVersion(), is(projectVersion));
	}

	static Stream<String> versions() {
		return Stream.concat(new Random().ints(100).mapToObj(number -> "Version" + number),
				Stream.of("0", "no version"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#setClasses(java.util.List)}
	 * and {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getClasses()}.
	 */
	@Test
	void testClasses() {
		List<Class<?>> list = new ArrayList<>();
		Plugin p = new Plugin();
		list.add(this.getClass());
		list.add(Plugin.class);
		p.setClasses(list);
		assertThat("Wrong list of classes", p.getClasses(), contains(this.getClass(), Plugin.class));
		assertThat("Wrong list of classes", p.getClasses(), hasSize(list.size()));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getManifest()}.
	 */
	@Test
	void testManifest() {
		Manifest mf = mock(Manifest.class);
		Plugin p = new Plugin();
		p.setManifest(mf);
		assertThat("Wrong manifest", p.getManifest(), is(mf));
	}

}
