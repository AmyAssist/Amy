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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.userinteraction;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLIAnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLAIMIntent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLAmyInteractionModel;
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
			// this stream is gonna be closed - line 92
			@SuppressWarnings("resource")
			InputStream stream = plugin.getClassLoader().getResourceAsStream(pathS);

			if (stream == null) {
				this.logger.info("could not find aim meta file for plugin {}", plugin.getDisplayName());
				continue;
			}
			// get all aim.xml file names
			List<String> aimFiles = readMetaFile(stream, plugin);

			try {
				stream.close();
			} catch (IOException e) {
				this.logger.info("stream could not be closed {}", e);
			}

			// extract all intents
			List<XMLAIMIntent> aimIntents = new ArrayList<>();

			for (String s : aimFiles) {
				aimIntents.addAll(extractIntents(plugin, s));
			}

			matchAndRegister(aimIntents, plugin.getClassLoader());

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
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
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
	private List<String> readMetaFile(InputStream stream, IPlugin plugin) {
		List<String> aimFiles = new ArrayList<>();
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));

		try {
			String line = null;
			boolean aimsFound = false;
			while ((line = in.readLine()) != null) {
				if (line.matches("#") || line.matches("\\s*"))
					continue;
				if (aimsFound) {
					aimFiles.add(line);
				}
				if (line.matches(".aims:")) {
					aimsFound = true;
				}
			}
		} catch (IOException e) {
			this.logger.error("could not read speech meta file of {}: " + plugin.getDisplayName(), e);
		}
		return aimFiles;

	}

}
