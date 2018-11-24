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

package io.github.amyassist.amy.core.service;

/**
 * A RunnableService is a Service that must be startet and is only operational in the running state. The RunnableService
 * must be stoppable by calling the {@link #stop()} method. RunnableServices are Singletons.
 * 
 * @author Leon Kiefer
 */
public interface RunnableService {
	/**
	 * Start the RunnableService. After start the Service operational
	 */
	void start();

	/**
	 * Stop the RunnableService. All Threads started with {@link #start()} must be stopped and all resources opened in
	 * {@link #start()} must be closed.
	 */
	void stop();
}
