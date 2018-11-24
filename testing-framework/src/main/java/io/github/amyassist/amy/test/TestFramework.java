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

package io.github.amyassist.amy.test;

import javax.ws.rs.client.WebTarget;

import io.github.amyassist.amy.core.plugin.api.IStorage;

/**
 * A Framework to test Services
 * 
 * @author Leon Kiefer
 */
public interface TestFramework {
	/**
	 * specify the Rest resource
	 * 
	 * @param resource
	 *            the class of the Rest resource
	 * @return the WebTarget pointing to the REST Resource
	 */
	WebTarget setRESTResource(Class<?> resource);

	/**
	 * specify the Service Under Test
	 * 
	 * @param serviceClass
	 *            the class to be tested
	 * @return the Service Under Test
	 * @param <T>
	 *            the type of the service implementation
	 */
	<T> T setServiceUnderTest(Class<T> serviceClass);

	/**
	 * create a mock for the serviceType and bind it in the DI.
	 * 
	 * @param serviceType
	 *            the class of the service type to mock
	 * @return the service mock
	 * @param <T>
	 *            the type of the service
	 */
	<T> T mockService(Class<T> serviceType);

	/**
	 * register a real implementation instead of a mock and bind it in the DI. For "Storage" Services this is the best
	 * solution if there is a simple in-memory implementation of the Service
	 * 
	 * @param serviceType
	 *            the class of the service type
	 * @param serviceClass
	 *            the class of the implementation
	 * @return the service instance
	 * @param <T>
	 *            the type of the service
	 * 
	 */
	<T> T registerService(Class<T> serviceType, Class<? extends T> serviceClass);

	/**
	 * @return a storage object
	 */
	IStorage storage();
}
