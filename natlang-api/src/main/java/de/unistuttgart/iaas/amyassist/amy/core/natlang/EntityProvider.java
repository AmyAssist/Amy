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

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks methods returning a custom string array as a grammar replacement e.g. {@code List<String> example =
 * {"test","test2"};} represents the grammar (test|test2)
 * 
 * @author Felix Burk, Lars Buttgereit
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(java.lang.annotation.ElementType.METHOD)
@Repeatable(EntityProviders.class)
public @interface EntityProvider {

	/**
	 * contains the unique entity key name
	 * 
	 * @return the key name
	 */
	String value();
}
