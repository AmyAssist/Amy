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

package de.unistuttgart.iaas.amyassist.amy.core.di;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * This class name is used as deployment descriptor name.
 * 
 * @author Leon Kiefer
 */
public class Services {
	private static final String SERVICE_DEPLOYMENT_DESCRIPTOR = "META-INF/" + Services.class.getName();

	/**
	 * Loads Services using the deployment descriptor file
	 * META-INF/de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService
	 * 
	 * @param classLoader
	 *            the classLoader from which the deployment descriptor is read is used to load the classes
	 * @return the set of loaded classes
	 */
	public Set<Class<?>> loadServices(ClassLoader classLoader) {
		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(SERVICE_DEPLOYMENT_DESCRIPTOR);
		} catch (IOException e) {
			throw new IllegalStateException("Could not read the Service deployment descriptor", e);
		}
		Set<Class<?>> classes = new HashSet<>();
		while (resources.hasMoreElements()) {
			try (InputStream resourceAsStream = resources.nextElement().openStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))) {
				String className;
				while ((className = reader.readLine()) != null) {
					if (className.isEmpty() || className.startsWith("#")) {
						continue;
					}
					classes.add(this.loadClass(className, classLoader));
				}
			} catch (IOException e) {
				throw new IllegalStateException("Could not read the Service deployment descriptor", e);
			}
		}
		return classes;
	}

	/**
	 * @param className
	 *            the name of the class to load
	 * @param classLoader
	 *            the classLoader to load the class from
	 * @return the class with the given name loaded with the classLoader
	 * @throws IllegalArgumentException
	 *             if the class could not be loaded
	 */
	private Class<?> loadClass(String className, ClassLoader classLoader) {
		try {
			return Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"could not load class " + className + " with the given ClassLoader " + classLoader, e);
		}
	}
}
