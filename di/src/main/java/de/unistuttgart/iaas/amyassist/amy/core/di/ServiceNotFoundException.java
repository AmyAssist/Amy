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

/**
 * A exception of the dependency injection
 * 
 * @author Leon Kiefer
 */
public class ServiceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2441944380474159637L;
	private final Class<?> serviceType;

	/**
	 * @param serviceDescription
	 */
	public ServiceNotFoundException(ServiceDescription<?> serviceDescription) {
		this.serviceType = serviceDescription.getServiceType();
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return "The Service " + this.serviceType.getName() + " is not registered in the DI or do not exists.";
	}

}
