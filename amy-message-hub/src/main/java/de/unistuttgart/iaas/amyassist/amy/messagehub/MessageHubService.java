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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.information.InstanceInformation;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactory;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName;

/**
 * The implementation of the MessageHub api interface as Runnable Service
 *
 * @author Kai Menzel, Leon Kiefer
 */
@Service(MessageHub.class)
public class MessageHubService implements MessageHub, RunnableService, MqttCallback, IMqttActionListener {

	private static final String CONFIG_NAME = "messaging.config";
	private static final String KEY_BROKER_ADDRESS = "brokerAddress";

	@Reference
	private Logger logger;

	@Reference
	private InstanceInformation info;

	@Reference
	private ConfigurationManager configManager;

	@Reference
	private TopicFactory tf;

	private MqttAsyncClient client;

	private Map<UUID, BiConsumer<TopicName, Message>> eventListener = new HashMap<>();
	private Map<TopicFilter, List<UUID>> topicListeners = new HashMap<>();

	private String brokerAddress;

	@PostConstruct
	private void init() {
		Properties config = this.configManager.getConfigurationWithDefaults(CONFIG_NAME);
		this.brokerAddress = config.getProperty(KEY_BROKER_ADDRESS);

		try {
			this.client = new MqttAsyncClient(this.brokerAddress, this.info.getNodeId() + "-MainJavaApp",
					new MqttDefaultFilePersistence()); // TODO: Configure directory
			this.client.setCallback(this);
		} catch (MqttException e) {
			fail("Initialize", e);
		}
	}

	@Override
	public void start() {
		try {
			this.client.connect("Connect", this);
		} catch (MqttException e) {
			fail("Connect", e);
		}
	}

	private void executeHandler(BiConsumer<TopicName, Message> handler, Message msg, TopicName topic) {
		try {
			handler.accept(topic, msg);
		} catch (RuntimeException e) {
			this.logger.error("Error in event handler", e);
		}
	}

	@Override
	public void publish(String topic, String message) {
		publish(this.tf.createTopicName(topic), message, 2, false);
	}

	@Override
	public void stop() {
		try {
			this.client.disconnect();
		} catch (MqttException e) {
			fail("Disconnect", e);
		}
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.IMqttActionListener#onSuccess(org.eclipse.paho.client.mqttv3.IMqttToken)
	 */
	@Override
	public void onSuccess(IMqttToken asyncActionToken) {
		// Don't do anything
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.IMqttActionListener#onFailure(org.eclipse.paho.client.mqttv3.IMqttToken,
	 *      java.lang.Throwable)
	 */
	@Override
	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		fail(asyncActionToken.getUserContext().toString(), exception);
	}

	private void fail(String action, Throwable exception) {
		throw new IllegalStateException("Error during action " + action + ".", exception);
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
	 */
	@Override
	public void connectionLost(Throwable cause) {
		throw new IllegalStateException("Connection lost.", cause);
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String,
	 *      org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		TopicName name = this.tf.createTopicName(topic);

		for (TopicFilter filter : this.topicListeners.keySet()) {
			if (filter.doesFilterMatch(name)) {
				for (UUID uuid : this.topicListeners.get(filter)) {
					BiConsumer<TopicName, Message> handler = this.eventListener.get(uuid);
					executeHandler(handler, new MessageImpl(message), name);
				}
			}
		}
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Don't do anything
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#subscribe(java.lang.String,
	 *      java.util.function.Consumer)
	 */
	@Override
	public UUID subscribe(String topic, Consumer<String> handler) {
		return subscribe(this.tf.createTopicFilter(topic), (t, m) -> handler.accept(m.getPayload()));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#subscribe(TopicFilter,
	 *      java.util.function.BiConsumer)
	 */
	@Override
	public UUID subscribe(TopicFilter topic, BiConsumer<TopicName, Message> handler) {
		UUID uuid = UUID.randomUUID();

		if (this.topicListeners.containsKey(topic)) {
			this.topicListeners.get(topic).add(uuid);
		} else {
			List<UUID> listeners = new LinkedList<>();
			listeners.add(uuid);
			this.topicListeners.put(topic, listeners);
			try {
				this.client.subscribe(topic.getStringRepresentation(), 0, "Subscribe", this);
			} catch (MqttException e) {
				fail("Subscribe", e);
			}
		}

		this.eventListener.put(uuid, handler);

		return uuid;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#unsubscribe(java.util.UUID)
	 */
	@Override
	public void unsubscribe(UUID identifier) {
		this.eventListener.remove(identifier);
		for (TopicFilter topic : this.topicListeners.keySet()) {
			if (this.topicListeners.get(topic).contains(identifier)) {
				this.topicListeners.get(topic).remove(identifier);
				if (this.topicListeners.get(topic).isEmpty()) {
					this.topicListeners.remove(topic);
					try {
						this.client.unsubscribe(topic.getStringRepresentation(), "Unsubscribe", this);
					} catch (MqttException e) {
						fail("Unsubscribe", e);
					}
				}
				break;
			}
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(TopicName, java.lang.String, int, boolean)
	 */
	@Override
	public void publish(TopicName topic, String payload, int qualityOfService, boolean retain) {
		MqttMessage msg = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
		msg.setQos(qualityOfService);
		msg.setRetained(retain);
		try {
			this.client.publish(topic.getStringRepresentation(), msg, "publish", this);
		} catch (MqttException e) {
			fail("Publish", e);
		}
	}
}
