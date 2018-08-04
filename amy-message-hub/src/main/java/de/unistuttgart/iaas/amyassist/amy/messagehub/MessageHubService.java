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

package de.unistuttgart.iaas.amyassist.amy.messagehub;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;

/**
 * The implementation of the MessageHub api interface as Runnable Service
 * 
 * @author Kai Menzel, Leon Kiefer
 */
@Service(MessageHub.class)
public class MessageHubService implements MessageHub, Runnable, RunnableService {

	@Reference
	private Logger logger;

	private Map<String, List<Consumer<String>>> eventListener = new ConcurrentHashMap<>();

	private BlockingQueue<Message<String>> queue = new LinkedBlockingQueue<>();

	private Thread thread;

	@Override
	public void start() {
		this.thread = new Thread(this, "MessageHub");
		this.thread.start();
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Message<String> event = this.queue.take();
				if (this.eventListener.containsKey(event.getTopic())) {
					List<Consumer<String>> list = this.eventListener.get(event.getTopic());
					for (Consumer<String> eventExecuter : list) {
						executeHandler(eventExecuter, event);
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}

	}

	private <T> void executeHandler(Consumer<T> handler, Message<T> event) {
		try {
			handler.accept(event.getData());
		} catch (RuntimeException e) {
			this.logger.error("Error in event handler", e);
		}
	}

	/**
	 * Subscribe to a topic
	 * 
	 * @param topic
	 *            to subscribe to
	 * @param eventExecuter
	 *            Class that gets called shoud sth gets published on given Topic
	 */
	@Override
	public void subscribe(String topic, Consumer<String> eventExecuter) {
		if (this.eventListener.containsKey(topic)) {
			this.eventListener.get(topic).add(eventExecuter);
		} else {
			List<Consumer<String>> listeners = new LinkedList<>();
			listeners.add(eventExecuter);
			this.eventListener.put(topic, listeners);
		}

	}

	/**
	 * Unsubscribe to a topic
	 * 
	 * @param topic
	 *            to unsubscribe to
	 * @param eventExecuter
	 *            class that will be unsubscribed to the Topic
	 */
	@Override
	public void unsubscribe(String topic, Consumer<String> eventExecuter) {
		if (this.eventListener.containsKey(topic)) {
			this.eventListener.get(topic).remove(eventExecuter);
		}

	}

	/**
	 * Publish a message to a Topic
	 * 
	 * @param topic
	 *            Topic of the Message
	 * @param message
	 *            containing the Information
	 */
	@Override
	public void publish(String topic, String message) {
		this.queue.add(new Message<>(topic, message));
	}

	@Override
	public void stop() {
		this.thread.interrupt();
	}

}
