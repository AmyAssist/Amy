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

package io.github.amyassist.amy.plugin.spotify.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity;

/**
 * Test case for DeviceEntity
 * 
 * @author Muhammed Kaya, Lars Buttgereit
 */
class DeviceEntityTest {

	private DeviceEntity deviceEntity;

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#Device(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	@BeforeEach
	void init() {
		this.deviceEntity = new DeviceEntity("Smartphone", "Hello", "abc123");

	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#getType()}.
	 */
	@Test
	void testGetType() {
		assertThat(this.deviceEntity.getType(), equalTo("Smartphone"));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#setType(java.lang.String)}.
	 */
	@Test
	void testSetType() {
		this.deviceEntity.setType("Computer");
		assertThat(this.deviceEntity.getType(), equalTo("Computer"));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#getName()}.
	 */
	@Test
	void testGetName() {
		assertThat(this.deviceEntity.getName(), equalTo("Hello"));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#setName(java.lang.String)}.
	 */
	@Test
	void testSetName() {
		this.deviceEntity.setName("GoodBye");
		assertThat(this.deviceEntity.getName(), equalTo("GoodBye"));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#getID()}.
	 */
	@Test
	void testGetUri() {
		assertThat(this.deviceEntity.getID(), equalTo("abc123"));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#setID(java.lang.String)}.
	 */
	@Test
	void testSetUri() {
		this.deviceEntity.setID("123abc");
		assertThat(this.deviceEntity.getID(), equalTo("123abc"));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#equals(Object)}.
	 */
	@Test
	public void testEqualsSameObject() {
		assertThat(this.deviceEntity.equals(this.deviceEntity), equalTo(true));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#equals(Object)}.
	 */
	@Test
	public void testEqualsOtherClass() {
		assertThat(this.deviceEntity.equals(""), equalTo(false));
		assertThat(this.deviceEntity.equals(null), equalTo(false));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#equals(Object)}.
	 */
	@Test
	public void testEquals() {
		DeviceEntity deviceEntity2 = new DeviceEntity("Smartphone", "Hello", "abc123");
		assertThat(this.deviceEntity.equals(deviceEntity2), equalTo(true));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.entities.DeviceEntity#equals(Object)}.
	 */
	@Test
	public void testEqualsNotEqual() {
		DeviceEntity deviceEntity2 = new DeviceEntity("Smartphone", "Hello", "123");
		assertThat(this.deviceEntity.equals(deviceEntity2), equalTo(false));
		DeviceEntity deviceEntity3 = new DeviceEntity("Smartphone", "Hallo", "123");
		assertThat(this.deviceEntity.equals(deviceEntity3), equalTo(false));
		DeviceEntity deviceEntity4 = new DeviceEntity("Computer", "Hallo", "123");
		assertThat(this.deviceEntity.equals(deviceEntity4), equalTo(false));
	}

}
