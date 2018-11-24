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

package io.github.amyassist.amy.deployment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.amyassist.amy.deployment.classes.Class1;
import io.github.amyassist.amy.deployment.classes.Class2;
import io.github.amyassist.amy.deployment.classes.Class3;

/**
 * Test different deployment descriptors
 * 
 * @author Leon Kiefer
 */
class DeploymentDescriptorUtilTest {

	private ClassLoader classLoader;

	@BeforeEach
	void init() {
		this.classLoader = this.getClass().getClassLoader();
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.deployment.DeploymentDescriptorUtil#getClasses(java.lang.ClassLoader, java.lang.String)}.
	 */
	@Test
	void testGetClasses() {
		assertThat(DeploymentDescriptorUtil.getClasses(this.classLoader, "normalDD"),
				containsInAnyOrder(Class1.class, Class2.class, Class3.class));
	}

	@Test
	void testEmptyDeploymentDescriptorGetClasses() {
		assertThat(DeploymentDescriptorUtil.getClasses(this.classLoader, "emptyDD"), is(empty()));
	}

	@Test
	void testNoDD() {
		assertThat(DeploymentDescriptorUtil.getClasses(this.classLoader, "noDD"), is(empty()));
	}

	@Test
	void testnewLineDD() {
		assertThat(DeploymentDescriptorUtil.getClasses(this.classLoader, "newLineDD"), contains(Class1.class));
	}

	@Test
	void testnoNewLineDD() {
		assertThat(DeploymentDescriptorUtil.getClasses(this.classLoader, "noNewLineDD"), contains(Class1.class));
	}

	@Test
	void testwrongClassNameDD() {
		assertThrows(IllegalArgumentException.class, () -> {
			DeploymentDescriptorUtil.getClasses(this.classLoader, "wrongClassNameDD");
		});
	}

	@Test
	void testIOExceptionDD() throws IOException {
		ClassLoader mock = Mockito.mock(ClassLoader.class);
		Mockito.when(mock.getResources("normalDD")).thenThrow(IOException.class);
		assertThrows(IllegalStateException.class, () -> {
			DeploymentDescriptorUtil.getClasses(mock, "normalDD");
		});
	}
}
