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

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Information about the execution environment
 * 
 * @author Leon Kiefer
 */
public interface Environment {
	/**
	 * Get the current working directory. This does not change while running, but can differ after a restart of the
	 * application.
	 * 
	 * @return the path of the current working directory
	 */
	Path getWorkingDirectory();

	/**
	 * The current Date combinded with the local time as seen on a wall clock. It does not represent an instant on the
	 * time-line and cant be used to compare with other environments.
	 * 
	 * @return the current date-time
	 */
	LocalDateTime getCurrentLocalDateTime();

	/**
	 * The current date-time with the time zone of this environment.
	 * 
	 * @return the current date-time with time zone
	 */
	ZonedDateTime getCurrentDateTime();
}
