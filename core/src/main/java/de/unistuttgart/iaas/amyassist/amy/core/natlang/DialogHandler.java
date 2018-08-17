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

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * TODO: Description
 * 
 * @author Felix Burk
 */
@Service()
public class DialogHandler {

	@Reference
	private NLProcessingManager manager;

	@Reference
	private ServiceLocator serviceLocator;

	Map<UUID, DialogImpl> map = new HashMap<>();

	/**
	 * 
	 * @return
	 */
	public UUID createDialog(Consumer<String> cons) {
		UUID uuid = UUID.randomUUID();
		map.put(uuid, new DialogImpl(cons));
		return uuid;
	}

	public void process(String naturalLanguageText, UUID uuid) {
		if (!this.map.containsKey(uuid)) {
			throw new IllegalArgumentException("wrong UUID");
		}

		DialogImpl dialog = map.get(uuid);

		if (dialog.getIntent() == null) {
			this.manager.decideIntent(dialog, naturalLanguageText);
		} else {
			this.manager.processIntent(dialog, naturalLanguageText);
		}

		// is the intent ready for the plugin?
		if (dialog.getIntent().isFinished()) {
			Object object = this.serviceLocator.createAndInitialize(dialog.getIntent().getPartialNLIClass());
			dialog.output(dialog.getIntent().call(object, naturalLanguageText));
			dialog.setIntent(null);
		}
	}
}