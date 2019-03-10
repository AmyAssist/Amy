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

package io.github.amyassist.amy.logger;

import org.slf4j.LoggerFactory;

import asg.cliche.Command;
import asg.cliche.Param;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * 
 * This class contains the commands of the logger.
 * 
 * @author Leon Kiefer
 */
public class LoggerConsole {
	/**
	 * Set the log level
	 * 
	 * @param loggerName
	 *            the package name
	 * @param level
	 *            the string representation of the log level
	 */
	@Command(abbrev = "log", description = "Set the log level of the given logger to the given level."
			+ " This settings are not persisted.")
	public void setLogLevel(@Param(name = "logger") String loggerName, @Param(name = "level") String level) {
		Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
		logger.setLevel(Level.toLevel(level));
	}

	/**
	 * Set the log level of the root logger
	 * 
	 * @param level
	 *            the string representation of the log level
	 */
	@Command(abbrev = "log", description = "Set the log level of the root logger to the given level."
			+ " This settings are not persisted.")
	public void setLogLevel(@Param(name = "level") String level) {
		this.setLogLevel(org.slf4j.Logger.ROOT_LOGGER_NAME, level);
	}
}
