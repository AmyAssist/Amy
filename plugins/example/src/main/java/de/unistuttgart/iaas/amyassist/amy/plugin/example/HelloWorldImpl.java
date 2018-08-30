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

package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import de.unistuttgart.iaas.amyassist.amy.plugin.example.registry.ColorEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.registry.ColorRegistry;
import de.unistuttgart.iaas.amyassist.amy.registry.*;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.api.HelloWorldService;

import java.util.List;

/**
 * Does the logic of the Hello World plugin
 * 
 * @author Tim Neumann, Benno Krauß
 */
@Service
public class HelloWorldImpl implements HelloWorldService {
	private static final String KEY = "hellocount";

	@Reference
	private Logger logger;

	/**
	 * A reference to the storage.
	 */
	@Reference
	protected IStorage storage;

	@Reference
	private ContactRegistry contacts;

	@Reference
	private LocationRegistry locationRegistry;

	@Reference
	private ColorRegistry colorRegistry;

	@Override
	public String helloWorld() {
		if (!this.storage.has(KEY)) {
			this.logger.debug("init storage");
			this.storage.put(KEY, "0");
		}
		int count = Integer.parseInt(this.storage.get(KEY));
		count++;

		String countString = String.valueOf(count);
		this.logger.debug("put {} into storage", countString);
		this.storage.put(KEY, countString);

		return "hello " + countString;
	}

	@Override
	public String helloWorldXTimes(int times) {
		this.logger.debug("helloWorldXTimes called with {}", times);

		StringBuilder hellos = new StringBuilder();

		for (int i = 0; i < times; i++) {
			hellos.append("hello ");
		}

		return hellos.toString().trim();
	}

	/**
	 * Show all contacts
	 * @return human-readable text
	 */
	@Override
	public String demonstrateContactRegistry() {
		List<? extends Contact> contactsList = contacts.getAll();
		StringBuilder b = new StringBuilder("All contacts:\n");
		for (Contact c: contactsList) {
			b.append(c.getFirstName()).append(" ").append(c.getLastName()).append(" ")
					.append(c.isImportant() ? "important" : "unimporant").append(" person").append("\n");
		}
		return b.toString();
	}

	/**
	 * Exception class to signalize bad test result
	 */
	private class TestException extends Exception {
		/**
		 * Classic exception constructor
		 * @param message exception message
		 */
		public TestException(String message) {
			super(message);
		}
	}

	/**
	 * Test the contact registry's functionality
	 * @return human-readable text
	 */
	@Override
	public String testContactRegistry() {
		return performTest(() -> {
			// Test creation
			Contact personA = contacts.createNewEntity();
			personA.setEmail("a@b.c");
			personA.setFirstName("Max");
			personA.setLastName("Mustermann");
			personA.setImportant(true);

			Contact personB = contacts.createNewEntity();
			personB.setEmail("b@b.com");
			personB.setFirstName("Alice");
			personB.setLastName("Musterfrau");
			personB.setImportant(true);

			assertTrue(personA.getPersistentId() == 0);

			contacts.save(personA);
			contacts.save(personB);

			// Make sure primary keys are set
			assertTrue(personA.getPersistentId() != personB.getPersistentId());

			// Make sure getAll works
			List<Contact> list = contacts.getAll();
			assertTrue(list.contains(personA));
			assertTrue(list.contains(personB));

			// Test update functionality
			personA.setEmail("x@y.z");
			contacts.save(personA);
			assertTrue(contacts.getById(personA.getPersistentId()).equals(personA));

			int personAId = personA.getPersistentId();

			// Test retrieval
			Contact personA2 = contacts.getById(personA.getPersistentId());
			assertTrue(personA.equals(personA2));

			// Test deletion
			contacts.deleteById(personA.getPersistentId());
			contacts.deleteById(personB.getPersistentId());

			Contact c3 = contacts.getById(personAId);
			assertTrue(c3 == null);
		});
	}

	@Override
	public String testLocationRegistry() {
		return performTest(() -> {
			// Test that there are no locations to begin with
			List<Location> pois = locationRegistry.getAll();
			assertTrue(pois.isEmpty());

			// Test creation of a location
			Location work = locationRegistry.createNewEntity();
			work.setCity("Stuttgart");
			work.setZipCode("70563");
			work.setStreet("Universitätsstraße");
			work.setHouseNumber(38);
			work.setLongitude(9.106600);
			work.setLatitude(48.745172);
			work.setTag(Tags.WORK);
			work.setName("Uni");

			// Test getAddressString method
			assertTrue(work.getAddressString().equals("Universitätsstraße 38, 70563 Stuttgart"));

			// Test entity saving
			locationRegistry.save(work);

			assertTrue(locationRegistry.getById(work.getPersistentId()).equals(work));

			// Test getWork method
			assertTrue(locationRegistry.getWork().equals(work));

			// Test deletion
			locationRegistry.deleteById(work.getPersistentId());

			assertTrue(locationRegistry.getWork() == null);
		});
	}

	@Override
	public String testCustomRegistry() {
		return performTest(() -> {
			// Probe if color registry is empty
			assertTrue(colorRegistry.getAll().isEmpty());

			// Create new entity, save entity
			ColorEntity c1 = colorRegistry.createNewEntity();
			c1.setRedComponent(0.1f);
			c1.setGreenComponent(0.1f);
			c1.setBlueComponent(0.1f);

			colorRegistry.save(c1);

			// Entity retrieval works
			assertTrue(colorRegistry.getById(c1.getPersistentId()).equals(c1));

			assertTrue(colorRegistry.getAll().size() == 1);

			// Entity deletion works
			colorRegistry.deleteById(c1.getPersistentId());

			assertTrue(colorRegistry.getAll().isEmpty());

			assertTrue(colorRegistry.getById(c1.getPersistentId()) == null);
		});
	}

	@FunctionalInterface
	private interface Test {
		void test() throws HelloWorldImpl.TestException;
	}

	private String performTest(Test test) {
		try {
			test.test();
			return "Test successful";
		} catch (TestException e) {
			logger.warn("Test failed: ",e);
			return "Tests failed: " + e.getMessage();
		}
	}

	/**
	 * Tiny function for testing
	 * @param b expression to be tested
	 */
	private void assertTrue(boolean b) throws TestException {
		if (!b) {
			throw new TestException("Error in test");
		}
	}
}
