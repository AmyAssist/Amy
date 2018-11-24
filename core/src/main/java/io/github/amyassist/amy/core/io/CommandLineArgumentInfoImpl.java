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

package de.unistuttgart.iaas.amyassist.amy.core.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link CommandLineArgumentInfo}
 * 
 * @author Tim Neumann
 */
public class CommandLineArgumentInfoImpl implements CommandLineArgumentInfo {

	private List<String> configPaths;

	/**
	 * Creates a new command line argument info
	 * 
	 * @param pConfigPaths
	 *            The config paths.
	 */
	public CommandLineArgumentInfoImpl(List<String> pConfigPaths) {
		this.configPaths = nullSaveList(pConfigPaths);
	}

	private List<String> nullSaveList(List<String> arg) {
		if (arg == null)
			return new ArrayList<>();
		return arg;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.io.CommandLineArgumentInfo#getConfigPaths()
	 */
	@Override
	public List<String> getConfigPaths() {
		return Collections.unmodifiableList(this.configPaths);
	}
}
