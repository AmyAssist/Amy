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

package io.github.amyassist.amy.messagehub;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.BiConsumer;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.information.InstanceInformation;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.service.RunnableService;
import io.github.amyassist.amy.messagehub.topic.TopicFactory;
import io.github.amyassist.amy.messagehub.topic.TopicFilter;
import io.github.amyassist.amy.messagehub.topic.TopicName;

/**
 * A adapter to talk with the mqtt broker
 * 
 * @author Tim Neumann, Leon Kiefer
 */
@Service(MessagingAdapter.class)
public class MQTTAdapter implements MessagingAdapter, RunnableService, MqttCallback, IMqttActionListener {

	private static final String CONFIG_NAME = "mqtt.config";
	private static final String KEY_BROKER_ADDRESS = "brokerAddress";
	private static final String KEY_PERSITENCE_LOCATION = "persitenceLocation";

	private static final int DISCONNECTED_BUFFER_SIZE = 10000;

	// all durations are given in seconds
	private static final int CONNECTION_TIMEOUT = 10;
	private static final int KEEP_ALIVE_INTERVAL = 5;
	private static final int DISCONNECT_TIMEOUT = 2;

	@Reference
	private Logger logger;

	@Reference
	private InstanceInformation info;

	@Reference
	private ConfigurationManager configManager;

	@Reference
	private TopicFactory tf;

	@Reference
	private Environment env;

	private MqttAsyncClient client;

	private BiConsumer<TopicName, Message> handler;

	private Runnable onSuccesfullStartCallback;

	private MqttConnectOptions options;

	private boolean running;
	private IMqttToken connectToken;

	@PostConstruct
	private void init() {
		Properties config = this.configManager.getConfigurationWithDefaults(CONFIG_NAME);
		String brokerAddress = config.getProperty(KEY_BROKER_ADDRESS);
		String persitanceLocation = config.getProperty(KEY_PERSITENCE_LOCATION);

		Path persistencePath = this.env.getWorkingDirectory().resolve(persitanceLocation);

		try {
			this.client = new MqttAsyncClient(brokerAddress, this.info.getNodeId() + "-MainJavaApp",
					new MqttDefaultFilePersistence(persistencePath.toAbsolutePath().toString()));
			this.client.setCallback(this);
		} catch (MqttException e) {
			throw new IllegalStateException("Failed to initialize mqtt client", e);
		}

		DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
		disconnectedBufferOptions.setBufferEnabled(true);
		disconnectedBufferOptions.setPersistBuffer(true);
		disconnectedBufferOptions.setBufferSize(DISCONNECTED_BUFFER_SIZE);
		disconnectedBufferOptions.setDeleteOldestMessages(false);
		this.client.setBufferOpts(disconnectedBufferOptions);

		this.options = new MqttConnectOptions();
		this.options.setCleanSession(false);
		this.options.setConnectionTimeout(CONNECTION_TIMEOUT);
		this.options.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);
		this.options.setAutomaticReconnect(true);
	}

	@Override
	public void start() {
		connect();
	}

	@Override
	public void stop() {
		this.running = false;
		disconnect();
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	private void connect() {
		try {
			this.connectToken = this.client.connect(this.options, null, this);
		} catch (MqttException e) {
			throw new IllegalStateException("Error while connecting", e);
		}
	}

	private void disconnect() {
		try {
			this.client.disconnect(DISCONNECT_TIMEOUT * 1000L);
		} catch (MqttException | IllegalStateException e) {
			this.logger.error("Error while stopping mqtt adapter", e);
		}
	}

	@Override
	public void publish(TopicName topic, String payload, int qualityOfService, boolean retain) {
		MqttMessage msg = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
		msg.setQos(qualityOfService);
		msg.setRetained(retain);
		try {
			this.client.publish(topic.getStringRepresentation(), msg, "publish", this);
		} catch (MqttException e) {
			throw new IllegalStateException("Error while publishing.", e);
		}
	}

	@Override
	public void subscribe(TopicFilter topic) {
		if (!this.isRunning())
			throw new IllegalStateException("Cannot subscribe, because the mqtt adapter is not running.");
		try {
			this.client.subscribe(topic.getStringRepresentation(), 2, "Subscribe", this);
		} catch (MqttException e) {
			throw new IllegalStateException("Error while subscribing", e);
		}
	}

	@Override
	public void unsubscribe(TopicFilter topic) {
		if (!this.isRunning())
			throw new IllegalStateException("Cannot unsubscribe, because the mqtt adapter is not running.");
		try {
			this.client.unsubscribe(topic.getStringRepresentation(), "Unsubscribe", this);
		} catch (MqttException e) {
			throw new IllegalStateException("Error while unsubscribing", e);
		}
	}

	@Override
	public void setCallback(BiConsumer<TopicName, Message> callback) {
		this.handler = callback;
	}

	@Override
	public void setStartCallback(Runnable callback) {
		this.onSuccesfullStartCallback = callback;
	}

	@Override
	public void onSuccess(IMqttToken asyncActionToken) {
		if (asyncActionToken.isComplete() && asyncActionToken.equals(this.connectToken)) {
			this.logger.debug("MQTT Adapter connected.");
			this.connectToken = null;
			if (!this.running) {
				this.running = true;
				this.onSuccesfullStartCallback.run();
			}
		}
	}

	@Override
	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		if (asyncActionToken.equals(this.connectToken)) {
			this.connectToken = null;
			this.logger.error("Could not connect to Broker", exception);
		}
		this.logger.error("Async error during operation \"{}\".", asyncActionToken.getUserContext(), exception);
	}

	@Override
	public void connectionLost(Throwable cause) {
		this.logger.warn("Conncetion to mqtt broker lost:", cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		if (this.handler != null) {
			this.handler.accept(this.tf.createTopicName(topic), new MessageImpl(message));
		} else {
			this.logger.debug("No handler for incoming messages. Message is ignored!");
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Don't do anything
	}

}
