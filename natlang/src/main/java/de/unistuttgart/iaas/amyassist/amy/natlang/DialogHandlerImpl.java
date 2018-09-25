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

package de.unistuttgart.iaas.amyassist.amy.natlang;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.Response;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.DialogHandler;
import de.unistuttgart.iaas.amyassist.amy.natlang.userinteraction.EntityDataImpl;
import de.unistuttgart.iaas.amyassist.amy.natlang.userinteraction.UserIntent;

/**
 * Handles different dialog tasks
 *
 * @author Felix Burk
 */
@Service
public class DialogHandlerImpl implements DialogHandler {

	@Reference
	private NLProcessingManager manager;

	@Reference
	private ServiceLocator serviceLocator;

	@Reference
	private Logger logger;

	/**
	 * internal map of dialogs with corresponding uuid
	 */
	private Map<UUID, Dialog> map = new HashMap<>();

	@Override
	public UUID createDialog(Consumer<Response> cons) {
		UUID uuid = UUID.randomUUID();
		this.map.put(uuid, new Dialog(cons));
		return uuid;
	}

	@Override
	public void deleteDialog(UUID uuid) {
		this.map.remove(uuid);
	}

	@Override
	public void process(String naturalLanguageText, UUID uuid) {
		if (!this.map.containsKey(uuid))
			throw new IllegalArgumentException("wrong UUID");

		Dialog dialog = this.map.get(uuid);
		UserIntent intent = dialog.getIntent();

		if (intent == null) {
			dialog = this.manager.decideIntent(dialog, naturalLanguageText);
			intent = dialog.getIntent();
		} else {

			this.manager.processIntent(dialog, naturalLanguageText);
		}

		// is the intent ready for the plugin?
		if (intent != null && dialog.getIntent() != null && dialog.getIntent().isFinished()) {
			Map<String, EntityDataImpl> stringToEntityData = new HashMap<>();

			for (String s : dialog.getIntent().getEntityList().keySet()) {
				stringToEntityData.put(s, intent.getEntityList().get(s).getEntityData());
			}
			if (intent.getPartialNLIClass() != null) {
				Object object = this.serviceLocator.createAndInitialize(intent.getPartialNLIClass());
				try {
					dialog.output(dialog.getIntent().call(object, stringToEntityData));
				} catch (RuntimeException e) { // NOSONAR
					this.logger.warn("Error during plugin execution", e);
					dialog.output("There is an error in the plugin. For more details see in the logs.");
				}
			}
			dialog.setIntent(null);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.natlang.DialogHandler#hasDialogUnfinishedIntent(java.util.UUID)
	 */
	@Override
	public boolean hasDialogUnfinishedIntent(UUID uuid) {
		if (!this.map.containsKey(uuid))
			throw new IllegalArgumentException("wrong UUID");

		UserIntent intent = this.map.get(uuid).getIntent();

		return (intent != null && !intent.isFinished());
	}

}
