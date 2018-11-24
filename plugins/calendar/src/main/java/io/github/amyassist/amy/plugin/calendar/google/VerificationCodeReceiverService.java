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

package io.github.amyassist.amy.plugin.calendar.google;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.service.RunnableService;
import io.github.amyassist.amy.httpserver.Server;

/**
 * Adapter for javax.ws.rs and VerificationCodeReceiver.
 * 
 * @author Leon Kiefer
 */
@Service(VerificationCodeReceiverService.class)
public class VerificationCodeReceiverService implements RunnableService {

	private final Map<UUID, CompletableFuture<String>> openRequests = new ConcurrentHashMap<>();

	@Reference
	private Logger logger;

	@Reference
	private Server server;

	public VerificationCodeReceiver newVerificationCodeReceiver() {
		return new VerificationCodeReceiver() {

			private final CompletableFuture<String> completableFuture = new CompletableFuture<>();
			private final UUID randomUUID = UUID.randomUUID();

			@Override
			public String waitForCode() throws IOException {
				try {
					return this.completableFuture.get();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new IOException("Got interruped while waiting for response.", e);
				} catch (ExecutionException e) {
					throw new IOException(e);
				}
			}

			@Override
			public void stop() throws IOException {
				VerificationCodeReceiverService.this.openRequests.remove(this.randomUUID);
			}

			@Override
			public String getRedirectUri() throws IOException {

				VerificationCodeReceiverService.this.openRequests.put(this.randomUUID, this.completableFuture);

				URI uri = UriBuilder.fromPath(server.getBaseUrl()).path(VerificationCodeReceiverResource.class)
						.path(VerificationCodeReceiverResource.class, "verificationCode").build(this.randomUUID);

				return uri.toString();
			}
		};
	}

	/**
	 * Set the received code.
	 * 
	 * @param id
	 * @param code
	 */
	void setVerificationCode(UUID id, String code) {
		synchronized (this.openRequests) {
			if (!this.openRequests.containsKey(id)) {
				throw new IllegalArgumentException("No verification request found for this id.");
			}
			this.openRequests.remove(id).complete(code);
		}
	}

	@Override
	public void start() {
		// empty
	}

	@Override
	public void stop() {
		if (!this.openRequests.isEmpty()) {
			this.logger.warn("There are open Verification code requests!");
			this.openRequests.clear();
		}
	}
}
