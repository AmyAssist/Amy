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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

@Service(ConfigLoader.class)
public class ConfigLoader {

	@Reference
	private Logger logger;
	private Properties p;
	private static final String FILE_PATH = "apikeys/spotify_config.properties";

	/**
	 * this class save a config file with different keys
	 */
	@PostConstruct
	public void init() {
		p = new Properties();

	}

	/**
	 * get a value with the given key
	 * 
	 * @param s
	 * @return
	 */
	public String get(String s) {
		try {
			p.load(new FileReader(FILE_PATH));
			return p.getProperty(s);
		} catch (IOException e) {
			logger.warn("Error loading config file for spotify plugin");
			return null;
		}
	}

	/**
	 * set a value with the given key
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		p.setProperty(key, value);
		try {
			p.store(new FileWriter(FILE_PATH), "Spotify Plugin");
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
}
