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

package de.unistuttgart.iaas.amyassist.amy.core.di;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandle;

/**
 * Information about the creation process of a Service
 * 
 * @author Leon Kiefer
 */
class ServiceCreation<T> {
	CompletableFuture<ServiceHandle<T>> completableFuture;
	private final Set<ServiceCreation<?>> dependents = new HashSet<>();

	/**
	 * Check if the given ServiceCreation is transitive dependent on this. This check is importent to prevent
	 * 
	 * @param serviceCreation
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
	}

}
