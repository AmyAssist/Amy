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

package io.github.amyassist.amy.httpserver;

import java.net.URI;

/**
 * A http server, which can be configured, started and stopped programmatically.
 * 
 * @author Leon Kiefer
 */
public interface Server {

	/**
	 * creates and starts the HttpServer
	 * 
	 * @param classes
	 *            the resource classes
	 */
	void startWithResources(Class<?>... classes);

	/**
	 * Checks whether the server is running
	 * 
	 * @return whether the server is running
	 */
	boolean isRunning();

	/**
	 * @param cls
	 *            a resource class
	 */
	void register(Class<?> cls);

	/**
	 * shutdown the server if the server is running
	 */
	void stop();

	/**
	 * Start the server with the registered resource classes
	 */
	void start();

	/**
	 * Get the base url of the server. This is the uri specified in the config under server.url.
	 * 
	 * @return The base url.
	 */
	String getBaseUrl();

	/**
	 * Get the socket uri for the server. This starts with http, followed by the bound ip, the port and at maximum one
	 * path segment.
	 * 
	 * @return The socket uri.
	 */
	URI getSocketUri();

	/**
	 * Register a runnable that is executed some time after the server is started.
	 * 
	 * @param hook
	 *            The runnable to be executed.
	 */
	void registerOnPostStartHook(Runnable hook);
}
