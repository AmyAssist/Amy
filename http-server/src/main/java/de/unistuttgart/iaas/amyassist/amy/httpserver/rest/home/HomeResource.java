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

package de.unistuttgart.iaas.amyassist.amy.httpserver.rest.home;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
/**
 * The home resource of Amy
 * 
 * @author Christian Bräuner
 */
@Path(HomeResource.PATH)
public class HomeResource {

	public static final String PATH = "hom";
	@Reference
	PluginManager manager; // TODO or whatever
	
	@POST
	@Path("console")
	public void useAmy(String input) {
		//core.doSomething(input)
	}
	
	@GET
	public SimplePluginEntity[] getPlugins() {
		SimplePluginEntity[] plugins = new SimplePluginEntity[manager.getNumberOfInstalledPlugins()];
		for(int i = 0; i < plugins.length; i++) {
			plugins[i] = new SimplePluginEntity();
			//plugins[i].setValues(values i got from somewhere);
		}
		return plugins;
	}
	
}
