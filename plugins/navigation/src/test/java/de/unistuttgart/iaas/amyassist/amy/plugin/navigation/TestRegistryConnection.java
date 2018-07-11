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

package de.unistuttgart.iaas.amyassist.amy.plugin.navigation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationRegistry;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for the registry connection
 * 
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class TestRegistryConnection {

	@Reference
	private TestFramework testFramework;

	private LocationRegistry registry;

	private RegistryConnection connection;

	@Mock
	private Location location;

	@Mock
	private Location location2;

	@BeforeEach
	public void init() {
		this.registry = this.testFramework.mockService(LocationRegistry.class);
		this.connection = this.testFramework.setServiceUnderTest(RegistryConnection.class);
	}

	@Test
	public void testGetAdressHome() {
		when(this.location.getAddressString()).thenReturn("Pforzheim");
		when(this.registry.getHome()).thenReturn(this.location);
		assertThat(this.connection.getAdress("Home"), equalTo("Pforzheim"));
	}

	@Test
	public void testGetAdressWork() {
		when(this.location.getAddressString()).thenReturn("Pforzheim");
		when(this.registry.getWork()).thenReturn(this.location);
		assertThat(this.connection.getAdress("work"), equalTo("Pforzheim"));
	}

	@Test
	public void testGetAdressOther() {
		when(this.location.getAddressString()).thenReturn("Pforzheim");
		when(this.location.getName()).thenReturn("friend");
		List<Location> locations = new ArrayList<>();
		locations.add(this.location2);
		locations.add(this.location);
		when(this.location2.getName()).thenReturn("other");
		when(this.registry.getAll()).thenReturn(locations);
		assertThat(this.connection.getAdress("friend"), equalTo("Pforzheim"));
	}

	@Test
	public void testGetAdressNotInRegistry() {
		when(this.location.getName()).thenReturn("friend");
		List<Location> locations = new ArrayList<>();
		locations.add(this.location2);
		locations.add(this.location);
		when(this.location2.getName()).thenReturn("other");
		when(this.registry.getAll()).thenReturn(locations);
		assertThat(this.connection.getAdress("bla"), equalTo(null));
	}
}
