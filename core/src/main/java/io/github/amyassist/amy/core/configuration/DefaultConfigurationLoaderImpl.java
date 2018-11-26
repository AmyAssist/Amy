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

package io.github.amyassist.amy.core.configuration;

import java.util.Properties;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.annotation.Context;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * Implementation of {@link DefaultConfigurationLoader} using the {@link InternalDefaultConfigurationLoader} and the
 * ClassLoader from the Context.
 * 
 * @author Leon Kiefer
 */
@Service
public class DefaultConfigurationLoaderImpl implements DefaultConfigurationLoader {
	@Reference
	private InternalDefaultConfigurationLoader internalDefaultConfigurationLoader;
	@Context(io.github.amyassist.amy.core.di.Context.CLASSLOADER)
	private ClassLoader classLoader;

	@Override
	public @Nonnull Properties load(String configurationName) {
		return this.internalDefaultConfigurationLoader.load(this.classLoader, configurationName);
	}

}
