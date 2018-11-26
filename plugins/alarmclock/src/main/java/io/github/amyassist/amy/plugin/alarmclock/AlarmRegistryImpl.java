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

package io.github.amyassist.amy.plugin.alarmclock;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.registry.AbstractRegistry;

/**
 * The alarm registry
 * 
 * @author Patrick Gebhardt
 */
@Service(AlarmRegistry.class)
public class AlarmRegistryImpl extends AbstractRegistry<Alarm> implements AlarmRegistry {

	@Override
	protected String getPersistenceUnitName() {
		return "AlarmRegistry";
	}

	@Nonnull
	@Override
	public Class<? extends Alarm> getEntityClass() {
		return Alarm.class;
	}

}
