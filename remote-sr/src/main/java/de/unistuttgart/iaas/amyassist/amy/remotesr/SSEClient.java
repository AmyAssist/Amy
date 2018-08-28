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

package de.unistuttgart.iaas.amyassist.amy.remotesr;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SSE client module object
 *
 * @author Benno Krauß, Tim Neumann
 */
class SSEClient {

	private final Logger logger = LoggerFactory.getLogger(SSEClient.class);

	private final SseEventSink sink;
	private final Sse sse;

	/**
	 * Creates a new SSE Client
	 * 
	 * @param sink
	 *            The sse event sink
	 * @param sse
	 *            The sse
	 */
	SSEClient(SseEventSink sink, Sse sse) {
		this.sink = sink;
		this.sse = sse;
	}

	/**
	 * @return Whether this client is connected.
	 */
	boolean isConnected() {
		return this.sink != null && !this.sink.isClosed();
	}

	/**
	 * Disconnect this client.
	 */
	void disconnect() {
		this.sink.close();
	}

	/**
	 * Send an event to a connected client
	 * 
	 * @param event
	 *            the name of the event
	 * @return true if the client is connected and the transmission was successful, otherwise false
	 */
	boolean sendEvent(String event) {
		if (isConnected()) {
			try {
				this.sink.send(this.sse.newEvent(event, event)).toCompletableFuture().get();
				return true;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				this.logger.warn("Couldn't send SSE", e);
			}
		}
		return false;
	}
}
