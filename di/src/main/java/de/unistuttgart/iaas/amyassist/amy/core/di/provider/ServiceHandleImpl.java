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

package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

/**
 * The implementation of the ServiceHandle
 * 
 * @author Leon Kiefer
 */
public class ServiceHandleImpl<T> implements ServiceHandle<T> {

	private final T service;

	/**
	 * Create a new ServiceHandle with a given service instance
	 * 
	 * @param service
	 *            the service instance for this ServiceHandle
	 */
	public ServiceHandleImpl(T service) {
		this.service = service;
	}

	@Override
	public T getService() {
		return this.service;
	}

}
