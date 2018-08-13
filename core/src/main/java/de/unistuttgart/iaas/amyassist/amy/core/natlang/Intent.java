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

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.AIMIntent;

/**
 * TODO: Description
 * @author Lars Buttgereit
 */
public class Intent {
	@Nonnull
	private final Method method;
	@Nonnull
	private final AGFNode grammar;
	@Nonnull
	private final Class<?> partialNLIClass;
	@Nonnull
	private final AIMIntent aimIntent;
	
	
	private List<Entity> entityList;
	private Map<String, IMatcher> matchers;
	private List<Prompt> prompts;
	
	
	public Intent(@Nonnull Method method, @Nonnull AGFNode grammar, @Nonnull Class<?> partialNLIClass, @Nonnull AIMIntent aimIntent) {
		this.method = method;
		this.grammar = grammar;
		this.partialNLIClass = partialNLIClass;
		this.aimIntent = aimIntent;
	}
}
