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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLIAnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.PreDefinedEntityTypes;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFLexer;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.AGFParser;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.agf.nodes.AGFNode;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLAIMIntent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLEntityTemplate;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLPrompt;

/**
 * The user intent class load all needed grammars and answers from the xml. the entities were saved here.
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
	private final XMLAIMIntent aimIntent;

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
	public UserIntent(@Nonnull Method method, @Nonnull XMLAIMIntent aimIntent) {
		this.method = method;
		this.partialNLIClass = method.getDeclaringClass();
		this.aimIntent = aimIntent;

		registerEntities();
		this.grammar = parseStringToAGF(this.aimIntent.getGram());
	}

	private void registerEntities() {
		Map<String, AGFNode> customEntities = PreDefinedEntityTypes.getTypes();
		for (Entry<String, AGFNode> e : customEntities.entrySet()) {
			this.entityList.put(e.getKey(), new Entity(e.getKey(), e.getValue(), false));
		}

		for (XMLEntityTemplate xmlEntityTemplate : this.aimIntent.getTemplates()) {
			Entity e = new Entity(xmlEntityTemplate.getEntityId(), parseStringToAGF(xmlEntityTemplate.getGrammar()),
					Boolean.parseBoolean(xmlEntityTemplate.getRequired()));
			this.entityList.put(xmlEntityTemplate.getEntityId(), e);
			e.setMethod(NLIAnnotationReader.getValidEnityProviderMethod(this.partialNLIClass, e.getEntityId()));

		}

		Map<String, Prompt> idToPrompt = new HashMap<>();
		for (XMLPrompt xmlPrompt : this.aimIntent.getPrompts()) {
			idToPrompt.put(xmlPrompt.getEntityTemplateId(),
					new Prompt(parseStringToAGF(xmlPrompt.getGram()), xmlPrompt.getText()));
			Entity e = this.entityList.get(xmlPrompt.getEntityTemplateId());
			e.setPrompt(idToPrompt.get(xmlPrompt.getEntityTemplateId()));
			e.setMethod(NLIAnnotationReader.getValidEnityProviderMethod(this.partialNLIClass, e.getEntityId()));
			this.entityList.replace(xmlPrompt.getEntityTemplateId(), e);
		}

	}

	/**
	 * parse a string to agf
	 *
	 * @param toParse
	 *            String to parse
	 * @return a agf node
	 */
	private AGFNode parseStringToAGF(String toParse) {
		HashMap<String, AGFNode> idToGram = new HashMap<>();
		for (Entry<String, Entity> e : this.entityList.entrySet()) {
			idToGram.put(e.getKey(), e.getValue().getGrammar());
		}

		AGFLexer lex = new AGFLexer(toParse);
		AGFParser parse = new AGFParser(lex, idToGram);
		AGFNode node = parse.parseWholeExpression();

		return node;
	}

	/**
	 * Invoke the method of this partialNLI with an instance of the partialNLIClass
	 *
	 * @param instance
	 *            the instance of the partialNLIClass
	 * @param map
	 *            map of all entities with the id as key
	 * @return the result String from calling the command
	 */
	public String call(Object instance, Map<String, EntityDataImpl> map) {
		Object[] params = { map };
		return NLIAnnotationReader.callNLIMethod(this.method, instance, params);
	}

	/**
	 * tells if this intent is already finished if all entities have been provided by the user
	 *
	 * @return if this intent is finished
	 */
	public boolean isFinished() {
		Map<String, AGFNode> customEntities = PreDefinedEntityTypes.getTypes();

		for (Entity entity : this.entityList.values()) {
			if ((entity.getEntityData() == null && !customEntities.containsKey(entity.getEntityId()))
					&& entity.isRequired()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * get the next prompt to process
	 *
	 * @return the next prompt
	 */
	public Prompt getNextPrompt() {
		if (isFinished()) {
			return null;
		}

		Map<String, AGFNode> customEntities = PreDefinedEntityTypes.getTypes();

		for (Entity entity : this.entityList.values()) {
			if (entity.getEntityData() == null && !customEntities.containsKey(entity.getEntityId()) && entity.isRequired()) {
				return entity.getPrompt();
			}
		}
		throw new IllegalStateException(
				"could not find empty entity but user intent for method " + this.method.getName() + " is not finished");
	}

	/**
	 * Get's {@link #entityList entityList}
	 *
	 * @return entityList
	 */
	public Map<String, Entity> getEntityList() {
		return this.entityList;
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
	 * @param object
	 *            to receive new grammars from
	 */
	public void updateGrammars(Object object) {
		List<Entity> toUpdate = new ArrayList<>();
		for (Entry<String, Entity> entry : this.entityList.entrySet()) {
			if (entry.getValue().getMethod() != null) {
				StringBuilder builder = new StringBuilder();
				List<String> providedEntities = NLIAnnotationReader
						.callNLIGetEntityProviderMethod(entry.getValue().getMethod(), object);
				if (!providedEntities.isEmpty()) {
					builder.append("(").append(providedEntities.get(0));
					for (int i = 1; i < providedEntities.size(); i++) {
						builder.append("|").append(providedEntities.get(i));
					}
					builder.append(")");
				}
				AGFNode node = parseStringToAGF(builder.toString());
				Entity e = entry.getValue();
				e.setGrammar(node);
				toUpdate.add(e);
			}
		}

		for (Entity e : toUpdate) {
			this.entityList.replace(e.getEntityId(), e);
		}

		Map<String, Prompt> idToPrompt = new HashMap<>();
		for (XMLPrompt xmlPrompt : this.aimIntent.getPrompts()) {
			idToPrompt.put(xmlPrompt.getEntityTemplateId(),
					new Prompt(parseStringToAGF(xmlPrompt.getGram()), xmlPrompt.getText()));
			Entity e = this.entityList.get(xmlPrompt.getEntityTemplateId());
			e.setPrompt(idToPrompt.get(xmlPrompt.getEntityTemplateId()));
			this.entityList.replace(xmlPrompt.getEntityTemplateId(), e);
		}
	}
}
