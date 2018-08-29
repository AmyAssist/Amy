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

package de.unistuttgart.iaas.amyassist.amy.restresources.home;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;
import io.swagger.v3.oas.annotations.Operation;

/**
 * The home resource of Amy
 * 
 * @author Christian Bräuner
 */
@Path(HomeResource.PATH)
public class HomeResource {

	/**
	 * the Path of this resource
	 */
	public static final String PATH = "home";

	@Reference
	private PluginManager manager;
	@Context
	private UriInfo uriInfo;
	
	@Reference
	private MessageHub messageHub;

	/**
	 * returns all installed plugins
	 * 
	 * @return array of installed plugins
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(tags = "home")
	public SimplePluginEntity[] getPlugins() {
		List<IPlugin> pluginList = this.manager.getPlugins();
		SimplePluginEntity[] plugins = new SimplePluginEntity[pluginList.size()];
		for (int i = 0; i < pluginList.size(); i++) {
			plugins[i] = new SimplePluginEntity(pluginList.get(i));
			plugins[i].setLink(createPath(pluginList.get(i)));
		}
		return plugins;
	}

	private String createPath(IPlugin iPlugin) {
		return null;
	}

	/**
	 * mutes amy
	 */
	@POST
	@Path("mute")
	public void mute() {
		this.messageHub.publish("home/all/mute", "true");
	}

	/**
	 * unmutes amy
	 */
	@POST
	@Path("unmute")
	public void unmute() {
		this.messageHub.publish("home/all/mute", "false");
	}

}
