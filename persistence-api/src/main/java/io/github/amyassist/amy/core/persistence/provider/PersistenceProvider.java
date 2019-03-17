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

package io.github.amyassist.amy.core.persistence.provider;

import java.util.Properties;

/**
 * Adapter interface for Persistence providers to customize the configuration parameter used to create a
 * EntityManagerFactory. For example this adapter should be used set the javax.persistence.jdbc properties to connect to
 * a database.
 * 
 * @author Leon Kiefer
 */
public interface PersistenceProvider {
	/**
	 * @param name
	 *            the name of the persistence unit
	 * @return the properties used to create the EntityManagerFactory
	 */
	Properties getProperties(String name);
}
