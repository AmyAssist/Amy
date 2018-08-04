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

package de.unistuttgart.iaas.amyassist.amy.core.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;

/**
 * Tests for {@link de.unistuttgart.iaas.amyassist.amy.core.service.ServiceManagerImpl}.
 * 
 * @author Leon Kiefer
 */
class ServiceManagerImplTest {
	private DependencyInjection di;
	private RunnableServiceExtension runnableServiceExtension;

	@BeforeEach
	public void setup() {
		this.runnableServiceExtension = new RunnableServiceExtension();
		this.di = new DependencyInjection(this.runnableServiceExtension);
		this.di.register(ServiceManagerImpl.class);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.service.ServiceManagerImpl#start()}.
	 */
	@Test
	void testStart() {

		this.di.register(TestRunnableService.class);
		this.runnableServiceExtension.deploy();
		this.runnableServiceExtension.start();

		TestRunnableService service = this.di.getService(TestRunnableService.class);
		assertThat(service.run, is(true));
		this.runnableServiceExtension.stop();
		assertThat(service.run, is(false));
		assertThat(service.count, is(1));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.service.ServiceManagerImpl#start()}.
	 */
	@Test
	void testStartDouble() {
		this.runnableServiceExtension.deploy();
		this.runnableServiceExtension.start();
		assertThrows(IllegalStateException.class, () -> this.runnableServiceExtension.start());
	}

}
