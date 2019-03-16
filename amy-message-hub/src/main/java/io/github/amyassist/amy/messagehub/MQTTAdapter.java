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
import io.github.amyassist.amy.core.taskscheduler.api.TaskScheduler;
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
	private static final String KEY_BROKER_ADDRESS = "broker.host";
	private static final String KEY_BROKER_USERNAME = "broker.username";
	private static final String KEY_BROKER_PASSWORD = "broker.password";
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

	@Reference
	private TaskScheduler scheduler;

	private MqttAsyncClient client;

	private BiConsumer<TopicName, Message> handler;

	private Runnable onSuccesfullStartCallback;

	private MqttConnectOptions options;

	private Object stateLock = new Object();
	private ConnectionState currentState = ConnectionState.DISCONNECTED;
	private boolean running;
	private IMqttToken connectToken;

	@PostConstruct
	private void init() {
		Properties config = this.configManager.getConfigurationWithDefaults(CONFIG_NAME);
		String brokerAddress = config.getProperty(KEY_BROKER_ADDRESS);
		String persitanceLocation = config.getProperty(KEY_PERSITENCE_LOCATION);

		Path persistencePath = this.env.getWorkingDirectory().resolve(persitanceLocation);

		try {
			this.client = new MqttAsyncClient(brokerAddress, this.info.getNodeId(),
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

		if (!config.getProperty(KEY_BROKER_USERNAME).isEmpty()) {
			this.options.setUserName(config.getProperty(KEY_BROKER_USERNAME));
		}
		if (!config.getProperty(KEY_BROKER_PASSWORD).isEmpty()) {
			this.options.setPassword(config.getProperty(KEY_BROKER_PASSWORD).toCharArray());
		}
	}

	@Override
	public void start() {
		this.connect();
	}

	@Override
	public void stop() {
		this.running = false;
		this.disconnect();
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	private void connect() {
		synchronized (this.stateLock) {
			if (this.currentState == ConnectionState.DISCONNECTED) {
				try {
					this.currentState = ConnectionState.CONNECTING;
					this.connectToken = this.client.connect(this.options, null, this);
				} catch (MqttException e) {
					this.currentState = ConnectionState.DISCONNECTED;
					throw new IllegalStateException("Error while connecting", e);
				}
			} else {
				throw new IllegalStateException(this.currentState.toString());
			}
		}
	}

	private void disconnect() {
		synchronized (this.stateLock) {
			if (this.currentState == ConnectionState.CONNECTED) {
				try {
					this.currentState = ConnectionState.DISCONNECTING;
					this.client.disconnect(DISCONNECT_TIMEOUT * 1000L);
				} catch (MqttException e) {
					this.currentState = ConnectionState.CONNECTED;
					this.logger.error("Error while stopping mqtt adapter", e);
				}
			} else if (this.currentState == ConnectionState.CONNECTING) {
				this.currentState = ConnectionState.STOPCONNECTING;
			} else if (this.currentState == ConnectionState.RECONNECTING) {
				this.logger.error("not supported by MQTT client");
			}
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
			synchronized (this.stateLock) {
				this.connectToken = null;
				if (this.currentState == ConnectionState.CONNECTING) {
					this.currentState = ConnectionState.CONNECTED;
					this.logger.debug("MQTT Adapter connected.");
				} else if (this.currentState == ConnectionState.STOPCONNECTING) {
					this.currentState = ConnectionState.CONNECTED;
					this.scheduler.execute(this::disconnect);
				} else {
					throw new IllegalStateException(this.currentState.toString());
				}
			}
			if (!this.running) {
				this.running = true;
				this.onSuccesfullStartCallback.run();
			}
		}
	}

	@Override
	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		if (asyncActionToken.equals(this.connectToken)) {
			synchronized (this.stateLock) {
				this.connectToken = null;
				if (this.currentState == ConnectionState.CONNECTING) {
					this.currentState = ConnectionState.DISCONNECTED;
					this.logger.warn("Could not connect to Broker", exception);
				} else if (this.currentState == ConnectionState.STOPCONNECTING) {
					this.currentState = ConnectionState.DISCONNECTED;
				} else {
					throw new IllegalStateException(this.currentState.toString());
				}
			}
		} else {
			this.logger.error("Async error during operation \"{}\".", asyncActionToken.getUserContext(), exception);
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		synchronized (this.stateLock) {
			if (this.currentState == ConnectionState.CONNECTED) {
				this.currentState = ConnectionState.RECONNECTING;
			}
			if (this.currentState == ConnectionState.RECONNECTING) {
				this.currentState = ConnectionState.RECONNECTING;
			} else {
				throw new IllegalStateException(this.currentState.toString());
			}
		}
		if (cause instanceof MqttException) {
			MqttException exception = (MqttException) cause;
			this.logger.info("Conncetion to MQTT broker lost: {}", exception.getMessage());
		} else {
			this.logger.warn("Conncetion to MQTT broker lost:", cause);
		}
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

	private enum ConnectionState {
		/** -> CONNECTING */
		DISCONNECTED,
		/** -> DISCONNECTED, CONNECTED, STOPCONNECTING */
		CONNECTING,
		/** -> DISCONNECTED, DISCONNECTING */
		STOPCONNECTING,
		/** -> DISCONNECTING */
		CONNECTED,
		/** -> DISCONNECTED, RECONNECTING */
		DISCONNECTING,
		/** -> CONNECTED */
		RECONNECTING
	}

}
