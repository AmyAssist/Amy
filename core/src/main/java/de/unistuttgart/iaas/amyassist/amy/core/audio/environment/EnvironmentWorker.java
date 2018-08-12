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

package de.unistuttgart.iaas.amyassist.amy.core.audio.environment;

/**
 * An abstract worker for environments
 * 
 * @author Tim Neumann
 */
public abstract class EnvironmentWorker implements Runnable {

	private Thread thread;
	private boolean stop = false;

	/**
	 * Creates a new worker with the given name
	 * 
	 * @param name
	 *            The name of the worker
	 */
	public EnvironmentWorker(String name) {
		this.thread = new Thread(this, name);
	}

	/**
	 * Starts this worker
	 */
	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	/**
	 * Stops this worker.
	 */
	public void stop() {
		this.stop = true;
		this.thread.interrupt();
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * @return Whether the worker should stop.
	 */
	protected boolean shouldStop() {
		return (this.stop || Thread.currentThread().isInterrupted());
	}

}
