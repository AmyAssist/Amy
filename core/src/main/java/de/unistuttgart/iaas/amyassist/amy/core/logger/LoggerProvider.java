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

package de.unistuttgart.iaas.amyassist.amy.core.logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;

/**
 * The Logger Provider for all Services
 * 
 * @author Leon Kiefer
 */
public class LoggerProvider implements ServiceFunction<Logger> {

	private static final String CONTEXT_IDENTIFIER = "class";

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider#getService(java.util.Map, java.util.Map)
	 */
	@Override
	public Logger getService(Map<ServiceDescription<?>, ServiceFactory<?>> resolvedDependencies, Map<String, ?> context) {
		Class<?> cls = (Class<?>) context.get(CONTEXT_IDENTIFIER);
		return LoggerFactory.getLogger(cls);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider#getDependencies()
	 */
	@Override
	public Collection<ServiceDescription<?>> getDependencies() {
		return Collections.emptyList();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider#getRequiredContextIdentifiers()
	 */
	@Override
	public Collection<String> getRequiredContextIdentifiers() {
		return Collections.singletonList(CONTEXT_IDENTIFIER);
	}

}
