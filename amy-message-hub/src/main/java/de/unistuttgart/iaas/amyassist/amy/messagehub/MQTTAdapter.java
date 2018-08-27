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
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.information.InstanceInformation;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactory;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName;

/**
 * A adapter to talk with the mqtt broker
 * 
 * @author Tim Neumann
 */
@Service(MessagingAdapter.class)
public class MQTTAdapter implements MessagingAdapter, RunnableService, MqttCallback, IMqttActionListener {

	private static final String CONFIG_NAME = "mqtt.config";
	private static final String KEY_BROKER_ADDRESS = "brokerAddress";
	private static final String KEY_PERSITENCE_LOCATION = "persitenceLocation";

	private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(10);
	private static final Duration KEEP_ALIVE_INTERVAL = Duration.ofSeconds(5);
	private static final Duration DISCONNECT_TIMEOUT = Duration.ofSeconds(2);
	private static final Duration INITIAL_RECONNECT_TIME = Duration.ofSeconds(3);

	private static final Function<Duration, Duration> RECONNECT_MODIFIER = amount -> (amount.multipliedBy(4));
	private static final int MAX_RECONNECT_ATTEMPTS = 3;

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

	private MqttConnectOptions options;

	private int reconnectAttempt;

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

		this.options = new MqttConnectOptions();
		this.options.setCleanSession(false);
		this.options.setConnectionTimeout((int) CONNECTION_TIMEOUT.get(ChronoUnit.SECONDS));
		this.options.setKeepAliveInterval((int) KEEP_ALIVE_INTERVAL.get(ChronoUnit.SECONDS));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		connect();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		disconnect();
	}

	private void connect() {
		try {
			this.client.connect(this.options, "Connect", this);
		} catch (MqttException e) {
			throw new IllegalStateException("Error while connecting", e);
		}
	}

	private void disconnect() {
		try {
			this.client.disconnect(DISCONNECT_TIMEOUT.get(ChronoUnit.MILLIS));
		} catch (MqttException | IllegalStateException e) {
			this.logger.error("Error while stopping mqtt adapter", e);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessagingAdapter#publish(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName,
	 *      java.lang.String, int, boolean)
	 */
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

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessagingAdapter#subscribe(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter)
	 */
	@Override
	public void subscribe(TopicFilter topic) {
		try {
			this.client.subscribe(topic.getStringRepresentation(), 2, "Subscribe", this);
		} catch (MqttException e) {
			throw new IllegalStateException("Error while subscribing", e);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessagingAdapter#unsubscribe(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter)
	 */
	@Override
	public void unsubscribe(TopicFilter topic) {
		try {
			this.client.unsubscribe(topic.getStringRepresentation(), "Unsubscribe", this);
		} catch (MqttException e) {
			throw new IllegalStateException("Error while unsubscribing", e);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessagingAdapter#setCallback(java.util.function.BiConsumer)
	 */
	@Override
	public void setCallback(BiConsumer<TopicName, Message> callback) {
		this.handler = callback;
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.IMqttActionListener#onSuccess(org.eclipse.paho.client.mqttv3.IMqttToken)
	 */
	@Override
	public void onSuccess(IMqttToken asyncActionToken) {
		if (asyncActionToken.isComplete() && asyncActionToken.getUserContext().toString().equals("Connect")) {
			this.logger.info("MQTT Adapter connected.");
			this.reconnectAttempt = 0;
		}
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.IMqttActionListener#onFailure(org.eclipse.paho.client.mqttv3.IMqttToken,
	 *      java.lang.Throwable)
	 */
	@Override
	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		if (this.reconnectAttempt > 0 && asyncActionToken.getUserContext().toString().equals("Connect")) {
			this.logger.info("Reconnect failed.");
			tryReconnect();
		} else {
			this.logger.error("Async error during operation \"{}\".", asyncActionToken.getUserContext(), exception);
		}
	}

	private void tryReconnect() {
		if (this.reconnectAttempt < MAX_RECONNECT_ATTEMPTS) {
			Duration amount = INITIAL_RECONNECT_TIME;

			for (int i = 0; i < this.reconnectAttempt; i++) {
				amount = RECONNECT_MODIFIER.apply(amount);
			}

			this.reconnectAttempt++;

			this.logger.info("Trying to reconnect in {}", amount);
			this.scheduler.schedule(this::start, Instant.now().plus(amount));
		} else {
			this.logger.error("Ne reconnects left.");
		}
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
	 */
	@Override
	public void connectionLost(Throwable cause) {
		this.logger.warn("Conncetion to mqtt broker lost:", cause);
		tryReconnect();
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String,
	 *      org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		this.handler.accept(this.tf.createTopicName(topic), new MessageImpl(message));
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Don't do anything
	}

}