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

package io.github.amyassist.amy.natlang.userinteraction;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.pluginloader.IPlugin;
import io.github.amyassist.amy.core.pluginloader.PluginManager;
import io.github.amyassist.amy.core.service.DeploymentContainerService;
import io.github.amyassist.amy.natlang.NLIAnnotationReader;
import io.github.amyassist.amy.natlang.NLProcessingManager;
import io.github.amyassist.amy.natlang.aim.XMLAIMIntent;
import io.github.amyassist.amy.natlang.aim.XMLAmyInteractionModel;

/**
 * This service loads all amy interaction model xml files and saves them inside an array list, containing
 * AmyInteractionModel instances
 *
 * @author Felix Burk
 */
@Service(LoadAIMService.class)
public class LoadAIMService implements DeploymentContainerService {

	private static final String METAFILENAME = "natlangMeta";

	@Reference
	private PluginManager pluginManager;

	@Reference
	private NLProcessingManager nlManager;

	@Reference
	private Logger logger;

	/**
	 * @see io.github.amyassist.amy.core.service.DeploymentContainerService#deploy()
	 */
	@Override
	public void deploy() {
		List<IPlugin> plugins = this.pluginManager.getPlugins();

		for (IPlugin plugin : plugins) {

			String pathS = "META-INF/" + plugin.getUniqueName() + "." + METAFILENAME;

			try (InputStream stream = plugin.getClassLoader().getResourceAsStream(pathS);) {

				if (stream == null) {
					this.logger.info("could not find aim meta file for plugin {}", plugin.getDisplayName());
					continue;
				}
				// get all aim.xml file names
				List<String> aimFiles = readMetaFile(stream, plugin);

				// extract all intents
				List<XMLAIMIntent> aimIntents = new ArrayList<>();

				for (String s : aimFiles) {
					aimIntents.addAll(extractIntents(plugin, s));
				}

				matchAndRegister(aimIntents, plugin.getClassLoader());

			} catch (IOException e) {
				this.logger.info("stream could not be closed {}", e);
			}

		}

	}

	/**
	 * matches aim intents and methods of speech classes and registeres them inside the NLProcessingManager
	 *
	 * @param aimIntents
	 *                        list of all intents
	 * @param classloader
	 *                        matching classloader
	 */
	private void matchAndRegister(List<XMLAIMIntent> aimIntents, ClassLoader classloader) {
		for (XMLAIMIntent intent : aimIntents) {
			String ref = intent.getReference();
			String fullClassName = ref.substring(0, ref.lastIndexOf('.'));
			String methodName = ref.substring(ref.lastIndexOf('.') + 1, ref.length());

			Class<?> cls;
			try {
				cls = Class.forName(fullClassName, true, classloader);
				Set<Method> set = NLIAnnotationReader.getValidIntentMethods(cls);
				Iterator<Method> i = set.iterator();
				while (i.hasNext()) {
					Method m = i.next();
					if (m.getName().equals(methodName)) {
						this.nlManager.register(m, intent);
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				this.logger.error("class not found {} error {}", fullClassName, e);
			}
		}

	}

	/**
	 * extracts AIMIntents from aim xmls
	 *
	 * @param plugin
	 *                     to load resource from
	 * @param fileName
	 *                     n
	 * @return list of extracted aim intents
	 */
	private List<XMLAIMIntent> extractIntents(IPlugin plugin, String fileName) {
		InputStream stream = plugin.getClassLoader().getResourceAsStream(fileName);

		if (stream != null) {
			try (InputStreamReader inputStream = new InputStreamReader(stream, "UTF-8");
					BufferedReader reader = new BufferedReader(inputStream);) {
				XMLAmyInteractionModel model = extractModel(reader.lines().collect(Collectors.joining()), fileName);
				if (model != null) {
					return model.getIntents();
				}
				this.logger.error("could not read model");
			} catch (EOFException e) {
				this.logger.error("end of file {}", e);
			} catch (IOException e) {
				this.logger.debug("Could not read file!", e);
			}
		}
		this.logger.error("could not read aim file");

		return new ArrayList<>();
	}

	/**
	 * extracts AmyInteractionModel from
	 *
	 * @param xmlContent
	 *                       content of the xml
	 * @param entryName
	 *                       name of the jar entry
	 * @return AmyInteractionModel instance
	 */
	public XMLAmyInteractionModel extractModel(String xmlContent, String entryName) {
		try {
			JAXBContext jc = JAXBContext.newInstance(XMLAmyInteractionModel.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			StringReader reader = new StringReader(xmlContent);
			return (XMLAmyInteractionModel) unmarshaller.unmarshal(reader);
		} catch (JAXBException e1) {
			this.logger.error("Amy Interaction Model for file {} could not be parsed: " + entryName, e1);
		}
		return null;
	}

	/**
	 * simple reader for meta file data
	 *
	 * @param stream
	 *                   input stream of file
	 * @param plugin
	 *                   for debugging purposes
	 * @return list of names from .aim.xml files
	 */
	public List<String> readMetaFile(InputStream stream, IPlugin plugin) {
		List<String> aimFiles = new ArrayList<>();

		try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
				BufferedReader in = new BufferedReader(reader);){
			String line = null;
			boolean aimsFound = false;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("#") || line.trim().isEmpty())
					continue;
				if (aimsFound) 
					aimFiles.add(line);
				if(line.startsWith(".")) {
					if (line.matches(".aims:")) {
						aimsFound = true;
					} else {
						aimsFound = false;
					}
				}
			}
		} catch (IOException e) {
			this.logger.error("could not read speech meta file of {} {}",  plugin.getDisplayName(), e);
		}
		return aimFiles;

	}

}
