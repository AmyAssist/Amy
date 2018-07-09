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

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.Contact;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.ContactRegistry;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.registry.LocationRegistry;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.api.HelloWorldService;

import java.util.List;

/**
 * Does the logic of the Hello World plugin
 * 
 * @author Tim Neumann
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

		return "hello" + countString;
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
		try {
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

			assertTrue(personA.getId() == 0);

			contacts.save(personA);
			contacts.save(personB);

			assertTrue(personA.getId() != personB.getId());

			List<Contact> list = contacts.getAll();
			assertTrue(list.contains(personA));
			assertTrue(list.contains(personB));

			int personAId = personA.getId();

			Contact personA2 = contacts.getById(personA.getId());
			assertTrue(personA.equals(personA2));

			contacts.deleteById(personA.getId());
			contacts.deleteById(personB.getId());

			Contact c3 = contacts.getById(personAId);
			assertTrue(c3 == null);
			return "Tests successful";
		} catch (TestException e) {
			logger.warn("Registry test failed: ", e);
			return "Tests failed: " + e.getMessage();
		}
	}

	@Override
	public String testLocationRegistry() {
		try {
			List<Location> pois = locationRegistry.getAll();
			assertTrue(pois.isEmpty());

			Location work = locationRegistry.createNewEntity();
			work.setCity("Stuttgart");
			work.setZipCode("70563");
			work.setStreet("Universitätsstraße");
			work.setHouseNumber(38);
			work.setLongitude(9.106600);
			work.setLatitude(48.745172);
			work.setWork(true);

			locationRegistry.save(work);

			assertTrue(locationRegistry.getById(work.getId()).equals(work));

			assertTrue(locationRegistry.getWork().equals(work));

			locationRegistry.deleteById(work.getId());

			assertTrue(locationRegistry.getWork() == null);

			return "Test successful";
		} catch (TestException e) {
			logger.warn("Location registry test failed: ",e);
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
