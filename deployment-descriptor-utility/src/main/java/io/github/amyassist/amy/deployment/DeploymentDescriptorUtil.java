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

package io.github.amyassist.amy.deployment;

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
 * Utility class for reading deployment descriptors and get the classes.
 * 
 * @author Leon Kiefer
 */
public class DeploymentDescriptorUtil {

	private DeploymentDescriptorUtil() {
		// hide constructor
	}

	/**
	 * Get all classes from the deployment descriptor using the given {@link ClassLoader}.
	 * 
	 * @param classLoader
	 *            the ClassLoader to use
	 * @param deploymentDescriptor
	 *            the path of the deployment descriptor from which to read the class names
	 * @return all classes specified in the deployment descriptor from the ClassLoader
	 */
	public static Set<Class<?>> getClasses(ClassLoader classLoader, String deploymentDescriptor) {
		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(deploymentDescriptor);
		} catch (IOException e) {
			throw new IllegalStateException("Could not read the deployment descriptor", e);
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
					classes.add(getClass(className, classLoader));
				}
			} catch (IOException e) {
				throw new IllegalStateException("Could not read the deployment descriptor", e);
			}
		}
		return classes;
	}

	/**
	 * @param className
	 *            the name of the class
	 * @param classLoader
	 *            the classLoader to load the class from
	 * @return the class with the given name from the classLoader
	 * @throws IllegalArgumentException
	 *             if the class could not be loaded
	 */
	private static Class<?> getClass(String className, ClassLoader classLoader) {
		try {
			return Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"Could not load class " + className + " with the given ClassLoader " + classLoader, e);
		}
	}
}
