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

import java.util.logging.LogManager;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * The main entry point of the application
 *
 * @author Tim Neumann, Leon Kiefer
 */
public class Main {

	static {
		LogManager.getLogManager().reset();
		SLF4JBridgeHandler.install();
		// workaround for
		// https://github.com/cmusphinx/sphinx4/blob/master/sphinx4-core/src/main/java/edu/cmu/sphinx/util/props/ConfigurationManagerUtils.java#L138
		System.setProperty("java.util.logging.config.file", "");
	}

	/**
	 * The main entry point of the program
	 *
	 * @param args
	 *            The command line arguments. Will be passed to the core.
	 */
	public static void main(String[] args) {
		Core core = new Core();
		core.start(args);
	}
}
