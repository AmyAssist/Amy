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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.userInteraction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLIAnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.AIMIntent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLEntityTemplate;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLPrompt;

/**
 * TODO: Description
 * 
 * @author Lars Buttgereit, Felix Burk
 */
public class UserIntent {
	@Nonnull
	private final Method method;
	@Nonnull
	private AGFNode grammar;
	@Nonnull
	private final Class<?> partialNLIClass;
	@Nonnull
	private final AIMIntent aimIntent;

	/**
	 * internal list of all entities
	 */
	private Map<String, Entity> entityList = new HashMap<>();
	/**
	 * internal lists of possible prompts to receive missing entities
	 */
	private List<Prompt> prompts = new ArrayList<>();

	/**
	 * Represents an intent of a user
	 * 
	 * @param method
	 *            plugin method to call
	 * @param aimIntent
	 *            corresponding aimintent from xml
	 */
	public UserIntent(@Nonnull Method method, @Nonnull AIMIntent aimIntent) {
		this.method = method;
		this.partialNLIClass = method.getDeclaringClass();
		this.aimIntent = aimIntent;
		this.grammar = parseStringToAGF(this.aimIntent.getGram());
		setPrompts();
		setEntity();
	}

	private AGFNode parseStringToAGF(String toParse) {
		AGFLexer lex = new AGFLexer(toParse);
		AGFParser parse = new AGFParser(lex);
		return parse.parseWholeExpression();
	}

	private void setEntity() {
		for (XMLEntityTemplate xmlEntityTemplate : this.aimIntent.getTemplates()) {
			this.entityList.put(xmlEntityTemplate.getEntityId(), new Entity(xmlEntityTemplate.getType(),
					xmlEntityTemplate.getEntityId(), xmlEntityTemplate.getValues(), getAMatcher(xmlEntityTemplate)));
		}
	}

	private IMatcher getAMatcher(XMLEntityTemplate entityTemplate) {
		// TODO replace with regex
		if (entityTemplate.getType().equals("string")) {
			return new StringMatcher(entityTemplate.getValues());
		}
		return null;
	}

	private void setPrompts() {
		for (XMLPrompt xmlPrompt : this.aimIntent.getPrompts()) {
			this.prompts.add(new Prompt(xmlPrompt.getEntityTemplateId(), parseStringToAGF(xmlPrompt.getGram()),
					xmlPrompt.getText()));
		}
	}

	/**
	 * Get's the partialNLI class
	 * 
	 * @return partialNLIClass
	 */
	public Class<?> getPartialNLIClass() {
		return this.partialNLIClass;
	}

	/**
	 * Get's {@link #grammar grammar}
	 * 
	 * @return grammar
	 */
	public AGFNode getGrammar() {
		return this.grammar;
	}

	/**
	 * Invoke the method of this partialNLI with an instance of the partialNLIClass
	 * 
	 * @param instance
	 *            the instance of the partialNLIClass
	 * @param input
	 *            the natural language text splitted into words
	 * @return the result String from calling the command
	 */
	public String call(Object instance, String... input) {
		Object[] params = { input };
		return NLIAnnotationReader.callNLIMethod(this.method, instance, params);
	}

	/**
	 * tells if this intent is already finished if all entities have been provided by the user
	 * @return if this intent is finished
	 */
	public boolean isFinished() {
		for (Entity entity : this.entityList.values()) {
			if (entity.getEntityData() == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get's {@link #prompts prompts}
	 * 
	 * @return prompts
	 */
	public List<Prompt> getPrompts() {
		return this.prompts;
	}

	/**
	 * Get's {@link #entityList entityList}
	 * @return  entityList
	 */
	public Map<String, Entity> getEntityList() {
		return this.entityList;
	}
}
