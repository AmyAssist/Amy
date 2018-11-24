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

package io.github.amyassist.amy.core.di.exception;

import io.github.amyassist.amy.core.di.ServiceDescription;

/**
 * A exception of the dependency injection, signaling, that a service is already registered
 * 
 * @author Leon Kiefer
 */
public class DuplicateServiceException extends RuntimeException {

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = -4001860407694050099L;
	private final String serviceDescription;

	/**
	 * @param serviceDescription
	 *            the serviceDescription of the duplicate Service
	 */
	public DuplicateServiceException(ServiceDescription<?> serviceDescription) {
		this.serviceDescription = serviceDescription.toString();
	}

	@Override
	public String getMessage() {
		return this.serviceDescription;
	}
}
