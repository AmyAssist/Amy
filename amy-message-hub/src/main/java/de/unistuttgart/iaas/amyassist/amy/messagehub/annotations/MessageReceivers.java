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

package de.unistuttgart.iaas.amyassist.amy.messagehub.annotations;

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.commons.lang3.reflect.MethodUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService;
import de.unistuttgart.iaas.amyassist.amy.deployment.DeploymentDescriptorUtil;
import de.unistuttgart.iaas.amyassist.amy.messagehub.internal.InternalMessageHubService;
import de.unistuttgart.iaas.amyassist.amy.messagehub.internal.SubscriptionUtil;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactory;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter;

/**
 * Loads the DD and subscribes all MessageReceivers.
 * 
 * @author Leon Kiefer
 */
@Service(MessageReceivers.class)
public class MessageReceivers implements DeploymentContainerService {
	private static final String COMPONENT_DEPLOYMENT_DESCRIPTOR = "META-INF/" + MessageReceivers.class.getName();

	@Reference
	private InternalMessageHubService messageHub;
	@Reference
	private PluginManager pluginManager;
	@Reference
	private TopicFactory topicFactory;

	@Override
	public void deploy() {
		Set<Class<?>> all = DeploymentDescriptorUtil.getClasses(this.getClass().getClassLoader(),
				COMPONENT_DEPLOYMENT_DESCRIPTOR);

		for (IPlugin plugin : this.pluginManager.getPlugins()) {
			all.addAll(DeploymentDescriptorUtil.getClasses(plugin.getClassLoader(), COMPONENT_DEPLOYMENT_DESCRIPTOR));
		}
		try {
			all.forEach(this::register);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Could not deploy all MessageRecievers from deployment descriptor.", e);
		}
	}

	private void register(Class<?> cls) {
		if (!cls.isAnnotationPresent(MessageReceiver.class)) {
			throw new IllegalArgumentException("Missing @MessageReceiver annotation on class " + cls.getName());
		}

		Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(cls, Subscription.class);

		for (Method method : methodsWithAnnotation) {
			SubscriptionUtil.assertValidSubscriptionMethod(method);
			Subscription annotation = method.getAnnotation(Subscription.class);
			TopicFilter topicFilter;
			try {
				topicFilter = this.topicFactory.createTopicFilter(annotation.value());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Illegal topic filter on annotation in " + cls.getName(), e);
			}
			this.messageHub.subscribe(topicFilter, cls, method);
		}

	}

}
