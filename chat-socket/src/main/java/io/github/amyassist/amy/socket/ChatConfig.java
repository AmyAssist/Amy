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

package io.github.amyassist.amy.socket;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

import java.util.Properties;

/**
 * Config helper class for websocket server
 *
 * @author Benno Krauß
 */
@Service
public class ChatConfig {
    /** The name of the config used by this class */
    private static final String CONFIG_NAME = "socket.config";
    /** The name of the property, which specifies the port */
    static final String PROPERTY_PORT = "webSocketPort";
    /** The name of the property which specifies the websocket endpoint URL */
    static final String WEBSOCKET_URL = "webSocketURL";

    @Reference
    private ConfigurationManager configurationManager;

    String get(String key) {
        Properties conf = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
        return conf.getProperty(key);
    }
}
