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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLIAnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.PreDefinedEntityTypes;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.EntityNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.AIMIntent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLEntityTemplate;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLPrompt;

/**
 * TODO: Description
 * 
 * @author Lars Buttgereit, Felix Burk
 */
public class UserIntent {

	@Reference
	private Logger logger = LoggerFactory.getLogger(UserIntent.class);

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
		setEntity();
		this.grammar = parseStringToAGF(this.aimIntent.getGram());
		setPromptsInEntity();
	}

	/**
	 * generates amys answers
	 * 
	 * @return string of amys answer
	 */
	public String generateQuestion() {
		if (this.isFinished()) {
			return null;
		}

		for (String s : this.entityList.keySet()) {
			if (this.entityList.get(s).getEntityData() == null) {
				return this.entityList.get(s).getPrompt().getOutputText();
			}
		}

		throw new IllegalStateException(
				"could not find empty entity but user intent for method " + this.method.getName() + " is not finished");
	}

	private AGFNode parseStringToAGF(String toParse) {
		Map<String, AGFNode> customEntities = PreDefinedEntityTypes.getTypes();
		for (Entity entity : this.entityList.values()) {
			customEntities.put(entity.getEntityId(), entity.getGrammar());
		}
		AGFLexer lex = new AGFLexer(toParse);
		AGFParser parse = new AGFParser(lex, customEntities);
		AGFNode node = parse.parseWholeExpression();

		List<EntityNode> entityNodes = node.getChildEntityNodes();
		for (EntityNode entity : entityNodes) {
			this.entityList.put(entity.getContent(),
					new Entity(entity.getContent(), customEntities.get(entity.getContent())));
		}
		return node;
	}

	private void setEntity() {
		for (XMLEntityTemplate xmlEntityTemplate : this.aimIntent.getTemplates()) {
			AGFNode node = parseStringToAGF(xmlEntityTemplate.getGrammar());
			Entity entity = new Entity(xmlEntityTemplate.getEntityId(), node);
			this.entityList.put(xmlEntityTemplate.getEntityId(), entity);
		}
	}

	private void setPromptsInEntity() {
		for (XMLPrompt xmlPrompt : this.aimIntent.getPrompts()) {
			if (this.entityList.get(xmlPrompt.getEntityTemplateId()) != null) {
				Entity entity = this.entityList.get(xmlPrompt.getEntityTemplateId());
				entity.setPrompt(new Prompt(parseStringToAGF(xmlPrompt.getGram()), xmlPrompt.getText()));
				this.entityList.replace(xmlPrompt.getEntityTemplateId(), entity);
			} else {

			}
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
	 * 
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
	 * Get's {@link #entityList entityList}
	 * 
	 * @return entityList
	 */
	public Map<String, Entity> getEntityList() {
		return this.entityList;
	}
}
