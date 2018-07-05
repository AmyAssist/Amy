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
 * The interface of all ServiceFactories
 * 
 * @author Leon Kiefer
 */
public interface ServiceFactory<T> {
	/**
	 * Build the Service after all configuration is done. This doesn't mean a new instance is created. Multiple calls of
	 * this method must return the same instance. So after calling this method the configuration can't be changed.
	 * 
	 * @return the instance of the Service
	 */
	T build();
}
