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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for DeviceEntity
 * 
 * @author Muhammed Kaya
 */
class DeviceEntityTest {
	
	private DeviceEntity deviceEntity;

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity#Device(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test @BeforeEach
	void init() {
		this.deviceEntity = new DeviceEntity("Smartphone", "Hello", "abc123");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity#getType()}.
	 */
	@Test
	void testGetType() {
		assertThat(this.deviceEntity.getType(), equalTo("Smartphone"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity#setType(java.lang.String)}.
	 */
	@Test
	void testSetType() {
		this.deviceEntity.setType("Computer");
		assertThat(this.deviceEntity.getType(), equalTo("Computer"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity#getName()}.
	 */
	@Test
	void testGetName() {
		assertThat(this.deviceEntity.getName(), equalTo("Hello"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity#setName(java.lang.String)}.
	 */
	@Test
	void testSetName() {
		this.deviceEntity.setName("GoodBye");
		assertThat(this.deviceEntity.getName(), equalTo("GoodBye"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity#getID()}.
	 */
	@Test
	void testGetID() {
		assertThat(this.deviceEntity.getID(), equalTo("abc123"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity#setID(java.lang.String)}.
	 */
	@Test
	void testSetID() {
		this.deviceEntity.setID("123abc");
		assertThat(this.deviceEntity.getID(), equalTo("123abc"));
	}
	
}
