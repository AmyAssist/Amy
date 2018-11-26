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

package io.github.amyassist.amy.core.di.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import io.github.amyassist.amy.core.di.DependencyInjection;
import io.github.amyassist.amy.core.di.Service1;

/**
 * Test for {@link Extension} and the dependency injection interaction
 * 
 * @author Leon Kiefer
 */
class ExtensionTest {

	private DependencyInjection dependencyInjection;
	private Extension mockExtension;

	@BeforeEach
	public void setup() {
		this.mockExtension = Mockito.mock(Extension.class);

		this.dependencyInjection = new DependencyInjection(this.mockExtension);
	}

	@Test
	void testPostConstruct() {
		Mockito.verify(this.mockExtension).postConstruct(this.dependencyInjection);
	}

	@Test
	void testOnRegister() {
		this.dependencyInjection.getConfiguration().register(Service1.class);

		Mockito.verify(this.mockExtension).onRegister(ArgumentMatchers.any(), ArgumentMatchers.eq(Service1.class));
	}

	@Test
	void testOnRegisterFromService() {
		this.dependencyInjection.getConfiguration().register(ServiceConfigurationChange.class);
		Mockito.verify(this.mockExtension).onRegister(ArgumentMatchers.any(),
				ArgumentMatchers.eq(ServiceConfigurationChange.class));
		this.dependencyInjection.getServiceLocator().getService(ServiceConfigurationChange.class).install();

		Mockito.verify(this.mockExtension).onRegister(ArgumentMatchers.any(), ArgumentMatchers.eq(Service1.class));
	}
}
