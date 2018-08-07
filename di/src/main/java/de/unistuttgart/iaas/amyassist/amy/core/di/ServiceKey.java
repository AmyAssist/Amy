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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Key for the ServiceProvider registry of the DependencyInjection implementation. This Class defines the hashCode and
 * equals method, so the ServiceKey can be used as Key in a {@link java.util.HashMap}
 * 
 * @author Leon Kiefer
 */
class ServiceKey<T> {
	@Nonnull
	final Class<T> serviceType;
	@Nonnull
	final Set<Annotation> annotations;

	public ServiceKey(ServiceDescription<T> description) {
		this.serviceType = description.getServiceType();
		this.annotations = new HashSet<>(description.getAnnotations());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.serviceType, this.annotations);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceKey other = (ServiceKey) obj;
		if (!this.annotations.equals(other.annotations))
			return false;
		if (!this.serviceType.equals(other.serviceType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.serviceType.getName() + "\n" + this.annotations.toString();
	}
}
