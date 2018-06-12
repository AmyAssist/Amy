/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Scope;

/**
 * This class represents information about a scope
 * 
 * @author Tim Neumann
 */
public class ScopeInformation {
	private Scope scope;
	private Class<?> cls;
	private IPlugin plugin;

	/**
	 * Creates a new scope information with the given scope
	 * 
	 * Don't use this for the scopes class or plugin, because additional
	 * information is required
	 * 
	 * @param p_scope
	 *            The scope for this information
	 */
	public ScopeInformation(Scope p_scope) {
		this.scope = p_scope;
	}

	/**
	 * Creates a new scope information for the scope class
	 * 
	 * @param p_cls
	 *            The class for the scope.
	 */
	public ScopeInformation(Class<?> p_cls) {
		this.scope = Scope.CLASS;
		this.cls = p_cls;
	}

	/**
	 * Creates a new scope information for the scope plugin
	 * 
	 * @param p_plugin
	 *            The plugin for the scope
	 */
	public ScopeInformation(IPlugin p_plugin) {
		this.scope = Scope.PLUGIN;
		this.plugin = p_plugin;
	}

	/**
	 * Get's the scope
	 * 
	 * @return scope
	 */
	public Scope getScope() {
		return this.scope;
	}

	/**
	 * Get's the class of the scope if the scope is class
	 * 
	 * @return the class or null if the scope is not class
	 */
	public Class<?> getCls() {
		return this.cls;
	}

	/**
	 * Get's the plugin or the scope of the scope is plugin
	 * 
	 * @return the plugin or null if the scope is not plugin
	 */
	public IPlugin getPlugin() {
		return this.plugin;
	}
}
