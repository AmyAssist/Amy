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
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * The Service implementation of the Environment
 * 
 * @author Leon Kiefer
 */
@Service
public class EnvironmentService implements Environment {

	private Path workingDirectory;

	@PostConstruct
	private void init() {
		this.workingDirectory = Paths.get("");
	}

	@Override
	public Path getWorkingDirectory() {
		return this.workingDirectory;
	}

	@Override
	public LocalDateTime getCurrentLocalDateTime() {
		return LocalDateTime.now();
	}

	@Override
	public ZonedDateTime getCurrentDateTime() {
		return ZonedDateTime.now();
	}
}
