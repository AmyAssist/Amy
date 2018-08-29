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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
	@Reference
	private SpeechInputHandler speechInputHandler;
	@Context
	private UriInfo uriInfo;
	@Reference
	private Server server;

	/**
	 * handles consoleInput from a client
	 * 
	 * @param input
	 *            the input from the client
	 * @return the response from Amy
	 */
	@POST
	@Path("console")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Operation(summary = "Process natural language text input",
			description = "This is the remote amy chatbot."
					+ " It can be used to interact with the natural language interface of amy."
					+ " The possible intents are the same as using the local console or the speech interaction",
			tags = "home")
	@ApiResponse(responseCode = "200",
			description = "the natural language text input has been processed successfully and the response contain the answer",
			content = @Content(
					examples = @ExampleObject(summary = "This response represent that you don't have new mails.",
							name = "new mails", value = "You don't have new mails.")))
	@ApiResponse(responseCode = "500", description = "the input could not be precessed")
	public String useAmy(@RequestBody(description = "The natural language text", required = true,
			content = @Content(examples = @ExampleObject(name = "new mails", summary = "Ask Amy if you have new mails",
					value = "how many new emails do i have"))) String input) {
		try {
			return this.speechInputHandler.handle(input).get();
		} catch (Exception e) {
			throw new WebApplicationException("can't handle input: " + input, e, Status.INTERNAL_SERVER_ERROR);
		}
	}

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
		Class<?> cls = this.server.getPluginClass(iPlugin.getUniqueName());
		if (cls.isAnnotationPresent(Path.class)) {
			return this.uriInfo.getBaseUriBuilder().path(cls).build().toString();
		}
		return null;
	}

}
