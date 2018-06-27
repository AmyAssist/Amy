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

package de.unistuttgart.iaas.amyassist.amy.core;

/**
 * TODO: Description
 * @author
 */
public interface CommandLineArgumentHandler {

	/**
	 * @return whether the program should continue with normal execution considering these command line flags
	 * @throws IllegalStateException
	 *             When the CommandLineArgumentHandler is not initialized. (When {@link #init(String[]) init} was not
	 *             called before.)
	 */
	boolean shouldProgramContinue();

	/**
	 * Get the alternate config path set by the command line flags.
	 * 
	 * @return The alternate config path or null if none is set.
	 */
	String getConfigPath();

}