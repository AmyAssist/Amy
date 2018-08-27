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

import java.util.UUID;
import java.util.function.Consumer;

/**
 * entry point for natural language interpretation
 *  
 * @author Lars Buttgereit, Felix Burk
 */
public interface DialogHandler {

	/**
	 * creates a new dialog
	 * 
	 * @param cons
	 *                 callback for consumer
	 * @return the matching uuid
	 */
	UUID createDialog(Consumer<String> cons);

	/**
	 * deletes a save dialog - this should be called once in a while if the core is running for any long amounts of time
	 *
	 * @param uuid
	 *                 of fialog to delete
	 */
	void deleteDialog(UUID uuid);

	/**
	 * processes a dialog from a given uuid with natural language input from a user
	 *
	 * @param naturalLanguageText
	 *                                from user
	 * @param uuid
	 *                                of dialog
	 */
	void process(String naturalLanguageText, UUID uuid);

}