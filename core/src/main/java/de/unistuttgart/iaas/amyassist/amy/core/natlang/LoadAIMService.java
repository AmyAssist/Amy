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

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.AmyInteractionModel;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService;

/**
 * This service loads all amy interaction model xml files 
 * and saves them inside an array list, containing AmyInteractionModel instances
 * 
 * @author Felix Burk
 */
@Service(LoadAIMService.class)
public class LoadAIMService implements DeploymentContainerService {
	
	@Reference
	private PluginManager pluginManager;
	
	@Reference
	private Logger logger;
	
	/**
	 * internal list of all amy interaction models
	 */
	private List<AmyInteractionModel> aims = new ArrayList<>();


	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService#deploy()
	 */
	@Override
	public void deploy() {
		List<IPlugin> plugins = this.pluginManager.getPlugins();
		
		for(IPlugin plugin : plugins) {
			Path path = plugin.getPath();
			
			try (JarFile jar = new JarFile(path.toFile())) {
				Enumeration<JarEntry> jarEntries = jar.entries();

				while(jarEntries.hasMoreElements()) {
					JarEntry jarEntry = jarEntries.nextElement();
					if(jarEntry.getName().endsWith(".aim.xml")) {
						InputStream stream = plugin.getClassLoader().getResourceAsStream(jarEntry.getName());
						
						try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
							AmyInteractionModel model = this.extractModel(reader.lines().collect(Collectors.joining()), jarEntry.getName());
							if(model != null) {
								this.aims.add(model);
							}
							reader.close();
						}
					}
				}

			} catch (IOException e) {
				this.logger.error("could not load amy interaction model from plugin {}", plugin.getDisplayName());
			}
		}
	}


	/**
	 * extracts AmyInteractionModel from
	 *  
	 * @param xmlContent content of the xml
	 * @param entryName name of the jar entry
	 * @return AmyInteractionModel instance 
	 */
	private AmyInteractionModel extractModel(String xmlContent, String entryName) {
		try {
			JAXBContext jc = JAXBContext.newInstance(AmyInteractionModel.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			StringReader reader = new StringReader(xmlContent);
			AmyInteractionModel aim = (AmyInteractionModel) unmarshaller.unmarshal(reader);
			return aim;
		} catch (JAXBException e1) {
			e1.printStackTrace();
			this.logger.error("Amy Interaction Model for file {} could not be parsed", entryName);
		}
		return null;
	}


	/**
	 * Get's {@link #aims aims}
	 * @return  aims
	 */
	public List<AmyInteractionModel> getAims() {
		return this.aims;
	}	

}
