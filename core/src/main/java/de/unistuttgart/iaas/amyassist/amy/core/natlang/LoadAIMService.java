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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.AIMIntent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.AmyInteractionModel;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService;

/**
 * This service loads all amy interaction model xml files and saves them inside an array list, containing
 * AmyInteractionModel instances
 * 
 * @author Felix Burk
 */
@Service(LoadAIMService.class)
public class LoadAIMService implements DeploymentContainerService {

	private static final String METAFILENAME = "SpeechMeta";

	@Reference
	private PluginManager pluginManager;
	
	@Reference
	private NLProcessingManager nlManager;

	@Reference
	private Logger logger;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService#deploy()
	 */
	@Override
	public void deploy() {
		List<IPlugin> plugins = this.pluginManager.getPlugins();

		for (IPlugin plugin : plugins) {

			String pathS = "META-INF/" + plugin.getUniqueName() + "." + METAFILENAME;
			
			@SuppressWarnings("resource") //this stream is gonna be closed - line 92
			InputStream stream = plugin.getClassLoader().getResourceAsStream(pathS);
			
			if (stream == null) {
				this.logger.info("could not find aim meta file for plugin {}", plugin.getDisplayName());
				continue;
			}
			//get all aim.xml file names 
			List<String> aimFiles = readMetaFile(stream, plugin);
			
			try {
				stream.close();
			} catch (IOException e) {
				this.logger.info("stream could not be closed {}", e.getMessage());
			}
			
			//extract all intents
			List<AIMIntent> aimIntents = new ArrayList<>();
			
			for(String s : aimFiles) {
				this.logger.error(s);
				aimIntents.addAll(extractIntents(plugin, s));
			}
			
			//extract all speech methods
			List<Method> speechMethods = new ArrayList<>();
			
			for(Class<?> cls : plugin.getClasses()) {
				if(cls.isAnnotationPresent(de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand.class)) {
					speechMethods.addAll(NLIAnnotationReader.getValidIntentMethods(cls));
				}
			}
			
			matchAndRegister(aimIntents, speechMethods);

		}

	}

	/**
	 * matches aim intents and methods of speech classes
	 * and registeres them inside the NLProcessingManager
	 * 
	 * @param aimIntents list of all intents
	 * @param speechMethods list of all methods
	 */
	private void matchAndRegister(List<AIMIntent> aimIntents, List<Method> speechMethods) {
		for(AIMIntent intent : aimIntents) {
			String ref = intent.getReference();
			String fullClassName = ref.substring(0,ref.lastIndexOf("."));
			String methodName = ref.substring(ref.lastIndexOf(".")+1, ref.length());
			
			speechMethods.stream().filter(o -> o.getClass().getName().equals(fullClassName)).forEach(
				o -> {
					if(o.getName().equals(methodName)) {
						this.nlManager.register(o, intent);
						speechMethods.remove(o);
					}
				});
		}
		
	}

	/**
	 * extracts AIMIntents from aim xmls
	 * 
	 * @param plugin to load resource from
	 * @param fileName n
	 * @return list of extracted aim intents
	 */
	private List<AIMIntent> extractIntents(IPlugin plugin, String fileName) {
		InputStream stream = plugin.getClassLoader().getResourceAsStream(fileName);

		if (stream != null) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
				AmyInteractionModel model = this.extractModel(reader.lines().collect(Collectors.joining()), fileName);
				if (model != null) {
					return model.getIntents();
				}
				this.logger.error("could not read model");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.logger.error("could not read aim file");

		return null;
	}

	/**
	 * extracts AmyInteractionModel from
	 * 
	 * @param xmlContent
	 *            content of the xml
	 * @param entryName
	 *            name of the jar entry
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
	 * simple reader for meta file data 
	 * 
	 * @param stream input stream of file
	 * @param plugin for debugging purposes
	 * @return list of names from .aim.xml files
	 */
	private List<String> readMetaFile(InputStream stream, IPlugin plugin) {
		List<String> aimFiles = new ArrayList<>();
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));

		try {
			String line = null;
			boolean aimsFound = false;
			while ((line = in.readLine()) != null) {
				if (line.matches("#") || line.matches("\\s*"))
					continue;
				if (line.matches(".aims:")) {
					aimsFound = true;
					continue;
				}
				if(aimsFound) {
					aimFiles.add(line);
				}
			}
		} catch (IOException e) {
			this.logger.error("could not read speech meta file of {}", plugin.getDisplayName());
		}
		return aimFiles;

	}

}
