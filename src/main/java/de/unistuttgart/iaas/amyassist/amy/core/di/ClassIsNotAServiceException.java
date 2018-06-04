/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core.di;

/**
 * A exception of the dependency injection, signaling, that a given class is
 * not a service.
 * 
 * @author Leon Kiefer
 */
public class ClassIsNotAServiceException extends RuntimeException {

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = 5382225920636029620L;
	private final Class<?> cls;

	/**
	 * @param cls
	 *            the class that is not a Service
	 */
	public ClassIsNotAServiceException(Class<?> cls) {
		this.cls = cls;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return "The class " + this.cls.getName() + " is not a Service";
	}

}
