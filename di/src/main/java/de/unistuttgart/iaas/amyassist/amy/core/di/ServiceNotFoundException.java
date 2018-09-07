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
	private final String message;

	/**
	 * @param serviceDescription
	 */
	public ServiceNotFoundException(ServiceDescription<?> serviceDescription, ServiceCreation<?> serviceCreation) {
		this.message = "No Service of type " + serviceDescription.getServiceType().getName() + " with qualifier "
				+ serviceDescription.getAnnotations() + " is registered in the DI." + "\nRequired by:\n"
				+ serviceCreation.print() + this.resolveMessage(serviceDescription);
	}

	private final String resolveMessage(ServiceDescription<?> serviceDescription) {
		return "\nSo first make sure you use the Service type and not the Service implementation to find the service."
				+ "\nIs " + serviceDescription.getServiceType().getName() + " the type of the Service?"
				+ "\nIf not you MUST change the JavaType of the Reference to match the Service type to get the Service."
				+ "\nElse make sure there is a Service implementation for the Service type "
				+ serviceDescription.getServiceType().getName() + " registered in the DI."
				+ "\nFirst step find the implementation of the Service."
				+ "\nSecond check if the type of the Service is " + serviceDescription.getServiceType().getName()
				+ "\nif you use the @Service read the JavaDoc to find out how to set the correct type of the Service."
				+ "\nThird check if the Service implemenatin is loaded either by using a deployment descriptor or by a programmatic call.";
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
