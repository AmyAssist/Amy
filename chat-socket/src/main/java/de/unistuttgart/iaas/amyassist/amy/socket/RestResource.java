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

package de.unistuttgart.iaas.amyassist.amy.socket;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static de.unistuttgart.iaas.amyassist.amy.socket.ChatConfig.WEBSOCKET_URL;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * Rest class to provide the web client the websocket endpoint URL
 *
 * @author Benno Krauß
 */
@Path("chat")
public class RestResource {

    @Reference
    ChatConfig config;

    @Path("url")
    @Produces(TEXT_PLAIN)
    @GET
    public String getChatURL() {
        return config.get(WEBSOCKET_URL);
    }
}
