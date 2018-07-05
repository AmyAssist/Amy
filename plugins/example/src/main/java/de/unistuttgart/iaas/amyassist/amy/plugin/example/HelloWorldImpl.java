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

	@Override
	public String testContactRegistry() {
		try {
			contacts.testMyself();
			return "Tests successful";
		} catch (RuntimeException e) {
			logger.warn("Registry test failed: ", e);
			return "Tests failed: " + e.getLocalizedMessage();
		}
	}
}
