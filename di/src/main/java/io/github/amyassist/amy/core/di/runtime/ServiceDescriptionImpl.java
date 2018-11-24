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

package de.unistuttgart.iaas.amyassist.amy.core.di.runtime;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;

/**
 * Implementation of ServiceDescription interface
 * 
 * @author Leon Kiefer
 * @param <T>
 *            the type of the service
 */
public class ServiceDescriptionImpl<T> implements ServiceDescription<T> {

	private final Class<T> serviceType;
	private final Set<Annotation> annotations;

	/**
	 * @param serviceType
	 *            the type of the Service given as class
	 * @param annotations
	 *            the qualifier annotations
	 */
	public ServiceDescriptionImpl(Class<T> serviceType, Set<Annotation> annotations) {
		this.serviceType = serviceType;
		this.annotations = annotations;
	}

	/**
	 * Create a Service description with no qualifiers
	 * 
	 * @param serviceType
	 *            the type of the Service given as class
	 */
	public ServiceDescriptionImpl(Class<T> serviceType) {
		this.serviceType = serviceType;
		this.annotations = Collections.emptySet();
	}

	@Override
	public Class<T> getServiceType() {
		return this.serviceType;
	}

	@Override
	public Set<Annotation> getAnnotations() {
		return this.annotations;
	}

	@Override
	public String toString() {
		return "Service type: " + this.getServiceType().getSimpleName() + "\nService Annotations:\n"
				+ this.getAnnotations().stream().map(Annotation::toString).collect(Collectors.joining("\n"));
	}
}
