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

import java.io.File;
import java.util.ArrayList;
import java.util.jar.Manifest;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;

/**
 * For testing DI.
 * 
 * @author Tim Neumann
 */
public class TestPlugin implements IPlugin {

	private ArrayList<Class<?>> classes;

	/**
	 * New Test Plugin
	 * 
	 * @param p_classes
	 *            classes
	 */
	public TestPlugin(ArrayList<Class<?>> p_classes) {
		this.classes = p_classes;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getFile()
	 */
	@Override
	public File getFile() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getUniqueName()
	 */
	@Override
	public String getUniqueName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getVersion()
	 */
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getClasses()
	 */
	@Override
	public ArrayList<Class<?>> getClasses() {
		return this.classes;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getManifest()
	 */
	@Override
	public Manifest getManifest() {
		// TODO Auto-generated method stub
		return null;
	}

}
