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

package de.unistuttgart.iaas.amyassist.amy.core.information;

import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Implementation of {@link InstanceInformation}
 *
 * @author Tim Neumann
 */
@Service(InstanceInformation.class)
public class InstanceInformationService implements InstanceInformation {

	private static final String CONFIG_NAME = "instance.config";
	private static final String PROPERTY_ID = "nodeId";

	private String id;

	@Reference
	private Logger logger;

	@Reference
	private ConfigurationManager configManager;

	@Reference
	private ConfigurationLoader configLoader;

	@PostConstruct
	private void init() {
		Properties config = this.configManager.getConfiguration(CONFIG_NAME);
		this.id = config.getProperty(PROPERTY_ID);

		if (this.id == null) {
			this.logger.info("Missing property " + PROPERTY_ID + ". Will generate and save it.");
			// The last 12 digits are enough. In a System with 20000 nodes a collision has a probability of 0,02%.
			this.id = "Amy-Node-" + UUID.randomUUID().toString().substring(24);
			Properties props = this.configLoader.load(CONFIG_NAME);
			props.setProperty(PROPERTY_ID, this.id);
			this.configLoader.store(CONFIG_NAME, props);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.information.InstanceInformation#getInstanceId()
	 */
	@Override
	public String getNodeId() {
		return this.id;
	}

}
