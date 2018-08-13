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
 * Manages multiple Dialogs from different sources.
 * 
 * @author Leon Kiefer, Felix Burk
 */
public interface DialogHandler {
	/**
	 * 
	 * @param naturalLanguageText
	 * @param uuid
	 *            the uuid of the dialog for which to process the input
	 */
	void process(String naturalLanguageText, UUID uuid);

	/**
	 * Create a new Dialog with a given output handler. The Dialog is the Session that stores all information of the
	 * long running user interaction.
	 * 
	 * @param outputHandler
	 *            the output handler that is responsible to deliver the given natural language output to the user
	 * @return the unique id of the dialog, this must be passed to {@link #process(String, UUID)} to process input of
	 *         that dialog
	 */
	UUID createDialog(Consumer<String> outputHandler);

}