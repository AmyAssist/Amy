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
import java.util.Properties;
import java.util.function.BiConsumer;

import org.eclipse.paho.client.mqttv3.*;
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
 * A adapter to talk with the mqtt broker
 * 
 * @author Tim Neumann
 */
@Service(MessagingAdapter.class)
public class MQTTAdapter implements MessagingAdapter, RunnableService, MqttCallback, IMqttActionListener {

	private static final String CONFIG_NAME = "mqtt.config";
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
	private String brokerAddress;

	private BiConsumer<TopicName, Message> handler;

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

	private void fail(String action, Throwable exception) {
		throw new IllegalStateException("Error during action " + action + ".", exception);
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
			fail("Publish", e);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessagingAdapter#subscribe(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter)
	 */
	@Override
	public void subscribe(TopicFilter topic) {
		try {
			this.client.subscribe(topic.getStringRepresentation(), 0, "Subscribe", this);
		} catch (MqttException e) {
			fail("Subscribe", e);
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
			fail("Unsubscribe", e);
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
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		try {
			this.client.connect("Connect", this);
		} catch (MqttException e) {
			fail("Connect", e);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
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