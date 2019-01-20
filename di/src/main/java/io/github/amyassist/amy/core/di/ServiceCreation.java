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

package io.github.amyassist.amy.core.di;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import io.github.amyassist.amy.core.di.provider.ServiceHandle;

/**
 * Information about the creation process of a Service
 * 
 * @author Leon Kiefer
 * @param <T>
 *            the type of the created service
 */
public class ServiceCreation<T> {
	CompletableFuture<ServiceHandle<T>> completableFuture;
	private final Set<ServiceCreation<?>> dependents = new HashSet<>();
	private final Set<ServiceCreation<?>> dependencies = new HashSet<>();
	private final String name;

	/**
	 * @param name
	 *            the name for the debugging
	 * 
	 */
	ServiceCreation(String name) {
		this.name = name;
	}

	/**
	 * Check if the given ServiceCreation is transitive dependent on this. This check is important to prevent circles in
	 * the data structure.
	 * 
	 * @param serviceCreation
	 *            the given ServiceCreation
	 * @return true if the given serviceCreation depends on this
	 */
	boolean isDependent(ServiceCreation<?> serviceCreation) {
		if (this.dependents.contains(serviceCreation)) {
			return true;
		}
		for (ServiceCreation<?> dependent : this.dependents) {
			if (dependent.isDependent(serviceCreation)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a other ServiceCreationInfo as a dependent ServiceCreationInfo to this.
	 * 
	 * @param dependent
	 *            the ServiceCreationInfo that depends on this ServiceCreationInfo
	 */
	void addDependent(ServiceCreation<?> dependent) {
		if (dependent.isDependent(this)) {
			throw new IllegalStateException("circular dependencies");
		}

		this.dependents.add(dependent);
		dependent.dependencies.add(this);
	}

	@Override
	public String toString() {
		return "Dependencies:\n" + this.printDependencies() + "\nDependents:\n" + this.printDependents();
	}

	/**
	 * Create a tree of the dependents.
	 * 
	 * @return the tree as string to print to the console
	 */
	public String printDependents() {
		return this.printDependents("\n");
	}

	private String printDependents(String prefix) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.name);
		List<ServiceCreation<?>> children = new ArrayList<>(this.dependents);
		for (int i = 0; i < children.size() - 1; i++) {
			stringBuilder.append(prefix);
			stringBuilder.append("├── ");
			stringBuilder.append(children.get(i).printDependents(prefix + "│   "));
		}
		if (!children.isEmpty()) {
			stringBuilder.append(prefix);
			stringBuilder.append("└── ");
			stringBuilder.append(children.get(children.size() - 1).printDependents(prefix + "    "));
		}
		return stringBuilder.toString();
	}

	public String printDependencies() {
		return this.printDependencies("\n");
	}

	private String printDependencies(String prefix) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.name);
		List<ServiceCreation<?>> children = new ArrayList<>(this.dependencies);
		for (int i = 0; i < children.size() - 1; i++) {
			stringBuilder.append(prefix);
			stringBuilder.append("├── ");
			stringBuilder.append(children.get(i).printDependencies(prefix + "│   "));
		}
		if (!children.isEmpty()) {
			stringBuilder.append(prefix);
			stringBuilder.append("└── ");
			stringBuilder.append(children.get(children.size() - 1).printDependencies(prefix + "    "));
		}
		return stringBuilder.toString();
	}

}
